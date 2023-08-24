package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"name", "configuration"})
@XmlRootElement(name = "CreateProfile")
public class CreateProfile {


    @Getter @XmlElement(name = "Name", required = true)
    protected String name;

    @XmlElement(name = "Configuration")
    protected List<ConfigurationRef> configuration;


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
