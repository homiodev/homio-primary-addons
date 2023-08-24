package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "MulticastConfiguration",
        propOrder = {"address", "port", "ttl", "autoStart", "any"})
public class MulticastConfiguration {


    @XmlElement(name = "Address", required = true)
    protected IPAddress address;


    @Getter @XmlElement(name = "Port")
    protected int port;

    @XmlElement(name = "TTL")
    protected int ttl;


    @Getter @XmlElement(name = "AutoStart")
    protected boolean autoStart;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setAddress(IPAddress value) {
        this.address = value;
    }


    public void setPort(int value) {
        this.port = value;
    }


    public int getTTL() {
        return ttl;
    }


    public void setTTL(int value) {
        this.ttl = value;
    }


    public void setAutoStart(boolean value) {
        this.autoStart = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
