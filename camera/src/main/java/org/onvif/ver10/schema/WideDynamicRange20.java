package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Type describing whether WDR mode is enabled or disabled (on/off).
 *
 * <p>Java-Klasse f�r WideDynamicRange20 complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="WideDynamicRange20">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Mode" type="{http://www.onvif.org/ver10/schema}WideDynamicMode"/>
 *         <element name="Level" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "WideDynamicRange20",
        propOrder = {"mode", "level"})
public class WideDynamicRange20 {

    /**
     * -- GETTER --
     *  Ruft den Wert der mode-Eigenschaft ab.
     *
     * @return possible object is {@link WideDynamicMode }
     */
    @XmlElement(name = "Mode", required = true)
    protected WideDynamicMode mode;

    /**
     * -- GETTER --
     *  Ruft den Wert der level-Eigenschaft ab.
     *
     * @return possible object is {@link Float }
     */
    @XmlElement(name = "Level")
    protected Float level;

    /**
     * Legt den Wert der mode-Eigenschaft fest.
     *
     * @param value allowed object is {@link WideDynamicMode }
     */
    public void setMode(WideDynamicMode value) {
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
}
