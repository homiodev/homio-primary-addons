package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration of the streaming and coding settings of a Video window.
 *
 * <p>Java-Klasse f�r PaneConfiguration complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="PaneConfiguration">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="PaneName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="AudioOutputToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken" minOccurs="0"/>
 *         <element name="AudioSourceToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken" minOccurs="0"/>
 *         <element name="AudioEncoderConfiguration" type="{http://www.onvif.org/ver10/schema}AudioEncoderConfiguration" minOccurs="0"/>
 *         <element name="ReceiverToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken" minOccurs="0"/>
 *         <element name="Token" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
 *         <any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PaneConfiguration",
        propOrder = {
                "paneName",
                "audioOutputToken",
                "audioSourceToken",
                "audioEncoderConfiguration",
                "receiverToken",
                "token",
                "any"
        })
public class PaneConfiguration {

    /**
     * -- GETTER --
     *  Ruft den Wert der paneName-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "PaneName")
    protected String paneName;

    /**
     * -- GETTER --
     *  Ruft den Wert der audioOutputToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "AudioOutputToken")
    protected String audioOutputToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der audioSourceToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "AudioSourceToken")
    protected String audioSourceToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der audioEncoderConfiguration-Eigenschaft ab.
     *
     * @return possible object is {@link AudioEncoderConfiguration }
     */
    @Getter @XmlElement(name = "AudioEncoderConfiguration")
    protected AudioEncoderConfiguration audioEncoderConfiguration;

    /**
     * -- GETTER --
     *  Ruft den Wert der receiverToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "ReceiverToken")
    protected String receiverToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der token-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "Token", required = true)
    protected String token;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

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
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Legt den Wert der paneName-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setPaneName(String value) {
        this.paneName = value;
    }

    /**
     * Legt den Wert der audioOutputToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setAudioOutputToken(String value) {
        this.audioOutputToken = value;
    }

    /**
     * Legt den Wert der audioSourceToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setAudioSourceToken(String value) {
        this.audioSourceToken = value;
    }

    /**
     * Legt den Wert der audioEncoderConfiguration-Eigenschaft fest.
     *
     * @param value allowed object is {@link AudioEncoderConfiguration }
     */
    public void setAudioEncoderConfiguration(AudioEncoderConfiguration value) {
        this.audioEncoderConfiguration = value;
    }

    /**
     * Legt den Wert der receiverToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setReceiverToken(String value) {
        this.receiverToken = value;
    }

    /**
     * Legt den Wert der token-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setToken(String value) {
        this.token = value;
    }

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

}
