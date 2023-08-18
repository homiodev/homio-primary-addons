package de.onvif.soap;

import de.onvif.soap.devices.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.onvif.ver10.device.wsdl.GetDeviceInformationResponse;
import org.onvif.ver10.schema.Capabilities;
import org.onvif.ver10.schema.Profile;
import org.onvif.ver10.schema.VideoResolution;

import java.net.ConnectException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Log4j2
public class OnvifDeviceState {

    private final @NotNull SOAP soap;
    private final @NotNull InitialDevices initialDevices;
    private final @NotNull PtzDevices ptzDevices;
    private final @NotNull MediaDevices mediaDevices;
    private final @NotNull ImagingDevices imagingDevices;
    private final @NotNull EventDevices eventDevices;
    private final @NotNull String entityID;
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
    private List<Profile> profiles;
    private TreeMap<VideoEncodeResolution, Profile> resolutionProfiles;
    private String ip;
    private int onvifPort;
    private String profileToken;
    @Setter
    private Consumer<String> unreachableHandler;
    private Capabilities capabilities;
    private String subscriptionError;
    private boolean initialized;

    @Setter
    private @Nullable Runnable updateListener;

    @SneakyThrows
    public OnvifDeviceState(@NotNull String entityID) {
        this.entityID = entityID;
        this.soap = new SOAP(this);
        this.initialDevices = new InitialDevices(this, soap);
        this.ptzDevices = new PtzDevices(this, soap);
        this.mediaDevices = new MediaDevices(this, soap);
        this.imagingDevices = new ImagingDevices(this, soap);
        this.eventDevices = new EventDevices(entityID, this, soap);
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
        if (updateListener != null) {
            updateListener.run();
        }
    }

    public void updateParameters(String ip, int onvifPort, String user, String password) {
        this.ip = ip;
        this.onvifPort = onvifPort;
        this.HOST_IP = ip + ":" + onvifPort;
        this.serverDeviceUri = "http://%s/onvif/device_service".formatted(HOST_IP);
        this.serverDeviceIpLessUri = "/onvif/device_service";
        this.username = user;
        this.password = password;
    }

    @SneakyThrows
    public void initFully(int onvifMediaProfile, boolean supportOnvifEvents) {
        if (initialized) {
            return;
        }
        this.init();
        this.profiles = initialDevices.getProfiles();
        this.resolutionProfiles = new TreeMap<>(buildResolutionProfiles());

        int activeProfileIndex = onvifMediaProfile >= this.profiles.size() ? 0 : onvifMediaProfile;
        Profile profile =
                this.profiles.size() > activeProfileIndex ? this.profiles.get(activeProfileIndex) : null;
        if (profile != null) {
            this.profileToken = profile.getToken();
        }

        if (ptzDevices.supportPTZ()) {
            ptzDevices.initFully();
        }

        if (supportOnvifEvents) {
            eventDevices.initFully();
        }
        initialized = true;
    }

    private Map<VideoEncodeResolution, Profile> buildResolutionProfiles() {
        return profiles.stream().collect(Collectors.toMap(profile ->
                        new VideoEncodeResolution(profile.getVideoEncoderConfiguration().getResolution()),
                Function.identity()));
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
            original = original.replace("http:///", "http://" + HOST_IP);
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

    public String getEncryptedNonce() {
        if (nonce == null) {
            createNonce();
        }
        return Base64.encodeBase64String(nonce.getBytes());
    }

    public void createNonce() {
        Random generator = new Random();
        nonce = String.valueOf(generator.nextInt());
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

    public String getIEEEAddress() {
        try {
            this.init();
            GetDeviceInformationResponse deviceInformation = initialDevices.getDeviceInformation();
            return deviceInformation.getSerialNumber() == null
                    ? null
                    : deviceInformation.getModel() + "~" + deviceInformation.getSerialNumber();
        } catch (Exception ex) {
            // in case of auth this method may throws exception
            return null;
        }
    }

  /*public Date getDate() {
      init();
      return initialDevices.getDate();
  }*/

    public void cameraUnreachable(String errorMessage) {
        log.error("[{}]: Camera unreachable: <{}>", entityID, errorMessage);
        if (unreachableHandler != null) {
            unreachableHandler.accept(errorMessage);
        }
    }

  /*public String getHostname() {
      return initialDevices.getHostname();
  }*/

    public void setSubscriptionUri(String subscriptionUri) {
        this.subscriptionIpLessUri = SOAP.removeIpFromUrl(subscriptionUri);
    }

    public String getProfile(boolean highResolution) {
        return highResolution
                ? resolutionProfiles.firstEntry().getValue().getName()
                : resolutionProfiles.lastEntry().getValue().getName();
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
                log.error("[{}]: Unknown/Not implemented local protocol!", entityID);
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

    private String getNonce() {
        if (nonce == null) {
            createNonce();
        }
        return nonce;
    }

    @EqualsAndHashCode
    private static class VideoEncodeResolution implements Comparable<VideoEncodeResolution> {

        private final int width;
        private final int height;

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
