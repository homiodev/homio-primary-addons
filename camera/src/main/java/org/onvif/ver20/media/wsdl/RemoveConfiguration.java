package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken", "configuration"})
@XmlRootElement(name = "RemoveConfiguration")
public class RemoveConfiguration {


    @Getter @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;

    @XmlElement(name = "Configuration", required = true)
    protected List<ConfigurationRef> configuration;


    public void setProfileToken(String value) {
        this.profileToken = value;
    }


    public List<ConfigurationRef> getConfiguration() {
        if (configuration == null) {
            configuration = new ArrayList<ConfigurationRef>();
        }
        return this.configuration;
    }
}
