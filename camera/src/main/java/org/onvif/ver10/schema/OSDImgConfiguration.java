package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r OSDImgConfiguration complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="OSDImgConfiguration">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="ImgPath" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}OSDImgConfigurationExtension" minOccurs="0"/>
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
        name = "OSDImgConfiguration",
        propOrder = {"imgPath", "extension"})
public class OSDImgConfiguration {

    /**
     * -- GETTER --
     *  Ruft den Wert der imgPath-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "ImgPath", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String imgPath;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link OSDImgConfigurationExtension }
     */
    @XmlElement(name = "Extension")
    protected OSDImgConfigurationExtension extension;

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
     * Legt den Wert der imgPath-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setImgPath(String value) {
        this.imgPath = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link OSDImgConfigurationExtension }
     */
    public void setExtension(OSDImgConfigurationExtension value) {
        this.extension = value;
    }

}
