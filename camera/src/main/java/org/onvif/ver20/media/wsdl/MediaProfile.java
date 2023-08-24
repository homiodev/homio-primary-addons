package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "MediaProfile",
        propOrder = {"name", "configurations"})
public class MediaProfile {


    @Getter @XmlElement(name = "Name", required = true)
    protected String name;


    @Getter @XmlElement(name = "Configurations", required = true)
    protected ConfigurationSet configurations;


    @Getter @XmlAttribute(name = "token", required = true)
    protected String token;

    @XmlAttribute(name = "fixed")
    protected Boolean fixed;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setName(String value) {
        this.name = value;
    }


    public void setConfigurations(ConfigurationSet value) {
        this.configurations = value;
    }


    public void setToken(String value) {
        this.token = value;
    }


    public Boolean isFixed() {
        return fixed;
    }


    public void setFixed(Boolean value) {
        this.fixed = value;
    }

}
