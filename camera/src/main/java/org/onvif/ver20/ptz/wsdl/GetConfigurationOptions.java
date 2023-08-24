package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurationToken"})
@XmlRootElement(name = "GetConfigurationOptions")
public class GetConfigurationOptions {


    @XmlElement(name = "ConfigurationToken", required = true)
    protected String configurationToken;


    public void setConfigurationToken(String value) {
        this.configurationToken = value;
    }
}
