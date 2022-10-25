package de.onvif.soap;

import de.onvif.soap.devices.EventDevices;
import de.onvif.soap.devices.ImagingDevices;
import de.onvif.soap.devices.InitialDevices;
import de.onvif.soap.devices.MediaDevices;
import de.onvif.soap.devices.PtzDevices;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.onvif.ver10.device.wsdl.GetDeviceInformationResponse;
import org.onvif.ver10.schema.Capabilities;
import org.onvif.ver10.schema.Profile;
import org.onvif.ver10.schema.VideoResolution;

@Getter
public class OnvifDeviceState {

  private String HOST_IP;
  private String originalIp;

  private boolean isProxy;

  private String username, password, nonce, utcTime;

  private String serverDeviceUri;
  private String serverPtzUri;
  private String serverMediaUri;
  private String serverImagingUri;
  private String serverEventsUri;
  private String analyticsUri;

  private String serverDeviceIpLessUri;
  private String serverPtzIpLessUri;
  private String serverMediaIpLessUri;
  private String serverImagingIpLessUri;
  private String serverEventsIpLessUri;
  private String subscriptionIpLessUri;

  private SOAP soap;

  private InitialDevices initialDevices;
  private PtzDevices ptzDevices;
  private MediaDevices mediaDevices;
  private ImagingDevices imagingDevices;
  private EventDevices eventDevices;

  private List<Profile> profiles;
  private TreeMap<VideoEncodeResolution, Profile> resolutionProfiles;
  private String ip;
  private int onvifPort;
  private int serverPort;
  private Logger log;
  private String profileToken;
  @Setter
  private Consumer<String> unreachableHandler;
  private Capabilities capabilities;
  private String subscriptionError;

  @SneakyThrows
  public OnvifDeviceState(Logger log) {
    this.log = log;
    this.soap = new SOAP(this);
    this.initialDevices = new InitialDevices(this, soap);
    this.ptzDevices = new PtzDevices(this, soap);
    this.mediaDevices = new MediaDevices(this, soap);
    this.imagingDevices = new ImagingDevices(this, soap);
    this.eventDevices = new EventDevices(this, soap);
  }

  private static byte[] sha1(String s) throws NoSuchAlgorithmException {
    MessageDigest SHA1;
    SHA1 = MessageDigest.getInstance("SHA1");

    SHA1.reset();
    SHA1.update(s.getBytes());

    return SHA1.digest();
  }

  public void setSubscriptionError(String subscriptionError) {
    this.subscriptionError = subscriptionError;
  }

  public void updateParameters(String ip, int onvifPort, int serverPort, String user, String password) {
    this.ip = ip;
    this.onvifPort = onvifPort;
    this.serverPort = serverPort;
    this.HOST_IP = ip + ":" + onvifPort;
    this.serverDeviceUri = "http://" + HOST_IP + "/onvif/device_service";
    this.serverDeviceIpLessUri = "/onvif/device_service";
    this.username = user;
    this.password = password;
  }

  /**
   * Internal function to check, if device is available and answers to ping requests.
   */
  public boolean isOnline() {
    String port = HOST_IP.contains(":") ? HOST_IP.substring(HOST_IP.indexOf(':') + 1) : "80";
    String ip = HOST_IP.contains(":") ? HOST_IP.substring(0, HOST_IP.indexOf(':')) : HOST_IP;

    Socket socket = null;
    try {
      SocketAddress sockaddr = new InetSocketAddress(ip, new Integer(port));
      socket = new Socket();

      socket.connect(sockaddr, 5000);
    } catch (NumberFormatException | IOException e) {
      return false;
    } finally {
      try {
        if (socket != null) {
          socket.close();
        }
      } catch (IOException ignored) {
      }
    }
    return true;
  }

  /**
   * Initalizes the addresses used for SOAP messages and to get the internal IP, if given IP is a proxy.
   */
  @SneakyThrows
  private void init() {
    if (this.capabilities == null) {
      this.capabilities = initialDevices.getCapabilities();

      if (capabilities == null) {
        throw new ConnectException("Capabilities not reachable.");
      }

      String localDeviceUri = capabilities.getDevice().getXAddr();

      if (localDeviceUri.startsWith("http://")) {
        originalIp = localDeviceUri.replace("http://", "");
        originalIp = originalIp.substring(0, originalIp.indexOf('/'));
      } else {
        log.error("Unknown/Not implemented local protocol!");
      }

      if (!originalIp.equals(HOST_IP)) {
        isProxy = true;
      }

      if (capabilities.getMedia() != null && capabilities.getMedia().getXAddr() != null) {
        serverMediaUri = replaceLocalIpWithProxyIp(capabilities.getMedia().getXAddr());
        serverMediaIpLessUri = SOAP.removeIpFromUrl(serverMediaUri);
      }

      if (capabilities.getPTZ() != null && capabilities.getPTZ().getXAddr() != null) {
        serverPtzUri = replaceLocalIpWithProxyIp(capabilities.getPTZ().getXAddr());
        serverPtzIpLessUri = SOAP.removeIpFromUrl(serverPtzUri);
      }

      if (capabilities.getImaging() != null && capabilities.getImaging().getXAddr() != null) {
        serverImagingUri = replaceLocalIpWithProxyIp(capabilities.getImaging().getXAddr());
        serverImagingIpLessUri = SOAP.removeIpFromUrl(serverImagingUri);
      }

      if (capabilities.getMedia() != null && capabilities.getEvents().getXAddr() != null) {
        serverEventsUri = replaceLocalIpWithProxyIp(capabilities.getEvents().getXAddr());
        serverEventsIpLessUri = SOAP.removeIpFromUrl(serverEventsUri);
      }

      if (capabilities.getAnalytics() != null && capabilities.getAnalytics().getXAddr() != null) {
        analyticsUri = replaceLocalIpWithProxyIp(capabilities.getAnalytics().getXAddr());
      }
    }
  }

  @SneakyThrows
  public void initFully(int onvifMediaProfile, boolean supportOnvifEvents) {
    this.init();
    this.profiles = initialDevices.getProfiles();
    this.resolutionProfiles = new TreeMap<>(this.profiles.stream().collect(Collectors.toMap(profile ->
        new VideoEncodeResolution(profile.getVideoEncoderConfiguration().getResolution()), Function.identity())));

    int activeProfileIndex = onvifMediaProfile >= this.profiles.size() ? 0 : onvifMediaProfile;
    Profile profile = this.profiles.size() > activeProfileIndex ? this.profiles.get(activeProfileIndex) : null;
    if (profile != null) {
      this.profileToken = profile.getToken();
    }

    if (ptzDevices.supportPTZ()) {
      ptzDevices.initFully();
    }

    if (supportOnvifEvents) {
      eventDevices.initFully();
    }
  }

  public void dispose() {
    imagingDevices.dispose();
    eventDevices.dispose();
    initialDevices.dispose();
    mediaDevices.dispose();
    ptzDevices.dispose();
    soap.dispose();
  }

  public String replaceLocalIpWithProxyIp(String original) {
    if (original.startsWith("http:///")) {
      original.replace("http:///", "http://" + HOST_IP);
    }

    if (isProxy) {
      return original.replace(originalIp, HOST_IP);
    }
    return original;
  }

  public String getUsername() {
    return username;
  }

  public String getEncryptedPassword() {
    return encryptPassword();
  }

  /**
   * Returns encrypted version of given password like algorithm like in WS-UsernameToken
   */
  public String encryptPassword() {
    String nonce = getNonce();
    String timestamp = getUTCTime();

    String beforeEncryption = nonce + timestamp + password;

    byte[] encryptedRaw;
    try {
      encryptedRaw = sha1(beforeEncryption);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
    return Base64.encodeBase64String(encryptedRaw);
  }

  private String getNonce() {
    if (nonce == null) {
      createNonce();
    }
    return nonce;
  }

  public String getEncryptedNonce() {
    if (nonce == null) {
      createNonce();
    }
    return Base64.encodeBase64String(nonce.getBytes());
  }

  public void createNonce() {
    Random generator = new Random();
    nonce = "" + generator.nextInt();
  }

  public String getLastUTCTime() {
    return utcTime;
  }

  public String getUTCTime() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'");
    sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));

    Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    String utcTime = sdf.format(cal.getTime());
    this.utcTime = utcTime;
    return utcTime;
  }

  public Logger getLogger() {
    return log;
  }

    /*public Date getDate() {
        init();
        return initialDevices.getDate();
    }*/

  public String getIEEEAddress() {
    try {
      this.init();
      GetDeviceInformationResponse deviceInformation = initialDevices.getDeviceInformation();
      return deviceInformation.getSerialNumber() == null ? null : deviceInformation.getModel() + "~" + deviceInformation.getSerialNumber();
    } catch (Exception ex) {
      // in case of auth this method may throws exception
      return null;
    }
  }

    /*public String getHostname() {
        return initialDevices.getHostname();
    }*/

  public void checkForErrors() {
    if (!isOnline()) {
      throw new RuntimeException("No connection to onvif camera");
    }
    this.init();
    // check for Authentication validation
    initialDevices.getDeviceInformation();
  }

  public void cameraUnreachable(String errorMessage) {
    log.error("Camera unreachable: <{}>", errorMessage);
    if (unreachableHandler != null) {
      unreachableHandler.accept(errorMessage);
    }
  }

  public void setSubscriptionUri(String subscriptionUri) {
    this.subscriptionIpLessUri = SOAP.removeIpFromUrl(subscriptionUri);
  }

  public String getProfile(boolean highResolution) {
    return highResolution ? resolutionProfiles.firstEntry().getValue().getName() : resolutionProfiles.lastEntry().getValue().getName();
  }

  public void runOncePerMinute() {
    this.eventDevices.runOncePerMinute();
  }

  @EqualsAndHashCode
  private static class VideoEncodeResolution implements Comparable<VideoEncodeResolution> {

    private int width;
    private int height;

    public VideoEncodeResolution(VideoResolution resolution) {
      this.width = resolution.getWidth();
      this.height = resolution.getHeight();
    }

    @Override
    public int compareTo(@NotNull OnvifDeviceState.VideoEncodeResolution o) {
      return Integer.compare(width + height, o.width + o.height);
    }
  }
}
