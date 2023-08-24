package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"profileToken", "name", "configuration"})
@XmlRootElement(name = "AddConfiguration")
public class AddConfiguration {

    
    @Getter @XmlElement(name = "ProfileToken", required = true)
    protected String profileToken;

    
    @Getter @XmlElement(name = "Name")
    protected String name;

    @XmlElement(name = "Configuration")
    protected List<ConfigurationRef> configuration;

    
    public void setProfileToken(String value) {
        this.profileToken = value;
    }

    
    public void setName(String value) {
        this.name = value;
    }

    
    public List<ConfigurationRef> getConfiguration() {
        if (configuration == null) {
            configuration = new ArrayList<ConfigurationRef>();
        }
        return this.configuration;
    }
}
