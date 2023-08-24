package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "NetworkHost",
        propOrder = {"type", "iPv4Address", "iPv6Address", "dnSname", "extension"})
public class NetworkHost {


    @Getter @XmlElement(name = "Type", required = true)
    protected NetworkHostType type;

    @XmlElement(name = "IPv4Address")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String iPv4Address;

    @XmlElement(name = "IPv6Address")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String iPv6Address;

    @XmlElement(name = "DNSname")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String dnSname;


    @Getter @XmlElement(name = "Extension")
    protected NetworkHostExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setType(NetworkHostType value) {
        this.type = value;
    }


    public String getIPv4Address() {
        return iPv4Address;
    }


    public void setIPv4Address(String value) {
        this.iPv4Address = value;
    }


    public String getIPv6Address() {
        return iPv6Address;
    }


    public void setIPv6Address(String value) {
        this.iPv6Address = value;
    }


    public String getDNSname() {
        return dnSname;
    }


    public void setDNSname(String value) {
        this.dnSname = value;
    }


    public void setExtension(NetworkHostExtension value) {
        this.extension = value;
    }

}
