package org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java-Klasse f�r WideDynamicRangeOptions complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="WideDynamicRangeOptions">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Mode" type="{http://www.onvif.org/ver10/schema}WideDynamicMode" maxOccurs="unbounded"/>
 *         <element name="Level" type="{http://www.onvif.org/ver10/schema}FloatRange"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "WideDynamicRangeOptions",
    propOrder = {"mode", "level"})
public class WideDynamicRangeOptions {

    @XmlElement(name = "Mode", required = true)
    protected List<WideDynamicMode> mode;

    @XmlElement(name = "Level", required = true)
    protected FloatRange level;

    /**
     * Gets the value of the mode property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the mode
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getMode().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link WideDynamicMode }
     */
    public List<WideDynamicMode> getMode() {
        if (mode == null) {
            mode = new ArrayList<WideDynamicMode>();
        }
        return this.mode;
    }

    /**
     * Ruft den Wert der level-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    public FloatRange getLevel() {
        return level;
    }

    /**
     * Legt den Wert der level-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setLevel(FloatRange value) {
        this.level = value;
    }
}