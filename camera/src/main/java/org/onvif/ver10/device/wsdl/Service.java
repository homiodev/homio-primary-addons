







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.OnvifVersion;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Service",
        propOrder = {"namespace", "xAddr", "capabilities", "version", "any"})
public class Service {


    @XmlElement(name = "Namespace", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String namespace;

    @XmlElement(name = "XAddr", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String xAddr;


    @Getter @XmlElement(name = "Capabilities")
    protected Service.Capabilities capabilities;


    @Getter @XmlElement(name = "Version", required = true)
    protected OnvifVersion version;

    @XmlAnyElement(lax = true)
    protected List<Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setNamespace(String value) {
        this.namespace = value;
    }


    public String getXAddr() {
        return xAddr;
    }


    public void setXAddr(String value) {
        this.xAddr = value;
    }


    public void setCapabilities(Service.Capabilities value) {
        this.capabilities = value;
    }


    public void setVersion(OnvifVersion value) {
        this.version = value;
    }


    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }


    @Getter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"any"})
    public static class Capabilities {


        @XmlAnyElement(lax = true)
        protected Object any;


        public void setAny(Object value) {
            this.any = value;
        }
    }
}
