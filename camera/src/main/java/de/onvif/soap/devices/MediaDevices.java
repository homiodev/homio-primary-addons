package de.onvif.soap.devices;

import de.onvif.soap.OnvifDeviceState;
import de.onvif.soap.SOAP;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.onvif.ver10.media.wsdl.*;
import org.onvif.ver10.schema.*;

import javax.xml.soap.SOAPException;
import java.net.ConnectException;
import java.util.List;

@RequiredArgsConstructor
public class MediaDevices {

    private final OnvifDeviceState onvifDeviceState;
    private final SOAP soap;

    private String snapshotUri;
    private String rtspUri;

    @Deprecated
    public String getHTTPStreamUri(int profileNumber) {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.HTTP);
        setup.setTransport(transport);
        return getStreamUri(setup, profileNumber);
    }

    public String getHTTPStreamUri() {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.HTTP);
        setup.setTransport(transport);
        return getStreamUri(onvifDeviceState.getProfileToken(), setup);
    }

    public String getUDPStreamUri() {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.UDP);
        setup.setTransport(transport);
        return getStreamUri(onvifDeviceState.getProfileToken(), setup);
    }

    @Deprecated
    public String getTCPStreamUri(int profileNumber) {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.TCP);
        setup.setTransport(transport);
        return getStreamUri(setup, profileNumber);
    }

    public String getTCPStreamUri() {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.TCP);
        setup.setTransport(transport);
        return getStreamUri(onvifDeviceState.getProfileToken(), setup);
    }

    public String getRTSPStreamUri() {
        if (this.rtspUri == null) {
            StreamSetup setup = new StreamSetup();
            setup.setStream(StreamType.RTP_UNICAST);
            Transport transport = new Transport();
            transport.setProtocol(TransportProtocol.TCP);
            setup.setTransport(transport);
            this.rtspUri = getStreamUri(onvifDeviceState.getProfileToken(), setup);
        }
        return this.rtspUri;
    }

    @Deprecated
    public String getStreamUri(StreamSetup streamSetup, int profileNumber) {
        Profile profile = onvifDeviceState.getInitialDevices().getProfiles().get(profileNumber);
        return getStreamUri(profile, streamSetup);
    }

    @Deprecated
    public String getStreamUri(Profile profile, StreamSetup streamSetup) {
        return getStreamUri(profile.getToken(), streamSetup);
    }

    @SneakyThrows
    public String getStreamUri(String profileToken, StreamSetup streamSetup) {
        GetStreamUri request = new GetStreamUri();
        GetStreamUriResponse response = new GetStreamUriResponse();

        request.setProfileToken(profileToken);
        request.setStreamSetup(streamSetup);
        response = (GetStreamUriResponse) soap.createSOAPMediaRequest(request, response);

        if (response == null) {
            return null;
        }

        return onvifDeviceState.replaceLocalIpWithProxyIp(response.getMediaUri().getUri());
    }

    public static VideoEncoderConfiguration getVideoEncoderConfiguration(Profile profile) {
        return profile.getVideoEncoderConfiguration();
    }

    public VideoEncoderConfigurationOptions getVideoEncoderConfigurationOptions(String profileToken) throws SOAPException, ConnectException {
        GetVideoEncoderConfigurationOptions request = new GetVideoEncoderConfigurationOptions();
        GetVideoEncoderConfigurationOptionsResponse response = new GetVideoEncoderConfigurationOptionsResponse();

        request.setProfileToken(profileToken);

        response = (GetVideoEncoderConfigurationOptionsResponse) soap.createSOAPMediaRequest(request, response);

        if (response == null) {
            return null;
        }

        return response.getOptions();
    }

    public boolean setVideoEncoderConfiguration(VideoEncoderConfiguration videoEncoderConfiguration) throws SOAPException, ConnectException {
        SetVideoEncoderConfiguration request = new SetVideoEncoderConfiguration();
        SetVideoEncoderConfigurationResponse response = new SetVideoEncoderConfigurationResponse();

        request.setConfiguration(videoEncoderConfiguration);
        request.setForcePersistence(true);

        response = (SetVideoEncoderConfigurationResponse) soap.createSOAPMediaRequest(request, response);
        return response != null;
    }

    public String getSnapshotUri() {
        try {
            if (this.snapshotUri == null) {
                GetSnapshotUri request = new GetSnapshotUri();
                GetSnapshotUriResponse response = new GetSnapshotUriResponse();
                request.setProfileToken(onvifDeviceState.getProfileToken());

                response = (GetSnapshotUriResponse) soap.createSOAPMediaRequest(request, response);
                if (response == null || response.getMediaUri() == null) {
                    return null;
                }

                this.snapshotUri = onvifDeviceState.replaceLocalIpWithProxyIp(response.getMediaUri().getUri());
            }
            return this.snapshotUri;
        } catch (Exception ex) {
            return null;
        }
    }

    public List<VideoSource> getVideoSources() throws SOAPException, ConnectException {
        GetVideoSources request = new GetVideoSources();
        GetVideoSourcesResponse response = new GetVideoSourcesResponse();

        response = (GetVideoSourcesResponse) soap.createSOAPMediaRequest(request, response);
        if (response == null) {
            return null;
        }

        return response.getVideoSources();
    }

    public void dispose() {
        snapshotUri = null;
        rtspUri = null;
    }
}
