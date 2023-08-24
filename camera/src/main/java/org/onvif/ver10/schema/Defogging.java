package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Defogging",
        propOrder = {"mode", "level", "extension"})
public class Defogging {

    /**
     * -- GETTER --
     *  Ruft den Wert der mode-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Mode", required = true)
    protected String mode;

    /**
     * -- GETTER --
     *  Ruft den Wert der level-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "Level")
    protected Float level;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link DefoggingExtension }
     */
    @XmlElement(name = "Extension")
    protected DefoggingExtension extension;

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
     * Legt den Wert der mode-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setMode(String value) {
        this.mode = value;
    }

    /**
     * Legt den Wert der level-Eigenschaft fest.
     *
     * @param value allowed object is {@link Float }
     */
    public void setLevel(Float value) {
        this.level = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link DefoggingExtension }
     */
    public void setExtension(DefoggingExtension value) {
        this.extension = value;
    }

}
