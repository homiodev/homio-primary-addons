package de.onvif.soap;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Document;

import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;

@Log4j2
@RequiredArgsConstructor
public class SOAP {

    @Setter
    private boolean logging = false;

    private final OnvifDeviceState onvifDeviceState;

    public Object createSOAPDeviceRequest(Object soapRequestElem, Object soapResponseElem) throws SOAPException,
            ConnectException {
        return createSOAPRequest(soapRequestElem, soapResponseElem, onvifDeviceState.getDeviceUri());
    }

    public Object createSOAPPtzRequest(Object soapRequestElem, Object soapResponseElem) throws SOAPException, ConnectException {
        return createSOAPRequest(soapRequestElem, soapResponseElem, onvifDeviceState.getPtzUri());
    }

    public Object createSOAPMediaRequest(Object soapRequestElem, Object soapResponseElem) throws SOAPException, ConnectException {
        return createSOAPRequest(soapRequestElem, soapResponseElem, onvifDeviceState.getMediaUri());
    }

    public Object createSOAPImagingRequest(Object soapRequestElem, Object soapResponseElem) throws SOAPException,
            ConnectException {
        return createSOAPRequest(soapRequestElem, soapResponseElem, onvifDeviceState.getImagingUri());
    }

    public Object createSOAPEventsRequest(Object soapRequestElem, Object soapResponseElem) throws SOAPException,
            ConnectException {
        return createSOAPRequest(soapRequestElem, soapResponseElem, onvifDeviceState.getEventsUri());
    }

    /**
     * @param soapResponseElem Answer object for SOAP request
     * @return SOAP Response Element
     */
    public Object createSOAPRequest(Object soapRequestElem, Object soapResponseElem, String soapUri) throws ConnectException,
            SOAPException {
        SOAPConnection soapConnection = null;
        SOAPMessage soapResponse = null;

        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();

            SOAPMessage soapMessage = createSoapMessage(soapRequestElem);

            // Print the request message
            if (isLogging()) {
                log.info("Request SOAP Message (" + soapRequestElem.getClass().getSimpleName() + "): ");
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                soapMessage.writeTo(bout);
                log.info("{}", bout.toString());
            }

            soapResponse = soapConnection.call(soapMessage, soapUri);

            // print SOAP Response
            if (isLogging()) {
                log.info("Response SOAP Message (" + soapResponseElem.getClass().getSimpleName() + "): ");
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                soapResponse.writeTo(bout);
                log.info("{}", bout.toString());
            }

            if (soapResponseElem == null) {
                throw new NullPointerException("Improper SOAP Response Element given (is null).");
            }

            Unmarshaller unmarshaller = JAXBContext.newInstance(soapResponseElem.getClass()).createUnmarshaller();
            try {
                try {
                    soapResponseElem = unmarshaller.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument());
                } catch (SOAPException e) {
                    // Second try for SOAP 1.2
                    // Sorry, I don't know why it works, it just does o.o
                    soapResponseElem = unmarshaller.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument());
                }
            } catch (UnmarshalException e) {
                // Fault soapFault = (Fault)
                // unmarshaller.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument());
                onvifDeviceState.getLogger().warn("Could not unmarshal, ended in SOAP fault.");
                // throw new SOAPFaultException(soapFault);
            }

            return soapResponseElem;
        } catch (SocketException e) {
            throw new ConnectException(e.getMessage());
        } catch (SOAPException e) {
            onvifDeviceState.getLogger().error(
                    "Unexpected response. Response should be from class " + soapResponseElem.getClass() + ", but response is: " + soapResponse);
            throw e;
        } catch (ParserConfigurationException | JAXBException | IOException e) {
            onvifDeviceState.getLogger().error("Unhandled exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                soapConnection.close();
            } catch (SOAPException ignored) {
            }
        }
    }

    protected SOAPMessage createSoapMessage(Object soapRequestElem) throws SOAPException, ParserConfigurationException,
            JAXBException {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = messageFactory.createMessage();

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Marshaller marshaller = JAXBContext.newInstance(soapRequestElem.getClass()).createMarshaller();
        marshaller.marshal(soapRequestElem, document);
        soapMessage.getSOAPBody().addDocument(document);

        createSoapHeader(soapMessage);

        soapMessage.saveChanges();
        return soapMessage;
    }

    protected void createSoapHeader(SOAPMessage soapMessage) throws SOAPException {
        onvifDeviceState.createNonce();
        String encrypedPassword = onvifDeviceState.getEncryptedPassword();
        if (encrypedPassword != null && onvifDeviceState.getUsername() != null) {

            SOAPPart sp = soapMessage.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPHeader header = soapMessage.getSOAPHeader();
            se.addNamespaceDeclaration("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
            se.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

            SOAPElement securityElem = header.addChildElement("Security", "wsse");
            // securityElem.setAttribute("SOAP-ENV:mustUnderstand", "1");

            SOAPElement usernameTokenElem = securityElem.addChildElement("UsernameToken", "wsse");

            SOAPElement usernameElem = usernameTokenElem.addChildElement("Username", "wsse");
            usernameElem.setTextContent(onvifDeviceState.getUsername());

            SOAPElement passwordElem = usernameTokenElem.addChildElement("Password", "wsse");
            passwordElem.setAttribute("Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest");
            passwordElem.setTextContent(encrypedPassword);

            SOAPElement nonceElem = usernameTokenElem.addChildElement("Nonce", "wsse");
            nonceElem.setAttribute("EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
            nonceElem.setTextContent(onvifDeviceState.getEncryptedNonce());

            SOAPElement createdElem = usernameTokenElem.addChildElement("Created", "wsu");
            createdElem.setTextContent(onvifDeviceState.getLastUTCTime());
        }
    }

    public boolean isLogging() {
        return logging;
    }
}
