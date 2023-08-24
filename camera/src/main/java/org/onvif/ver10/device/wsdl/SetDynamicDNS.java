







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import org.onvif.ver10.schema.DynamicDNSType;

import javax.xml.datatype.Duration;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"type", "name", "ttl"})
@XmlRootElement(name = "SetDynamicDNS")
public class SetDynamicDNS {


    @XmlElement(name = "Type", required = true)
    protected DynamicDNSType type;


    @Getter @XmlElement(name = "Name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String name;

    @XmlElement(name = "TTL")
    protected Duration ttl;


    public void setType(DynamicDNSType value) {
        this.type = value;
    }


    public void setName(String value) {
        this.name = value;
    }


    public Duration getTTL() {
        return ttl;
    }


    public void setTTL(Duration value) {
        this.ttl = value;
    }
}
