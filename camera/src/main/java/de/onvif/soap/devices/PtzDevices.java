package de.onvif.soap.devices;

import de.onvif.soap.OnvifDeviceState;
import de.onvif.soap.SOAP;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.onvif.ver10.schema.*;
import org.onvif.ver20.ptz.wsdl.*;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PtzDevices {

    private final OnvifDeviceState onvifDeviceState;
    private final SOAP soap;

    // These hold the cameras PTZ position in the range that the camera uses, ie
    // mine is -1 to +1
    private Float panRangeMin = -1.0f;
    private Float panRangeMax = 1.0f;
    private Float tiltRangeMin = -1.0f;
    private Float tiltRangeMax = 1.0f;
    private Float zoomMin = 0.0f;
    private Float zoomMax = 1.0f;

    // These hold the PTZ values for updating controls in 0-100 range
    private Float currentPanPercentage = 0.0f;
    private Float currentTiltPercentage = 0.0f;
    private Float currentZoomPercentage = 0.0f;
    private Float currentPanCamValue = 0.0f;
    private Float currentTiltCamValue = 0.0f;
    private Float currentZoomCamValue = 0.0f;
    private List<PTZPreset> presets;

    public boolean isPtzOperationsSupported(String profileToken) {
        return getPTZConfiguration(profileToken) != null;
    }

    /**
     * @param profileToken
     * @return If is null, PTZ operations are not supported
     */
    public PTZConfiguration getPTZConfiguration(String profileToken) {
        if (profileToken == null || profileToken.equals("")) {
            return null;
        }
        Profile profile = onvifDeviceState.getInitialDevices().getProfile(profileToken);
        if (profile == null) {
            throw new IllegalArgumentException("No profile available for token: " + profileToken);
        }
        if (profile.getPTZConfiguration() == null) {
            return null; // no PTZ support
        }

        return profile.getPTZConfiguration();
    }

    public List<PTZNode> getNodes() {
        GetNodes request = new GetNodes();
        GetNodesResponse response = new GetNodesResponse();

        response = (GetNodesResponse) soap.createSOAPDeviceRequest(request, response);
        return response == null ? null : response.getPTZNode();
    }

    public PTZNode getNode(String profileToken) {
        return getNode(getPTZConfiguration(profileToken));
    }

    public PTZNode getNode(PTZConfiguration ptzConfiguration) {
        GetNode request = new GetNode();
        GetNodeResponse response = new GetNodeResponse();

        if (ptzConfiguration == null) {
            return null; // no PTZ support
        }
        request.setNodeToken(ptzConfiguration.getNodeToken());

        response = (GetNodeResponse) soap.createSOAPDeviceRequest(request, response);
        return response == null ? null : response.getPTZNode();
    }

    public FloatRange getPanSpaces(String profileToken) {
        PTZNode node = getNode(profileToken);

        PTZSpaces ptzSpaces = node.getSupportedPTZSpaces();
        return ptzSpaces.getAbsolutePanTiltPositionSpace().get(0).getXRange();
    }

    public FloatRange getTiltSpaces(String profileToken) {
        PTZNode node = getNode(profileToken);

        PTZSpaces ptzSpaces = node.getSupportedPTZSpaces();
        return ptzSpaces.getAbsolutePanTiltPositionSpace().get(0).getYRange();
    }

    public FloatRange getZoomSpaces(String profileToken) {
        PTZNode node = getNode(profileToken);

        PTZSpaces ptzSpaces = node.getSupportedPTZSpaces();
        return ptzSpaces.getAbsoluteZoomPositionSpace().get(0).getXRange();
    }

    public boolean isAbsoluteMoveSupported(String profileToken) {
        Profile profile = onvifDeviceState.getInitialDevices().getProfile(profileToken);
        try {
            if (profile.getPTZConfiguration().getDefaultAbsolutePantTiltPositionSpace() != null) {
                return true;
            }
        } catch (NullPointerException e) {
        }
        return false;
    }

    /**
     * @param x    Pan-Position
     * @param y    Tilt-Position
     * @param zoom Zoom
     * @return True if move successful
     */
    @SneakyThrows
    private boolean absoluteMove(float x, float y, float zoom) {
        String profileToken = onvifDeviceState.getProfileToken();
        PTZNode node = getNode(profileToken);
        if (node != null) {
            FloatRange xRange = node.getSupportedPTZSpaces().getAbsolutePanTiltPositionSpace().get(0).getXRange();
            FloatRange yRange = node.getSupportedPTZSpaces().getAbsolutePanTiltPositionSpace().get(0).getYRange();
            FloatRange zRange = node.getSupportedPTZSpaces().getAbsoluteZoomPositionSpace().get(0).getXRange();

            if (zoom < zRange.getMin() || zoom > zRange.getMax()) {
                throw new IllegalArgumentException("Bad value for zoom: " + zoom);
            }
            if (x < xRange.getMin() || x > xRange.getMax()) {
                throw new IllegalArgumentException("Bad value for pan:/x " + x);
            }
            if (y < yRange.getMin() || y > yRange.getMax()) {
                throw new IllegalArgumentException("Bad value for tilt/y: " + y);
            }
        }

        AbsoluteMove request = new AbsoluteMove();
        AbsoluteMoveResponse response = new AbsoluteMoveResponse();

        request.setPosition(new PTZVector(x, y, zoom));
        request.setProfileToken(profileToken);
        request.setSpeed(new PTZSpeed(0.1f, 0.1f, 0));

        response = (AbsoluteMoveResponse) soap.createSOAPPtzRequest(request, response);
        return response != null;
    }

    public boolean isRelativeMoveSupported(String profileToken) {
        Profile profile = onvifDeviceState.getInitialDevices().getProfile(profileToken);
        try {
            if (profile.getPTZConfiguration().getDefaultRelativePanTiltTranslationSpace() != null) {
                return true;
            }
        } catch (NullPointerException e) {
        }
        return false;
    }

    public boolean relativeMove(float x, float y, float zoom) {
        RelativeMove request = new RelativeMove();
        RelativeMoveResponse response = new RelativeMoveResponse();

        Vector2D panTiltVector = new Vector2D();
        panTiltVector.setX(x);
        panTiltVector.setY(y);
        Vector1D zoomVector = new Vector1D();
        zoomVector.setX(zoom);

        PTZVector translation = new PTZVector();
        translation.setPanTilt(panTiltVector);
        translation.setZoom(zoomVector);

        request.setProfileToken(onvifDeviceState.getProfileToken());
        request.setTranslation(translation);

        response = (RelativeMoveResponse) soap.createSOAPPtzRequest(request, response);
        return response != null;
    }

    public boolean isContinuousMoveSupported(String profileToken) {
        Profile profile = onvifDeviceState.getInitialDevices().getProfile(profileToken);
        try {
            if (profile.getPTZConfiguration().getDefaultContinuousPanTiltVelocitySpace() != null) {
                return true;
            }
        } catch (NullPointerException e) {
        }
        return false;
    }

    public boolean continuousMove(float x, float y, float zoom) {
        ContinuousMove request = new ContinuousMove();
        ContinuousMoveResponse response = new ContinuousMoveResponse();

        Vector2D panTiltVector = new Vector2D();
        panTiltVector.setX(x);
        panTiltVector.setY(y);
        Vector1D zoomVector = new Vector1D();
        zoomVector.setX(zoom);

        PTZSpeed ptzSpeed = new PTZSpeed();
        ptzSpeed.setPanTilt(panTiltVector);
        ptzSpeed.setZoom(zoomVector);
        request.setVelocity(ptzSpeed);

        request.setProfileToken(onvifDeviceState.getProfileToken());

        response = (ContinuousMoveResponse) soap.createSOAPPtzRequest(request, response);
        return response != null;
    }

    public boolean stopMove() {
        Stop request = new Stop();
        request.setPanTilt(true);
        request.setZoom(true);
        StopResponse response = new StopResponse();

        request.setProfileToken(onvifDeviceState.getProfileToken());

        response = (StopResponse) soap.createSOAPPtzRequest(request, response);
        return response != null;
    }

    public PTZStatus getStatus() {
        GetStatus request = new GetStatus();
        GetStatusResponse response = new GetStatusResponse();

        request.setProfileToken(onvifDeviceState.getProfileToken());

        response = (GetStatusResponse) soap.createSOAPPtzRequest(request, response);
        return response == null ? null : response.getPTZStatus();
    }

    public PTZVector getPosition() {
        PTZStatus status = getStatus();

        if (status == null) {
            return null;
        }

        return status.getPosition();
    }

    public boolean setHomePosition(String profileToken) {
        SetHomePosition request = new SetHomePosition();
        SetHomePositionResponse response = new SetHomePositionResponse();

        request.setProfileToken(profileToken);

        response = (SetHomePositionResponse) soap.createSOAPPtzRequest(request, response);
        return response != null;
    }

    public List<PTZPreset> getPresets() {
        if (this.presets == null) {
            GetPresets request = new GetPresets();
            GetPresetsResponse response = new GetPresetsResponse();

            request.setProfileToken(onvifDeviceState.getProfileToken());

            response = (GetPresetsResponse) soap.createSOAPPtzRequest(request, response);

            if (response == null) {
                return null;
            }

            this.presets = response.getPreset();
        }
        return this.presets;
    }

    public String setPreset(String presetName, String presetToken, String profileToken) {
        SetPreset request = new SetPreset();
        SetPresetResponse response = new SetPresetResponse();

        request.setProfileToken(profileToken);
        request.setPresetName(presetName);
        request.setPresetToken(presetToken);

        response = (SetPresetResponse) soap.createSOAPPtzRequest(request, response);
        return response == null ? null : response.getPresetToken();
    }

    public String setPreset(String presetName, String profileToken) {
        return setPreset(presetName, null, profileToken);
    }

    public boolean removePreset(String presetToken, String profileToken) {
        RemovePreset request = new RemovePreset();
        RemovePresetResponse response = new RemovePresetResponse();

        request.setProfileToken(profileToken);
        request.setPresetToken(presetToken);

        response = (RemovePresetResponse) soap.createSOAPPtzRequest(request, response);
        return response != null;
    }

    private boolean gotoPreset(String presetToken) {
        GotoPreset request = new GotoPreset();
        GotoPresetResponse response = new GotoPresetResponse();

        request.setProfileToken(onvifDeviceState.getProfileToken());
        request.setPresetToken(presetToken);

        response = (GotoPresetResponse) soap.createSOAPPtzRequest(request, response);
        return response != null;
    }

    public void setAbsolutePan(float panValue) {
        currentPanPercentage = panValue;
        currentPanCamValue = ((((panRangeMin - panRangeMax) * -1) / 100) * panValue + panRangeMin);
        absoluteMove(currentPanCamValue, currentTiltCamValue, currentZoomCamValue);
    }

    public void setAbsoluteTilt(float tiltValue) {
        currentTiltPercentage = tiltValue;
        currentTiltCamValue = ((((panRangeMin - panRangeMax) * -1) / 100) * tiltValue + tiltRangeMin);
        absoluteMove(currentPanCamValue, currentTiltCamValue, currentZoomCamValue);
    }

    public void setAbsoluteZoom(Float zoomValue) {// Value is 0-100% of cameras range
        currentZoomPercentage = zoomValue;
        currentZoomCamValue = ((((zoomMin - zoomMax) * -1) / 100) * zoomValue + zoomMin);
        absoluteMove(currentPanCamValue, currentTiltCamValue, currentZoomCamValue);
    }

    public void moveLeft(boolean continuous) {
        if (continuous) {
            continuousMove(-0.5f, 0f, 0f);
        } else
            relativeMove(0.05000000f, 0f, 0f);
    }

    public void moveRight(boolean continuous) {
        if (continuous) {
            continuousMove(0.5f, 0f, 0f);
        } else {
            relativeMove(-0.05000000f, 0f, 0f);
        }
    }

    public void moveUp(boolean continuous) {
        if (continuous) {
            continuousMove(0f, -0f, 0f);
        } else {
            relativeMove(0f, 0.100000000f, 0f);
        }
    }

    public void moveDown(boolean continuous) {
        if (continuous) {
            continuousMove(0f, 0.5f, 0f);
        } else {
            relativeMove(0f, -0.100000000f, 0f);
        }
    }

    public void moveIn(boolean continuous) {
        if (continuous) {
            continuousMove(0f, 0f, 0.5f);
        } else {
            relativeMove(0f, 0f, 0.0240506344f);
        }
    }

    public void moveOut(boolean continuous) {
        if (continuous) {
            continuousMove(0f, 0f, -0.5f);
        } else {
            relativeMove(0f, 0f, -0.0240506344f);
        }
    }

    public void initFully() {
        PTZStatus ptzStatus = getStatus();
        this.presets = getPresets();

        currentPanCamValue = ptzStatus.getPosition().getPanTilt().getX();
        currentPanPercentage = (((panRangeMin - currentPanCamValue) * -1) / ((panRangeMin - panRangeMax) * -1)) * 100;

        currentTiltCamValue = ptzStatus.getPosition().getPanTilt().getY();
        currentTiltPercentage = (((tiltRangeMin - currentTiltCamValue) * -1) / ((tiltRangeMin - tiltRangeMax) * -1)) * 100;

        currentZoomCamValue = ptzStatus.getPosition().getZoom().getX();
        currentZoomPercentage = (((zoomMin - currentZoomCamValue) * -1) / ((zoomMin - zoomMax) * -1)) * 100;
    }

    public void gotoPreset(int index) {
        gotoPreset(getPresets().get(index).getToken());
    }

    public boolean supportPTZ() {
        return onvifDeviceState.getServerPtzUri() != null;
    }

    public void dispose() {
        presets = null;
        panRangeMin = -1.0f;
        panRangeMax = 1.0f;
        tiltRangeMin = -1.0f;
        tiltRangeMax = 1.0f;
        zoomMin = 0.0f;
        zoomMax = 1.0f;

        currentPanPercentage = 0.0f;
        currentTiltPercentage = 0.0f;
        currentZoomPercentage = 0.0f;
        currentPanCamValue = 0.0f;
        currentTiltCamValue = 0.0f;
        currentZoomCamValue = 0.0f;
    }
}
