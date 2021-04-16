package de.onvif.soap;

import de.onvif.soap.devices.*;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.onvif.ver10.device.wsdl.GetDeviceInformationResponse;
import org.onvif.ver10.schema.Capabilities;
import org.onvif.ver10.schema.Profile;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.onvif.CameraBrandHandlerDescription;

import javax.ws.rs.NotAuthorizedException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

@Log4j2
@Getter
public class OnvifDeviceState {
    private final String HOST_IP;
    private String originalIp;

    private boolean isProxy;

    private String username, password, nonce, utcTime;

    private String serverDeviceUri;
    private String serverPtzUri;
    private String serverMediaUri;
    private String serverImagingUri;
    private String serverEventsUri;
    private String subscriptionUri;
    private String analyticsUri;

    private String serverDeviceIpLessUri;
    private String serverPtzIpLessUri;
    private String serverMediaIpLessUri;
    private String serverImagingIpLessUri;
    private String serverEventsIpLessUri;
    @Setter
    private String subscriptionIpLessUri;

    private SOAP soap;

    private InitialDevices initialDevices;
    private PtzDevices ptzDevices;
    private MediaDevices mediaDevices;
    private ImagingDevices imagingDevices;
    private EventDevices eventDevices;

    private List<Profile> profiles;
    private String ip;
    private int onvifPort;
    private int serverPort;
    private String profileToken;
    @Setter
    private Consumer<String> unreachableHandler;

    @SneakyThrows
    public OnvifDeviceState(String ip, int onvifPort, int serverPort, String user, String password) {
        this.ip = ip;
        this.onvifPort = onvifPort;
        this.serverPort = serverPort;
        this.HOST_IP = ip + ":" + onvifPort;
        this.serverDeviceUri = "http://" + HOST_IP + "/onvif/device_service";
        this.serverDeviceIpLessUri = "/onvif/device_service";

        this.username = user;
        this.password = password;

        this.soap = new SOAP(this);
        this.initialDevices = new InitialDevices(this, soap);
        this.ptzDevices = new PtzDevices(this, soap);
        this.mediaDevices = new MediaDevices(this, soap);
        this.imagingDevices = new ImagingDevices(this, soap);
        this.eventDevices = new EventDevices(this, soap);

        this.init();
    }

    /**
     * Internal function to check, if device is available and answers to ping
     * requests.
     */
    private boolean isOnline() {
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
     * Initalizes the addresses used for SOAP messages and to get the internal
     * IP, if given IP is a proxy.
     */
    @SneakyThrows
    private void init() {
        Capabilities capabilities = initialDevices.getCapabilities();

        if (capabilities == null) {
            throw new ConnectException("Capabilities not reachable.");
        }

        String localDeviceUri = capabilities.getDevice().getXAddr();

        if (localDeviceUri.startsWith("http://")) {
            originalIp = localDeviceUri.replace("http://", "");
            originalIp = originalIp.substring(0, originalIp.indexOf('/'));
        } else {
            log.error("Unknown/Not implemented local procotol!");
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

    @SneakyThrows
    public void initFully(OnvifCameraEntity onvifCameraEntity) {
        this.profiles = initialDevices.getProfiles();

        int activeProfileIndex = onvifCameraEntity.getOnvifMediaProfile() >= this.profiles.size() ? 0 : onvifCameraEntity.getOnvifMediaProfile();
        this.profileToken = this.profiles.get(activeProfileIndex).getToken();

        if (ptzDevices.supportPTZ()) {
            ptzDevices.initFully();
        }
        if (onvifCameraEntity.getCameraType().equals(CameraBrandHandlerDescription.DEFAULT_BRAND.getID())) {
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

    private static byte[] sha1(String s) throws NoSuchAlgorithmException {
        MessageDigest SHA1;
        SHA1 = MessageDigest.getInstance("SHA1");

        SHA1.reset();
        SHA1.update(s.getBytes());

        return SHA1.digest();
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
        this.init();
        GetDeviceInformationResponse deviceInformation = initialDevices.getDeviceInformation();
        return deviceInformation.getSerialNumber() == null ? null : deviceInformation.getModel() + "~" + deviceInformation.getSerialNumber();
    }

    /*public String getHostname() {
        return initialDevices.getHostname();
    }*/

    public void checkForErrors() {
        if (!isOnline()) {
            throw new RuntimeException("No connection to onvif device");
        }
        this.init();
        GetDeviceInformationResponse deviceInformation = initialDevices.getDeviceInformation();
        if (deviceInformation.getFault() != null) {
            throw new NotAuthorizedException(deviceInformation.getFault().getFaultstring());
        }
    }

    public void cameraUnreachable(String errorMessage) {
        log.error("Camera unreachable: <{}>", errorMessage);
        if (unreachableHandler != null) {
            unreachableHandler.accept(errorMessage);
        }
    }

    public void setSubscriptionUri(String subscriptionUri) {
        this.subscriptionUri = subscriptionUri;
        this.subscriptionIpLessUri = SOAP.removeIpFromUrl(subscriptionUri);
    }
}
