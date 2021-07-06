package de.onvif.soap;

import com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.oasis_open.docs.wsn.b_2.Renew;
import org.oasis_open.docs.wsn.b_2.Unsubscribe;
import org.onvif.ver10.events.wsdl.CreatePullPointSubscription;
import org.onvif.ver10.events.wsdl.PullMessages;
import org.springframework.security.authentication.BadCredentialsException;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.w3c.dom.Document;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Log4j2
public class SOAP implements BiConsumer<String, Integer> {

    public static final DatatypeFactory DATATYPE_FACTORY = new DatatypeFactoryImpl();

    @Setter
    private boolean logging = Boolean.parseBoolean(System.getProperty("soap.debug", "false"));

    private final OnvifDeviceState onvifDeviceState;
    private Bootstrap bootstrap;
    private EventLoopGroup mainEventLoopGroup = new NioEventLoopGroup();
    private final Map<String, AsyncClassListener> asyncSoapMessageToType = new HashMap<>();

    public SOAP(OnvifDeviceState onvifDeviceState) {
        this.onvifDeviceState = onvifDeviceState;
    }

    public static <T> T parseMessage(Class<T> responseClass, String message) {
        try {
            SOAPMessage soapResponse = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL)
                    .createMessage(null, new ByteArrayInputStream(message.getBytes()));
            Unmarshaller unmarshaller = JAXBContext.newInstance(responseClass).createUnmarshaller();
            return (T) unmarshaller.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument());
        } catch (Exception e) {
            log.warn("Could not unmarshal, ended in SOAP fault.");
        }
        return null;
    }

    @SneakyThrows
    public Object createSOAPDeviceRequest(Object soapRequestElem, Class soapResponseClass) {
        return createSOAPRequest(soapRequestElem, soapResponseClass, onvifDeviceState.getServerDeviceUri(),
                onvifDeviceState.getServerDeviceIpLessUri());
    }

    @SneakyThrows
    public <T> T createSOAPPtzRequest(Object soapRequestElem, Class<T> soapResponseClass) {
        return createSOAPRequest(soapRequestElem, soapResponseClass, onvifDeviceState.getServerPtzUri(),
                onvifDeviceState.getServerPtzIpLessUri());
    }

    @SneakyThrows
    public <T> T createSOAPMediaRequest(Object soapRequestElem, Class<T> soapResponseClass) {
        return createSOAPRequest(soapRequestElem, soapResponseClass, onvifDeviceState.getServerMediaUri(),
                onvifDeviceState.getServerMediaIpLessUri());
    }

    @SneakyThrows
    public <T> T createSOAPImagingRequest(Object soapRequestElem, Class<T> soapResponseClass) {
        return createSOAPRequest(soapRequestElem, soapResponseClass, onvifDeviceState.getServerImagingUri(),
                onvifDeviceState.getServerImagingIpLessUri());
    }

    @SneakyThrows
    public Object createSOAPEventsRequest(Object soapRequestElem, Class soapResponseClass) {
        return createSOAPRequest(soapRequestElem, soapResponseClass, onvifDeviceState.getServerEventsUri(),
                onvifDeviceState.getServerEventsIpLessUri());
    }

    @SneakyThrows
    public void sendSOAPEventRequestAsync(Object soapRequestElem) {
        sendSOAPRequestAsync(soapRequestElem, onvifDeviceState.getServerEventsIpLessUri());
    }

    @SneakyThrows
    public void sendSOAPSubscribeRequestAsync(Object soapRequestElem) {
        sendSOAPRequestAsync(soapRequestElem, onvifDeviceState.getSubscriptionIpLessUri());
    }

    @Override
    @SneakyThrows
    public void accept(String message, Integer code) {
        log.debug("Onvif {} reply is:{}", code, message);
        boolean handled = false;
        for (Map.Entry<String, AsyncClassListener> entry : asyncSoapMessageToType.entrySet()) {
            if (message.contains(entry.getKey())) {
                handled = true;
                Object soapResponseElem = parseMessage(entry.getValue().responseClass, message);
                if (soapResponseElem != null) {
                    for (Consumer<Object> consumer : entry.getValue().handlers.values()) {
                        consumer.accept(soapResponseElem);
                    }
                }
            }
        }
        if (!handled) {
            if (code != 200) {
                log.error("Accepted not expected onvif soap message with code: <{}>. Msg: <{}>", code, message);
            } else {
                log.warn("Accepted not expected onvif soap message with code: <{}>. Msg: <{}>", code, message);
            }
        }
    }

    private void sendSOAPRequestAsync(Object soapRequestElem, String xAddr) {
        HttpRequest httpRequest = soapRequestToHttpRequest(soapRequestElem, xAddr);
        if (bootstrap == null) {
            bootstrap = new Bootstrap();
            bootstrap.group(mainEventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
            bootstrap.option(ChannelOption.SO_SNDBUF, 1024 * 8);
            bootstrap.option(ChannelOption.SO_RCVBUF, 1024 * 1024);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast("idleStateHandler", new IdleStateHandler(0, 0, 70));
                    socketChannel.pipeline().addLast("HttpClientCodec", new HttpClientCodec());
                    socketChannel.pipeline().addLast("OnvifCodec", new OnvifCodec(SOAP.this));
                }
            });
        }
        bootstrap.connect(new InetSocketAddress(onvifDeviceState.getIp(), onvifDeviceState.getOnvifPort()))
                .addListener((ChannelFutureListener) future -> {
                    if (future == null) {
                        return;
                    }
                    if (future.isDone() && future.isSuccess()) {
                        Channel ch = future.channel();
                        ch.writeAndFlush(httpRequest);
                    } else {
                        onvifDeviceState.cameraUnreachable(future.cause() == null ? "" : TouchHomeUtils.getErrorMessage(future.cause()));
                    }
                });
    }

    @SneakyThrows
    private HttpRequest soapRequestToHttpRequest(Object soapRequestElem, String xAddr) {
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, new HttpMethod("POST"), xAddr);
        SOAPMessage soapMessage = createSoapMessage(soapRequestElem, xAddr);
        String deviceRequestType = soapRequestElem.getClass().getDeclaredAnnotation(XmlRootElement.class).name();
        String actionString = soapMessage.getSOAPBody().getFirstChild().getNamespaceURI();
        request.headers().add("Content-Type",
                "application/soap+xml; charset=utf-8; action=\"" + actionString + "/" + deviceRequestType + "\"");
        request.headers().add("Charset", "utf-8");
        if (onvifDeviceState.getOnvifPort() != 80) {
            request.headers().set(HttpHeaderNames.HOST, onvifDeviceState.getIp() + ":" + onvifDeviceState.getOnvifPort());
        } else {
            request.headers().set(HttpHeaderNames.HOST, onvifDeviceState.getIp());
        }
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, "gzip, deflate");


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        soapMessage.writeTo(outputStream);
        String fullXml = outputStream.toString();
        if (logging) {
            log.info("Request async SOAP Message (" + soapRequestElem.getClass().getSimpleName() + "): ");
            log.info("{}", fullXml);
        }

        request.headers().add("SOAPAction", "\"" + actionString + "/" + deviceRequestType + "\"");
        ByteBuf byteBuf = Unpooled.copiedBuffer(fullXml, StandardCharsets.UTF_8);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        request.content().clear().writeBytes(byteBuf);
        return request;
    }

    /**
     * @return SOAP Response Element
     */
    public <T> T createSOAPRequest(Object soapRequestElem, Class<T> soapResponseClass, String soapUri, String xAddr)
            throws IOException, SOAPException, JAXBException, ParserConfigurationException {
        SOAPConnection soapConnection = null;
        SOAPMessage soapResponse = null;

        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();

            SOAPMessage soapMessage = createSoapMessage(soapRequestElem, xAddr);

            // Print the request message
            if (logging) {
                log.info("Request sync SOAP Message (" + soapRequestElem.getClass().getSimpleName() + "): ");
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                soapMessage.writeTo(bout);
                log.info("{}", bout.toString());
            }

            soapResponse = soapConnection.call(soapMessage, soapUri);

            // print SOAP Response
            if (logging) {
                log.info("Response sync SOAP Message (" + soapResponseClass.getSimpleName() + "): ");
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                soapResponse.writeTo(bout);
                log.info("{}", bout.toString());
            }

            if (soapResponse.getSOAPBody().getFault() != null) {
                Iterator<?> iterator = soapResponse.getSOAPBody().getFault().getFaultSubcodes();
                if (iterator.hasNext()) {
                    String error = ((QName) iterator.next()).getLocalPart();
                    if ("NotAuthorized".equals(error)) {
                        throw new BadCredentialsException("Wrong credential to authorize access to soap URI: " + soapUri);
                    }
                    throw new RuntimeException("Unknown fault <" + error + "> during access to soap URI: " + soapUri);
                }
            }

            Object soapResponseElem = null;
            Unmarshaller unmarshaller = JAXBContext.newInstance(soapResponseClass).createUnmarshaller();
            Document document = soapResponse.getSOAPBody().extractContentAsDocument();
            try {
                soapResponseElem = unmarshaller.unmarshal(document);
            } catch (UnmarshalException e) {
                onvifDeviceState.getLogger().warn("Could not unmarshal, ended in SOAP fault.");
            }

            return (T) soapResponseElem;
        } catch (SocketException e) {
            throw new ConnectException(e.getMessage());
        } catch (SOAPException e) {
            onvifDeviceState.getLogger().error(
                    "Unexpected response. Response should be from class " + soapResponseClass + ", but response is: " + soapResponse);
            throw e;
        } catch (ParserConfigurationException | JAXBException | IOException e) {
            onvifDeviceState.getLogger().error("Unhandled exception: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (soapConnection != null) {
                    soapConnection.close();
                }
            } catch (SOAPException ignored) {
            }
        }
    }

    protected SOAPMessage createSoapMessage(Object soapRequestElem, String xAddr) throws SOAPException, ParserConfigurationException,
            JAXBException {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = messageFactory.createMessage();

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Marshaller marshaller = JAXBContext.newInstance(soapRequestElem.getClass()).createMarshaller();
        marshaller.marshal(soapRequestElem, document);
        soapMessage.getSOAPBody().addDocument(document);

        createSoapHeader(soapMessage, xAddr);

        soapMessage.saveChanges();
        return soapMessage;
    }

    protected void createSoapHeader(SOAPMessage soapMessage, String xAddr) throws SOAPException {
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

            String deviceRequestType = soapMessage.getSOAPBody().getFirstChild().getLocalName();
            if (deviceRequestType.equals(CreatePullPointSubscription.class.getSimpleName()) ||
                    deviceRequestType.equals(PullMessages.class.getSimpleName()) ||
                    deviceRequestType.equals(Renew.class.getSimpleName()) ||
                    deviceRequestType.equals(Unsubscribe.class.getSimpleName())) {

                se.addNamespaceDeclaration("wsa5", "http://www.w3.org/2005/08/addressing");
                SOAPElement to = header.addChildElement("To", "wsa5");
                to.setTextContent("http://" + onvifDeviceState.getIp() + xAddr);
                to.setAttribute("env:mustUnderstand", "1");
            }
        }
    }

    public <T> void addAsyncListener(Class<T> responseClass, String key, Consumer<T> consumer) {
        asyncSoapMessageToType.putIfAbsent(responseClass.getSimpleName(), new AsyncClassListener(responseClass));
        asyncSoapMessageToType.get(responseClass.getSimpleName()).handlers.put(key, (Consumer<Object>) consumer);
    }

    public void dispose() {
        if (bootstrap != null) {
            if (!mainEventLoopGroup.isShutdown()) {
                try {
                    mainEventLoopGroup.awaitTermination(3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.info("Onvif was not shutdown correctly due to being interrupted");
                } finally {
                    mainEventLoopGroup = new NioEventLoopGroup();
                    bootstrap = null;
                }
            }
        }
    }

    @RequiredArgsConstructor
    private final class AsyncClassListener {
        private final Class<?> responseClass;
        private Map<String, Consumer<Object>> handlers = new HashMap<>();
    }

    public static String removeIpFromUrl(String url) {
        int index = url.indexOf("//");
        if (index != -1) {// now remove the :port
            index = url.indexOf("/", index + 2);
        }
        if (index == -1) {
            log.debug("We hit an issue parsing url:{}", url);
            return "";
        }
        return url.substring(index);
    }
}
