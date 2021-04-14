package org.onvif.ver10.media.wsdl;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"configurationToken"})
@XmlRootElement(name = "GetMetadataConfiguration")
public class GetMetadataConfiguration {

    @XmlElement(name = "ConfigurationToken", required = true)
    protected String configurationToken;

    public String getConfigurationToken() {
        return configurationToken;
    }

    public void setConfigurationToken(String value) {
        this.configurationToken = value;
    }

}
