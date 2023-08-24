package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r StorageReferencePath complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * &lt;complexType name="StorageReferencePath">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StorageToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
 *         &lt;element name="RelativePath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Extension" type="{http://www.onvif.org/ver10/schema}StorageReferencePathExtension" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "StorageReferencePath",
        propOrder = {"storageToken", "relativePath", "extension"})
public class StorageReferencePath {

    /**
     * -- GETTER --
     *  Ruft den Wert der storageToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "StorageToken", required = true)
    protected String storageToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der relativePath-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "RelativePath")
    protected String relativePath;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link StorageReferencePathExtension }
     */
    @XmlElement(name = "Extension")
    protected StorageReferencePathExtension extension;

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
     * Legt den Wert der storageToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setStorageToken(String value) {
        this.storageToken = value;
    }

    /**
     * Legt den Wert der relativePath-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setRelativePath(String value) {
        this.relativePath = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link StorageReferencePathExtension }
     */
    public void setExtension(StorageReferencePathExtension value) {
        this.extension = value;
    }

}
