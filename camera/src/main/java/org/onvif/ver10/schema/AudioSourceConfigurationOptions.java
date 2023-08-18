package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AudioSourceConfigurationOptions",
        propOrder = {"inputTokensAvailable", "extension"})
public class AudioSourceConfigurationOptions {

    @XmlElement(name = "InputTokensAvailable", required = true)
    protected List<String> inputTokensAvailable;

    @XmlElement(name = "Extension")
    protected AudioSourceOptionsExtension extension;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the inputTokensAvailable property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the
     * inputTokensAvailable property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getInputTokensAvailable().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link String }
     */
    public List<String> getInputTokensAvailable() {
        if (inputTokensAvailable == null) {
            inputTokensAvailable = new ArrayList<String>();
        }
        return this.inputTokensAvailable;
    }

    /**
     * Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link AudioSourceOptionsExtension }
     */
    public AudioSourceOptionsExtension getExtension() {
        return extension;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link AudioSourceOptionsExtension }
     */
    public void setExtension(AudioSourceOptionsExtension value) {
        this.extension = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>the map is keyed by the name of the attribute and the value is the string value of the
     * attribute.
     *
     * <p>the map returned by this method is live, and you can add new attribute by updating the map
     * directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }
}
