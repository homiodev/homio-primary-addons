







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurationToken"})
@XmlRootElement(name = "GetOSDs")
public class GetOSDs {


    @XmlElement(name = "ConfigurationToken")
    protected String configurationToken;


    public void setConfigurationToken(String value) {
        this.configurationToken = value;
    }
}
