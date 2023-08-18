package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.OSDConfiguration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"osd"})
@XmlRootElement(name = "CreateOSD")
public class CreateOSD {

    @XmlElement(name = "OSD", required = true)
    protected OSDConfiguration osd;

    /**
     * Ruft den Wert der osd-Eigenschaft ab.
     *
     * @return possible object is {@link OSDConfiguration }
     */
    public OSDConfiguration getOSD() {
        return osd;
    }

    /**
     * Legt den Wert der osd-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDConfiguration }
     */
    public void setOSD(OSDConfiguration value) {
        this.osd = value;
    }
}
