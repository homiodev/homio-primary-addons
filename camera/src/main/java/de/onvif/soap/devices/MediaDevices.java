package de.onvif.soap.devices;

import de.onvif.soap.OnvifDeviceState;
import de.onvif.soap.SOAP;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.onvif.ver10.media.wsdl.*;
import org.onvif.ver10.schema.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MediaDevices {

    private final OnvifDeviceState onvifDeviceState;
    private final SOAP soap;

    private Map<String, ProfileMediaDeviceCache> profileCache = new HashMap<>();

    private static class ProfileMediaDeviceCache {
        private Map<TransportProtocol, String> protocolURI = new HashMap<>();
        private String snapshotUri;
    }

    public String getHTTPStreamUri() {
        return getHTTPStreamUri(onvifDeviceState.getProfileToken());
    }

    public String getHTTPStreamUri(String profile) {
        return getStreamUri(profile, TransportProtocol.HTTP);
    }

    public String getUDPStreamUri() {
        return getUDPStreamUri(onvifDeviceState.getProfileToken());
    }

    public String getUDPStreamUri(String profile) {
        return getStreamUri(profile, TransportProtocol.UDP);
    }

    public String getRTSPStreamUri() {
        return getRTSPStreamUri(onvifDeviceState.getProfileToken());
    }

    public String getRTSPStreamUri(String profile) {
        return getStreamUri(profile, TransportProtocol.TCP);
    }

    @SneakyThrows
    private String getStreamUri(String profileToken, StreamSetup streamSetup) {
        GetStreamUri request = new GetStreamUri();
        request.setProfileToken(profileToken);
        request.setStreamSetup(streamSetup);
        GetStreamUriResponse response = soap.createSOAPMediaRequest(request, GetStreamUriResponse.class);

        if (response == null) {
            return null;
        }

        return onvifDeviceState.replaceLocalIpWithProxyIp(response.getMediaUri().getUri());
    }

    public static VideoEncoderConfiguration getVideoEncoderConfiguration(Profile profile) {
        return profile.getVideoEncoderConfiguration();
    }

    public VideoEncoderConfigurationOptions getVideoEncoderConfigurationOptions(String profileToken) {
        GetVideoEncoderConfigurationOptions request = new GetVideoEncoderConfigurationOptions();
        request.setProfileToken(profileToken);

        GetVideoEncoderConfigurationOptionsResponse response = soap.createSOAPMediaRequest(request, GetVideoEncoderConfigurationOptionsResponse.class);

        if (response == null) {
            return null;
        }

        return response.getOptions();
    }

    public boolean setVideoEncoderConfiguration(VideoEncoderConfiguration videoEncoderConfiguration) {
        SetVideoEncoderConfiguration request = new SetVideoEncoderConfiguration();
        request.setConfiguration(videoEncoderConfiguration);
        request.setForcePersistence(true);

        SetVideoEncoderConfigurationResponse response = soap.createSOAPMediaRequest(request, SetVideoEncoderConfigurationResponse.class);
        return response != null;
    }

    public String getSnapshotUri() {
        return getSnapshotUri(onvifDeviceState.getProfileToken());
    }

    public String getSnapshotUri(String profile) {
        profileCache.putIfAbsent(profile, new ProfileMediaDeviceCache());
        ProfileMediaDeviceCache mediaDeviceCache = profileCache.get(profile);
        if (mediaDeviceCache.snapshotUri == null) {
            try {
                GetSnapshotUri request = new GetSnapshotUri();
                request.setProfileToken(profile);

                GetSnapshotUriResponse response = soap.createSOAPMediaRequest(request, GetSnapshotUriResponse.class);
                if (response == null || response.getMediaUri() == null) {
                    return null;
                }

                mediaDeviceCache.snapshotUri = onvifDeviceState.replaceLocalIpWithProxyIp(response.getMediaUri().getUri());
            } catch (Exception ex) {
                return null;
            }
        }
        return mediaDeviceCache.snapshotUri;
    }

    public List<VideoSource> getVideoSources() {
        GetVideoSources request = new GetVideoSources();
        GetVideoSourcesResponse response = soap.createSOAPMediaRequest(request, GetVideoSourcesResponse.class);
        if (response == null) {
            return null;
        }

        return response.getVideoSources();
    }

    public void dispose() {
        profileCache.clear();
    }

    private String getStreamUri(String profile, TransportProtocol transportProtocol) {
        profileCache.putIfAbsent(profile, new ProfileMediaDeviceCache());
        ProfileMediaDeviceCache mediaDeviceCache = profileCache.get(profile);
        if (!mediaDeviceCache.protocolURI.containsKey(transportProtocol)) {
            StreamSetup setup = new StreamSetup();
            setup.setStream(StreamType.RTP_UNICAST);
            Transport transport = new Transport();
            transport.setProtocol(transportProtocol);
            setup.setTransport(transport);
            mediaDeviceCache.protocolURI.put(transportProtocol, getStreamUri(profile, setup));
        }
        return mediaDeviceCache.protocolURI.get(transportProtocol);
    }
}
