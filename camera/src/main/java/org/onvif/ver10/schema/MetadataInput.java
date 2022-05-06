package org.onvif.ver10.schema;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Java-Klasse f�r MetadataInput complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType name="MetadataInput">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="MetadataConfig" type="{http://www.onvif.org/ver10/schema}Config" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}MetadataInputExtension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataInput", propOrder = { "metadataConfig", "extension" })
public class MetadataInput {

	@XmlElement(name = "MetadataConfig")
	protected List<Config> metadataConfig;
	@XmlElement(name = "Extension")
	protected MetadataInputExtension extension;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	/**
	 * Gets the value of the metadataConfig property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the metadataConfig property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getMetadataConfig().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Config }
	 *
	 *
	 */
	public List<Config> getMetadataConfig() {
		if (metadataConfig == null) {
			metadataConfig = new ArrayList<Config>();
		}
		return this.metadataConfig;
	}

	/**
	 * Ruft den Wert der extension-Eigenschaft ab.
	 *
	 * @return possible object is {@link MetadataInputExtension }
	 *
	 */
	public MetadataInputExtension getExtension() {
		return extension;
	}

	/**
	 * Legt den Wert der extension-Eigenschaft fest.
	 *
	 * @param value
	 *            allowed object is {@link MetadataInputExtension }
	 *
	 */
	public void setExtension(MetadataInputExtension value) {
		this.extension = value;
	}

	/**
	 * Gets a map that contains attributes that aren't bound to any typed property on this class.
	 *
	 * <p>
	 * the map is keyed by the name of the attribute and the value is the string value of the attribute.
	 *
	 * the map returned by this method is live, and you can add new attribute by updating the map directly. Because of this design, there's no setter.
	 *
	 *
	 * @return always non-null
	 */
	public Map<QName, String> getOtherAttributes() {
		return otherAttributes;
	}

}
