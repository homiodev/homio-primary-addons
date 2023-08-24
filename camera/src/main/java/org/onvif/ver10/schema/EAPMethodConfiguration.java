package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "EAPMethodConfiguration",
        propOrder = {"tlsConfiguration", "password", "extension"})
public class EAPMethodConfiguration {

    @XmlElement(name = "TLSConfiguration")
    protected TLSConfiguration tlsConfiguration;

    
    @Getter @XmlElement(name = "Password")
    protected String password;

    
    @Getter @XmlElement(name = "Extension")
    protected EapMethodExtension extension;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public TLSConfiguration getTLSConfiguration() {
        return tlsConfiguration;
    }

    
    public void setTLSConfiguration(TLSConfiguration value) {
        this.tlsConfiguration = value;
    }

    
    public void setPassword(String value) {
        this.password = value;
    }

    
    public void setExtension(EapMethodExtension value) {
        this.extension = value;
    }

}
