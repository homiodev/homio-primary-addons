package de.onvif.soap.devices;

import de.onvif.soap.OnvifDeviceState;
import de.onvif.soap.SOAP;
import lombok.RequiredArgsConstructor;
import org.onvif.ver10.schema.AbsoluteFocus;
import org.onvif.ver10.schema.FocusMove;
import org.onvif.ver10.schema.ImagingOptions20;
import org.onvif.ver10.schema.ImagingSettings20;
import org.onvif.ver20.imaging.wsdl.*;

@RequiredArgsConstructor
public class ImagingDevices {

    private final OnvifDeviceState onvifDeviceState;
    private final SOAP soap;

    public ImagingOptions20 getOptions(String videoSourceToken) {
        if (videoSourceToken == null) {
            return null;
        }

        GetOptions request = new GetOptions();
        GetOptionsResponse response = new GetOptionsResponse();

        request.setVideoSourceToken(videoSourceToken);

        response = (GetOptionsResponse) soap.createSOAPImagingRequest(request, response);
        return  response == null ? null: response.getImagingOptions();
    }

    public boolean moveFocus(String videoSourceToken, float absoluteFocusValue) {
        if (videoSourceToken == null) {
            return false;
        }

        Move request = new Move();
        MoveResponse response = new MoveResponse();

        AbsoluteFocus absoluteFocus = new AbsoluteFocus();
        absoluteFocus.setPosition(absoluteFocusValue);

        FocusMove focusMove = new FocusMove();
        focusMove.setAbsolute(absoluteFocus);

        request.setVideoSourceToken(videoSourceToken);
        request.setFocus(focusMove);

        response = (MoveResponse) soap.createSOAPImagingRequest(request, response);

        return response != null;
    }

    public ImagingSettings20 getImagingSettings(String videoSourceToken) {
        if (videoSourceToken == null) {
            return null;
        }

        GetImagingSettings request = new GetImagingSettings();
        GetImagingSettingsResponse response = new GetImagingSettingsResponse();

        request.setVideoSourceToken(videoSourceToken);

        response = (GetImagingSettingsResponse) soap.createSOAPImagingRequest(request, response);
        return  response == null ? null : response.getImagingSettings();
    }

    public boolean setImagingSettings(String videoSourceToken, ImagingSettings20 imagingSettings) {
        if (videoSourceToken == null) {
            return false;
        }

        SetImagingSettings request = new SetImagingSettings();
        SetImagingSettingsResponse response = new SetImagingSettingsResponse();

        request.setVideoSourceToken(videoSourceToken);
        request.setImagingSettings(imagingSettings);

        response = (SetImagingSettingsResponse) soap.createSOAPImagingRequest(request, response);
        return response != null;
    }

    public void dispose() {

    }
}
