//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.02.04 um 12:22:03 PM CET 
//

package org.onvif.ver10.device.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.NetworkInterfaceSetConfiguration;

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
 *         <element name="InterfaceToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
 *         <element name="NetworkInterface" type="{http://www.onvif.org/ver10/schema}NetworkInterfaceSetConfiguration"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "interfaceToken", "networkInterface" })
@XmlRootElement(name = "SetNetworkInterfaces")
public class SetNetworkInterfaces {

	@XmlElement(name = "InterfaceToken", required = true)
	protected String interfaceToken;
	@XmlElement(name = "NetworkInterface", required = true)
	protected NetworkInterfaceSetConfiguration networkInterface;

	/**
	 * Ruft den Wert der interfaceToken-Eigenschaft ab.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getInterfaceToken() {
		return interfaceToken;
	}

	/**
	 * Legt den Wert der interfaceToken-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setInterfaceToken(String value) {
		this.interfaceToken = value;
	}

	/**
	 * Ruft den Wert der networkInterface-Eigenschaft ab.
	 * 
	 * @return possible object is {@link NetworkInterfaceSetConfiguration }
	 * 
	 */
	public NetworkInterfaceSetConfiguration getNetworkInterface() {
		return networkInterface;
	}

	/**
	 * Legt den Wert der networkInterface-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link NetworkInterfaceSetConfiguration }
	 * 
	 */
	public void setNetworkInterface(NetworkInterfaceSetConfiguration value) {
		this.networkInterface = value;
	}

}
