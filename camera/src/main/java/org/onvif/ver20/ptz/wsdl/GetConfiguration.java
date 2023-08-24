package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"ptzConfigurationToken"})
@XmlRootElement(name = "GetConfiguration")
public class GetConfiguration {

    @XmlElement(name = "PTZConfigurationToken", required = true)
    protected String ptzConfigurationToken;


    public String getPTZConfigurationToken() {
        return ptzConfigurationToken;
    }


    public void setPTZConfigurationToken(String value) {
        this.ptzConfigurationToken = value;
    }
}
