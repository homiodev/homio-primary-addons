package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "MediaUri",
        propOrder = {"uri", "invalidAfterConnect", "invalidAfterReboot", "timeout", "any"})
public class MediaUri {


    @XmlElement(name = "Uri", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String uri;


    @Getter @XmlElement(name = "InvalidAfterConnect")
    protected boolean invalidAfterConnect;


    @Getter @XmlElement(name = "InvalidAfterReboot")
    protected boolean invalidAfterReboot;


    @Getter @XmlElement(name = "Timeout", required = true)
    protected Duration timeout;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setUri(String value) {
        this.uri = value;
    }


    public void setInvalidAfterConnect(boolean value) {
        this.invalidAfterConnect = value;
    }


    public void setInvalidAfterReboot(boolean value) {
        this.invalidAfterReboot = value;
    }


    public void setTimeout(Duration value) {
        this.timeout = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
