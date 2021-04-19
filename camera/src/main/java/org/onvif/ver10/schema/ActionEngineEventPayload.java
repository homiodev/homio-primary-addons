package org.onvif.ver10.schema;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.xmlsoap.schemas.soap.envelope.Envelope;
import org.xmlsoap.schemas.soap.envelope.Fault;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionEngineEventPayload", propOrder = { "requestInfo", "responseInfo", "fault", "extension" })
public class ActionEngineEventPayload {

	@XmlElement(name = "RequestInfo")
	protected Envelope requestInfo;
	@XmlElement(name = "ResponseInfo")
	protected Envelope responseInfo;
	@XmlElement(name = "Fault")
	protected Fault fault;
	@XmlElement(name = "Extension")
	protected ActionEngineEventPayloadExtension extension;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	/**
	 * Ruft den Wert der requestInfo-Eigenschaft ab.
	 *
	 * @return possible object is {@link Envelope }
	 *
	 */
	public Envelope getRequestInfo() {
		return requestInfo;
	}

	/**
	 * Legt den Wert der requestInfo-Eigenschaft fest.
	 *
	 * @param value
	 *            allowed object is {@link Envelope }
	 *
	 */
	public void setRequestInfo(Envelope value) {
		this.requestInfo = value;
	}

	/**
	 * Ruft den Wert der responseInfo-Eigenschaft ab.
	 *
	 * @return possible object is {@link Envelope }
	 *
	 */
	public Envelope getResponseInfo() {
		return responseInfo;
	}

	/**
	 * Legt den Wert der responseInfo-Eigenschaft fest.
	 *
	 * @param value
	 *            allowed object is {@link Envelope }
	 *
	 */
	public void setResponseInfo(Envelope value) {
		this.responseInfo = value;
	}

	/**
	 * Ruft den Wert der fault-Eigenschaft ab.
	 *
	 * @return possible object is {@link Fault }
	 *
	 */
	public Fault getFault() {
		return fault;
	}

	/**
	 * Legt den Wert der fault-Eigenschaft fest.
	 *
	 * @param value
	 *            allowed object is {@link Fault }
	 *
	 */
	public void setFault(Fault value) {
		this.fault = value;
	}

	/**
	 * Ruft den Wert der extension-Eigenschaft ab.
	 *
	 * @return possible object is {@link ActionEngineEventPayloadExtension }
	 *
	 */
	public ActionEngineEventPayloadExtension getExtension() {
		return extension;
	}

	/**
	 * Legt den Wert der extension-Eigenschaft fest.
	 *
	 * @param value
	 *            allowed object is {@link ActionEngineEventPayloadExtension }
	 *
	 */
	public void setExtension(ActionEngineEventPayloadExtension value) {
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
