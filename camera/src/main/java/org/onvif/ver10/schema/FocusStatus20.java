package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r FocusStatus20 complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="FocusStatus20">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Position" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         <element name="MoveStatus" type="{http://www.onvif.org/ver10/schema}MoveStatus"/>
 *         <element name="Error" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}FocusStatus20Extension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "FocusStatus20",
        propOrder = {"position", "moveStatus", "error", "extension"})
public class FocusStatus20 {

    /**
     * -- GETTER --
     *  Ruft den Wert der position-Eigenschaft ab.
     */
    @XmlElement(name = "Position")
    protected float position;

    /**
     * -- GETTER --
     *  Ruft den Wert der moveStatus-Eigenschaft ab.
     *
     * @return possible object is {@link MoveStatus }
     */
    @XmlElement(name = "MoveStatus", required = true)
    protected MoveStatus moveStatus;

    /**
     * -- GETTER --
     *  Ruft den Wert der error-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Error")
    protected String error;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link FocusStatus20Extension }
     */
    @XmlElement(name = "Extension")
    protected FocusStatus20Extension extension;

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
     * Legt den Wert der position-Eigenschaft fest.
     */
    public void setPosition(float value) {
        this.position = value;
    }

    /**
     * Legt den Wert der moveStatus-Eigenschaft fest.
     *
     * @param value allowed object is {@link MoveStatus }
     */
    public void setMoveStatus(MoveStatus value) {
        this.moveStatus = value;
    }

    /**
     * Legt den Wert der error-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setError(String value) {
        this.error = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link FocusStatus20Extension }
     */
    public void setExtension(FocusStatus20Extension value) {
        this.extension = value;
    }

}
