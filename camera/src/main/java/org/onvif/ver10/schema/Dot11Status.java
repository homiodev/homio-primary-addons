package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.HexBinaryAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Dot11Status",
        propOrder = {
                "ssid",
                "bssid",
                "pairCipher",
                "groupCipher",
                "signalStrength",
                "activeConfigAlias",
                "any"
        })
public class Dot11Status {

    @XmlElement(name = "SSID", required = true, type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    protected byte[] ssid;

    @XmlElement(name = "BSSID")
    protected String bssid;


    @Getter @XmlElement(name = "PairCipher")
    protected Dot11Cipher pairCipher;


    @Getter @XmlElement(name = "GroupCipher")
    protected Dot11Cipher groupCipher;


    @Getter @XmlElement(name = "SignalStrength")
    protected Dot11SignalStrength signalStrength;


    @Getter @XmlElement(name = "ActiveConfigAlias", required = true)
    protected String activeConfigAlias;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public byte[] getSSID() {
        return ssid;
    }


    public void setSSID(byte[] value) {
        this.ssid = value;
    }


    public String getBSSID() {
        return bssid;
    }


    public void setBSSID(String value) {
        this.bssid = value;
    }


    public void setPairCipher(Dot11Cipher value) {
        this.pairCipher = value;
    }


    public void setGroupCipher(Dot11Cipher value) {
        this.groupCipher = value;
    }


    public void setSignalStrength(Dot11SignalStrength value) {
        this.signalStrength = value;
    }


    public void setActiveConfigAlias(String value) {
        this.activeConfigAlias = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
