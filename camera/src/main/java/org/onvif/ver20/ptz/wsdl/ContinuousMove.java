package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.PTZSpeed;

import javax.xml.datatype.Duration;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken", "velocity", "timeout"})
@XmlRootElement(name = "ContinuousMove")
public class ContinuousMove {


    @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;


    @XmlElement(name = "Velocity", required = true)
    protected PTZSpeed velocity;


    @XmlElement(name = "Timeout")
    protected Duration timeout;


    public void setProfileToken(String value) {
        this.profileToken = value;
    }


    public void setVelocity(PTZSpeed value) {
        this.velocity = value;
    }


    public void setTimeout(Duration value) {
        this.timeout = value;
    }
}
