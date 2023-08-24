package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Dot11SecurityConfiguration",
        propOrder = {"mode", "algorithm", "psk", "dot1X", "extension"})
public class Dot11SecurityConfiguration {


    @Getter @XmlElement(name = "Mode", required = true)
    protected Dot11SecurityMode mode;


    @Getter @XmlElement(name = "Algorithm")
    protected Dot11Cipher algorithm;

    @XmlElement(name = "PSK")
    protected Dot11PSKSet psk;


    @Getter @XmlElement(name = "Dot1X")
    protected String dot1X;


    @Getter @XmlElement(name = "Extension")
    protected Dot11SecurityConfigurationExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setMode(Dot11SecurityMode value) {
        this.mode = value;
    }


    public void setAlgorithm(Dot11Cipher value) {
        this.algorithm = value;
    }


    public Dot11PSKSet getPSK() {
        return psk;
    }


    public void setPSK(Dot11PSKSet value) {
        this.psk = value;
    }


    public void setDot1X(String value) {
        this.dot1X = value;
    }


    public void setExtension(Dot11SecurityConfigurationExtension value) {
        this.extension = value;
    }

}
