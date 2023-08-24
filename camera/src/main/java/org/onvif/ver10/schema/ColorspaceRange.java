package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ColorspaceRange",
        propOrder = {"x", "y", "z", "colorspace"})
public class ColorspaceRange {

    /**
     * -- GETTER --
     *  Ruft den Wert der x-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @XmlElement(name = "X", required = true)
    protected FloatRange x;

    /**
     * -- GETTER --
     *  Ruft den Wert der y-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @XmlElement(name = "Y", required = true)
    protected FloatRange y;

    /**
     * -- GETTER --
     *  Ruft den Wert der z-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @XmlElement(name = "Z", required = true)
    protected FloatRange z;

    /**
     * -- GETTER --
     *  Ruft den Wert der colorspace-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Colorspace", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String colorspace;

    /**
     * -- GETTER --
     *  Gets a map that contains attributes that aren't bound to any typed property on this class.
     *  <p>the map is keyed by the name of the attribute and the value is the string value of the
     *  attribute.
     *  <p>the map returned by this method is live, and you can add new attribute by updating the map
     *  directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Legt den Wert der x-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setX(FloatRange value) {
        this.x = value;
    }

    /**
     * Legt den Wert der y-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setY(FloatRange value) {
        this.y = value;
    }

    /**
     * Legt den Wert der z-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setZ(FloatRange value) {
        this.z = value;
    }

    /**
     * Legt den Wert der colorspace-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setColorspace(String value) {
        this.colorspace = value;
    }

}
