package de.onvif.soap.devices;

import de.onvif.soap.OnvifDeviceState;
import de.onvif.soap.SOAP;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.oasis_open.docs.wsn.b_2.NotificationMessageHolderType;
import org.oasis_open.docs.wsn.b_2.Renew;
import org.oasis_open.docs.wsn.b_2.RenewResponse;
import org.oasis_open.docs.wsn.b_2.Subscribe;
import org.oasis_open.docs.wsn.b_2.SubscribeResponse;
import org.oasis_open.docs.wsn.b_2.Unsubscribe;
import org.onvif.ver10.events.wsdl.CreatePullPointSubscription;
import org.onvif.ver10.events.wsdl.CreatePullPointSubscriptionResponse;
import org.onvif.ver10.events.wsdl.GetEventProperties;
import org.onvif.ver10.events.wsdl.GetEventPropertiesResponse;
import org.onvif.ver10.events.wsdl.GetServiceCapabilities;
import org.onvif.ver10.events.wsdl.GetServiceCapabilitiesResponse;
import org.onvif.ver10.events.wsdl.PullMessages;
import org.onvif.ver10.events.wsdl.PullMessagesResponse;
import org.w3._2005._08.addressing.AttributedURIType;
import org.w3._2005._08.addressing.EndpointReferenceType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EventDevices {

  private final Map<String, EventHandler> eventHandlers = new HashMap<>();

  private final OnvifDeviceState onvifDeviceState;
  private final SOAP soap;
  private final Logger log;

  private GetEventPropertiesResponse eventProperties;
  private long lastTimeRenewResponse;

  public EventDevices(OnvifDeviceState onvifDeviceState, SOAP soap) {
    this.onvifDeviceState = onvifDeviceState;
    this.soap = soap;
    this.log = onvifDeviceState.getLogger();
  }

  public void runOncePerMinute() {
    // send renew to onvif camera if no response more than one min
    if (lastTimeRenewResponse != 0 && System.currentTimeMillis() - lastTimeRenewResponse > 60000) {
      log.warn("Resend 'Renew' message to onvif device");
      soap.sendSOAPSubscribeRequestAsync(new Renew());
    }
    // try reinitialize if Subscription not initialized
    if (onvifDeviceState.getSubscriptionIpLessUri() == null) {
      fetchSubscriptionUrlAndSendPullMessages();
    }
  }

  public void dispose() {
    if (eventProperties != null) {
      soap.sendSOAPEventRequestAsync(new Unsubscribe());
      eventProperties = null;
    }
  }

  public void initFully() {
    // first listen for events
    soap.addAsyncListener(PullMessagesResponse.class, "listen-PullMessagesResponse", this::handleEventReceived);
    soap.addAsyncListener(RenewResponse.class, "listen-RenewResponse", response -> {
      log.debug("Pulling messages from onvif");
      this.lastTimeRenewResponse = System.currentTimeMillis();
      soap.sendSOAPSubscribeRequestAsync(new PullMessages());
    });

    this.eventProperties = soap.createSOAPDeviceRequestType(new GetEventProperties(), GetEventPropertiesResponse.class);

    fetchSubscriptionUrlAndSendPullMessages();

    GetServiceCapabilitiesResponse serviceCapabilities = soap.createSOAPDeviceRequestType(new GetServiceCapabilities(), GetServiceCapabilitiesResponse.class);
    if (serviceCapabilities != null && serviceCapabilities.getCapabilities().getWsSubscriptionPolicySupport()) {
      Subscribe subscribe = new Subscribe().setConsumerReference(new EndpointReferenceType()
          .setAddress(new AttributedURIType().setValue(onvifDeviceState.getIp() + ":" + onvifDeviceState.getServerPort())));
      SubscribeResponse subscribeResponse = soap.createSOAPDeviceRequestType(subscribe, SubscribeResponse.class);
      if (subscribeResponse != null) {
        log.info("Onvif Subscribe appears to be working for Alarms/Events.");
      }
    }
  }

  private void fetchSubscriptionUrlAndSendPullMessages() {
    log.info("Trying fetch onvif message subscription for ip address <{}>...", this.onvifDeviceState.getIp());
    try {
      CreatePullPointSubscriptionResponse pullPointResponse = soap.createSOAPDeviceRequestTypeThrowError(
          new CreatePullPointSubscription(),
          CreatePullPointSubscriptionResponse.class);
      onvifDeviceState.setSubscriptionUri(pullPointResponse.getSubscriptionReference().getAddress().getValue());
      soap.sendSOAPSubscribeRequestAsync(new PullMessages());
      log.info("Successfully fetched onvif message subscription: <{}>",
          onvifDeviceState.getSubscriptionIpLessUri());
      onvifDeviceState.setSubscriptionError(null);
    } catch (Exception ex) {
      onvifDeviceState.setSubscriptionError(ex.getMessage());
      log.error("Unable to fetch onvif message subscription");
    }
  }

  public void fireEvent(String message) {
    handleEventReceived(SOAP.parseMessage(PullMessagesResponse.class, message, log));
  }

  public void subscribe(String event, EventHandler eventHandler) {
    this.eventHandlers.put(event, eventHandler);
  }

  public void subscribe(String event1, String event2, String event3, EventHandler handler) {
    this.eventHandlers.put(event1, handler);
    this.eventHandlers.put(event2, handler);
    this.eventHandlers.put(event3, handler);
  }

  private void handleEventReceived(PullMessagesResponse pullMessagesResponse) {
    if (pullMessagesResponse != null) {
      boolean handled = pullMessagesResponse.getNotificationMessage().isEmpty();
      for (NotificationMessageHolderType notificationMessageHolderType : pullMessagesResponse.getNotificationMessage()) {
        String topic = notificationMessageHolderType.getTopic().getContent().get(0).toString();
        if (topic.startsWith("tns1:")) {
          topic = topic.substring("tns1:".length());
          if (eventHandlers.containsKey(topic)) {
            Node data = findEventData(notificationMessageHolderType);
            if (data != null) {
              handled = true;
              String name = data.getAttributes().getNamedItem("Name").getTextContent();
              String value = data.getAttributes().getNamedItem("Value").getTextContent();
              log.info("Received onvif event <{}>. Name: <{}>. Value: <{}>", topic, name, value);
              eventHandlers.get(topic).handle(name, value);
            }
          }
        }
      }
      if (!handled) {
        log.warn("No handler found for Onvif message <{}>", pullMessagesResponse);
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

  public interface EventHandler {

    void handle(String name, String value);
  }
}
