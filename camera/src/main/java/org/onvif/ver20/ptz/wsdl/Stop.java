package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken", "panTilt", "zoom"})
@XmlRootElement(name = "Stop")
public class Stop {


    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;

    @XmlElement(name = "PanTilt")
    protected Boolean panTilt;

    @XmlElement(name = "Zoom")
    protected Boolean zoom;


    public void setProfileToken(String value) {
        this.profileToken = value;
    }


    public Boolean isPanTilt() {
        return panTilt;
    }


    public void setPanTilt(Boolean value) {
        this.panTilt = value;
    }


    public Boolean isZoom() {
        return zoom;
    }


    public void setZoom(Boolean value) {
        this.zoom = value;
    }
}
