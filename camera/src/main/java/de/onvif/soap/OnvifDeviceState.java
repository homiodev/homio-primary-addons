package de.onvif.soap;

import de.onvif.soap.devices.ImagingDevices;
import de.onvif.soap.devices.InitialDevices;
import de.onvif.soap.devices.MediaDevices;
import de.onvif.soap.devices.PtzDevices;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.onvif.ver10.device.wsdl.GetDeviceInformationResponse;
import org.onvif.ver10.schema.Capabilities;
import org.onvif.ver10.schema.Profile;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;

import javax.ws.rs.NotAuthorizedException;
import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@Log4j2
public class OnvifDeviceState {
    private final String HOST_IP;
    private String originalIp;

    private boolean isProxy;

    private String username, password, nonce, utcTime;

    private String serverDeviceUri, serverPtzUri, serverMediaUri, serverImagingUri, serverEventsUri;

    private SOAP soap;

    private InitialDevices initialDevices;
    private PtzDevices ptzDevices;
    private MediaDevices mediaDevices;
    private ImagingDevices imagingDevices;

    private List<Profile> profiles;
    private OnvifCameraEntity onvifCameraEntity;
    private String snapshotUri;
    private String rtspUri;
    private String ip;
    private int port;

    @SneakyThrows
    public OnvifDeviceState(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.HOST_IP = ip + ":" + port;
        this.serverDeviceUri = "http://" + HOST_IP + "/onvif/device_service";

        this.username = user;
        this.password = password;

        this.soap = new SOAP(this);
        this.initialDevices = new InitialDevices(this);
        this.ptzDevices = new PtzDevices(this);
        this.mediaDevices = new MediaDevices(this);
        this.imagingDevices = new ImagingDevices(this);

        init();
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
     *
     * @throws ConnectException Get thrown if device doesn't give answers to
     *                          GetCapabilities()
     */
    protected void init() throws ConnectException, SOAPException {
        Capabilities capabilities = getDevices().getCapabilities();

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
        }

        if (capabilities.getPTZ() != null && capabilities.getPTZ().getXAddr() != null) {
            serverPtzUri = replaceLocalIpWithProxyIp(capabilities.getPTZ().getXAddr());
        }

        if (capabilities.getImaging() != null && capabilities.getImaging().getXAddr() != null) {
            serverImagingUri = replaceLocalIpWithProxyIp(capabilities.getImaging().getXAddr());
        }

        if (capabilities.getMedia() != null && capabilities.getEvents().getXAddr() != null) {
            serverEventsUri = replaceLocalIpWithProxyIp(capabilities.getEvents().getXAddr());
        }
    }

    @SneakyThrows
    public void initFully(OnvifCameraEntity onvifCameraEntity) {
        this.onvifCameraEntity = onvifCameraEntity;
        this.profiles = getDevices().getProfiles();

        int activeProfileIndex = onvifCameraEntity.getOnvifMediaProfile() >= this.profiles.size() ? 0 : onvifCameraEntity.getOnvifMediaProfile();
        String activeProfileToken = this.profiles.get(activeProfileIndex).getToken();
        this.snapshotUri = getMedia().getSnapshotUri(activeProfileToken);
        this.rtspUri = getMedia().getRTSPStreamUri(activeProfileToken);
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

    public SOAP getSoap() {
        return soap;
    }

    /**
     * Is used for basic devices and requests of given Onvif Device
     */
    public InitialDevices getDevices() {
        return initialDevices;
    }

    /**
     * Can be used for PTZ controlling requests, may not be supported by device!
     */
    public PtzDevices getPtz() {
        return ptzDevices;
    }

    /**
     * Can be used to get media data from OnvifDevice
     */
    public MediaDevices getMedia() {
        return mediaDevices;
    }

    /**
     * Can be used to get media data from OnvifDevice
     */
    public ImagingDevices getImaging() {
        return imagingDevices;
    }

    public Logger getLogger() {
        return log;
    }

    public String getDeviceUri() {
        return serverDeviceUri;
    }

    protected String getPtzUri() {
        return serverPtzUri;
    }

    protected String getMediaUri() {
        return serverMediaUri;
    }

    protected String getImagingUri() {
        return serverImagingUri;
    }

    protected String getEventsUri() {
        return serverEventsUri;
    }

    public Date getDate() {
        return initialDevices.getDate();
    }

    public String getIEEEAddress() {
        GetDeviceInformationResponse deviceInformation = initialDevices.getDeviceInformation();
        return deviceInformation.getSerialNumber() == null ? null : deviceInformation.getModel() + "~" + deviceInformation.getSerialNumber();
    }

    public String getHostname() {
        return initialDevices.getHostname();
    }

    public void checkForErrors() {
        if (!isOnline()) {
            throw new RuntimeException("No connection to onvif device");
        }
        GetDeviceInformationResponse deviceInformation = initialDevices.getDeviceInformation();
        if (deviceInformation.getFault() != null) {
            throw new NotAuthorizedException(deviceInformation.getFault().getFaultstring());
        }
    }
}
