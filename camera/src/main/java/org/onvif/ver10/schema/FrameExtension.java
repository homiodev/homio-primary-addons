package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Java-Klasse f�r FrameExtension complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="FrameExtension">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="MotionInCells" type="{http://www.onvif.org/ver10/schema}MotionInCells" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}FrameExtension2" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "FrameExtension",
        propOrder = {"any", "motionInCells", "extension"})
public class FrameExtension {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    /**
     * -- GETTER --
     *  Ruft den Wert der motionInCells-Eigenschaft ab.
     *
     * @return possible object is {@link MotionInCells }
     */
    @Getter @XmlElement(name = "MotionInCells")
    protected MotionInCells motionInCells;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link FrameExtension2 }
     */
    @Getter @XmlElement(name = "Extension")
    protected FrameExtension2 extension;

    /**
     * Gets the value of the any property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the any
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getAny().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link Element } {@link
     * java.lang.Object }
     */
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

    /**
     * Legt den Wert der motionInCells-Eigenschaft fest.
     *
     * @param value allowed object is {@link MotionInCells }
     */
    public void setMotionInCells(MotionInCells value) {
        this.motionInCells = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link FrameExtension2 }
     */
    public void setExtension(FrameExtension2 value) {
        this.extension = value;
    }
}
