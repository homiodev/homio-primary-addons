package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.PTZSpeed;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken", "speed"})
@XmlRootElement(name = "GotoHomePosition")
public class GotoHomePosition {


    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;


    @XmlElement(name = "Speed")
    protected PTZSpeed speed;


    public void setProfileToken(String value) {
        this.profileToken = value;
    }


    public void setSpeed(PTZSpeed value) {
        this.speed = value;
    }
}
