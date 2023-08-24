







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurationToken", "profileToken"})
@XmlRootElement(name = "GetVideoEncoderConfigurationOptions")
public class GetVideoEncoderConfigurationOptions {


    @XmlElement(name = "ConfigurationToken")
    protected String configurationToken;


    @XmlElement(name = "ProfileToken")
    protected String profileToken;


    public void setConfigurationToken(String value) {
        this.configurationToken = value;
    }


    public void setProfileToken(String value) {
        this.profileToken = value;
    }
}
