package org.onvif.ver20.ptz.wsdl;

import org.onvif.ver10.schema.PTZConfiguration;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Java-Klasse f�r anonymous complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="PTZConfiguration" type="{http://www.onvif.org/ver10/schema}PTZConfiguration" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "ptzConfiguration" })
@XmlRootElement(name = "GetConfigurationsResponse")
public class GetConfigurationsResponse {

	@XmlElement(name = "PTZConfiguration")
	protected List<PTZConfiguration> ptzConfiguration;

	/**
	 * Gets the value of the ptzConfiguration property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the ptzConfiguration property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getPTZConfiguration().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link PTZConfiguration }
	 *
	 *
	 */
	public List<PTZConfiguration> getPTZConfiguration() {
		if (ptzConfiguration == null) {
			ptzConfiguration = new ArrayList<PTZConfiguration>();
		}
		return this.ptzConfiguration;
	}

}
