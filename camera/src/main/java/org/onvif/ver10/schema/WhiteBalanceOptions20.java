package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * Java-Klasse f�r WhiteBalanceOptions20 complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="WhiteBalanceOptions20">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Mode" type="{http://www.onvif.org/ver10/schema}WhiteBalanceMode" maxOccurs="unbounded"/>
 *         <element name="YrGain" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/>
 *         <element name="YbGain" type="{http://www.onvif.org/ver10/schema}FloatRange" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}WhiteBalanceOptions20Extension" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "WhiteBalanceOptions20",
        propOrder = {"mode", "yrGain", "ybGain", "extension"})
public class WhiteBalanceOptions20 {

    @XmlElement(name = "Mode", required = true)
    protected List<WhiteBalanceMode> mode;

    /**
     * -- GETTER --
     *  Ruft den Wert der yrGain-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @Getter @XmlElement(name = "YrGain")
    protected FloatRange yrGain;

    /**
     * -- GETTER --
     *  Ruft den Wert der ybGain-Eigenschaft ab.
     *
     * @return possible object is {@link FloatRange }
     */
    @Getter @XmlElement(name = "YbGain")
    protected FloatRange ybGain;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link WhiteBalanceOptions20Extension }
     */
    @Getter @XmlElement(name = "Extension")
    protected WhiteBalanceOptions20Extension extension;

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
     * <p>Objects of the following type(s) are allowed in the list {@link WhiteBalanceMode }
     */
    public List<WhiteBalanceMode> getMode() {
        if (mode == null) {
            mode = new ArrayList<WhiteBalanceMode>();
        }
        return this.mode;
    }

    /**
     * Legt den Wert der yrGain-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setYrGain(FloatRange value) {
        this.yrGain = value;
    }

    /**
     * Legt den Wert der ybGain-Eigenschaft fest.
     *
     * @param value allowed object is {@link FloatRange }
     */
    public void setYbGain(FloatRange value) {
        this.ybGain = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link WhiteBalanceOptions20Extension }
     */
    public void setExtension(WhiteBalanceOptions20Extension value) {
        this.extension = value;
    }
}
