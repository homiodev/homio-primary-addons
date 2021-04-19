package de.onvif.soap.devices;

import de.onvif.soap.OnvifDeviceState;
import de.onvif.soap.SOAP;
import lombok.extern.log4j.Log4j2;
import org.oasis_open.docs.wsn.b_2.*;
import org.onvif.ver10.events.wsdl.*;
import org.w3._2005._08.addressing.AttributedURIType;
import org.w3._2005._08.addressing.EndpointReferenceType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Log4j2
public class EventDevices {

    private final OnvifDeviceState onvifDeviceState;
    private final SOAP soap;
    private final Map<String, BiConsumer<String, String>> eventHandlers = new HashMap<>();
    private GetEventPropertiesResponse eventProperties;

    public void dispose() {
        if (eventProperties != null) {
            soap.sendSOAPEventRequestAsync(new Unsubscribe());
            eventProperties = null;
        }
    }

    public EventDevices(OnvifDeviceState onvifDeviceState, SOAP soap) {
        this.onvifDeviceState = onvifDeviceState;
        this.soap = soap;

        soap.addAsyncListener(GetEventPropertiesResponse.class, "listen-GetEventPropertiesResponse", response -> {
            this.eventProperties = response;
            soap.sendSOAPEventRequestAsync(new CreatePullPointSubscription());
        });

        soap.addAsyncListener(CreatePullPointSubscriptionResponse.class, "listen-CreatePullPointSubscriptionResponse", pullPointResponse -> {
            onvifDeviceState.setSubscriptionUri(pullPointResponse.getSubscriptionReference().getAddress().getValue());
            soap.sendSOAPSubscribeRequestAsync(new PullMessages());
        });

        soap.addAsyncListener(PullMessagesResponse.class, "listen-PullMessagesResponse", this::handleEventReceived);
        soap.addAsyncListener(RenewResponse.class, "listen-RenewResponse", response -> soap.sendSOAPSubscribeRequestAsync(new PullMessages()));
        soap.addAsyncListener(GetServiceCapabilitiesResponse.class, "listen-GetServiceCapabilitiesResponse", response -> {
            if (response.getCapabilities().getWsSubscriptionPolicySupport()) {
                soap.sendSOAPEventRequestAsync(new Subscribe().setConsumerReference(new EndpointReferenceType()
                        .setAddress(new AttributedURIType().setValue(onvifDeviceState.getIp() + ":" + onvifDeviceState.getServerPort()))));
            }
            soap.sendSOAPSubscribeRequestAsync(new PullMessages());
        });
    }

    public void initFully() {
        soap.sendSOAPEventRequestAsync(new GetEventProperties());
        soap.sendSOAPEventRequestAsync(new GetServiceCapabilities());
    }

    public void fireEvent(String message) {
        handleEventReceived(SOAP.parseMessage(PullMessagesResponse.class, message));
    }

    public void subscribe(String event, BiConsumer<String, String> handler) {
        this.eventHandlers.put(event, handler);
    }

    public void subscribe(String event1, String event2, String event3, BiConsumer<String, String> handler) {
        this.eventHandlers.put(event1, handler);
        this.eventHandlers.put(event2, handler);
        this.eventHandlers.put(event3, handler);
    }

    private void handleEventReceived(PullMessagesResponse pullMessagesResponse) {
        if (pullMessagesResponse != null) {
            for (NotificationMessageHolderType notificationMessageHolderType : pullMessagesResponse.getNotificationMessage()) {
                String topic = notificationMessageHolderType.getTopic().getContent().get(0).toString();
                if (topic.startsWith("tns1:")) {
                    topic = topic.substring("tns1:".length());
                    if (eventHandlers.containsKey(topic)) {
                        Node data = findEventData(notificationMessageHolderType);
                        if (data != null) {
                            String name = data.getAttributes().getNamedItem("Name").getTextContent();
                            String value = data.getAttributes().getNamedItem("Value").getTextContent();
                            log.info("Received onvif event <{}>. Name: <{}>. Value: <{}>", topic, name, value);
                            eventHandlers.get(topic).accept(name, value);
                        }
                    }
                }
            }
        }
        soap.sendSOAPSubscribeRequestAsync(new Renew());
    }

    private Node findEventData(NotificationMessageHolderType notificationMessageHolderType) {
        NodeList childNodes = ((Node) notificationMessageHolderType.getMessage().getAny()).getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getLocalName().equals("Data")) {
                return childNodes.item(i).getFirstChild();
            }
        }
        return null;
    }
}
