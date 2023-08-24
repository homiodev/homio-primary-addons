package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.PTZSpeed;
import org.onvif.ver10.schema.PTZVector;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken", "position", "speed"})
@XmlRootElement(name = "AbsoluteMove")
public class AbsoluteMove {

    
    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;

    
    @XmlElement(name = "Position", required = true)
    protected PTZVector position;

    
    @XmlElement(name = "Speed")
    protected PTZSpeed speed;

    
    public void setProfileToken(String value) {
        this.profileToken = value;
    }

    
    public void setPosition(PTZVector value) {
        this.position = value;
    }

    
    public void setSpeed(PTZSpeed value) {
        this.speed = value;
    }
}
