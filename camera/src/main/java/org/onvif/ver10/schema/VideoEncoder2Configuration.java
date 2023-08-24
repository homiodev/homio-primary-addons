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
 * Java-Klasse f�r VideoEncoder2Configuration complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * &lt;complexType name="VideoEncoder2Configuration">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.onvif.org/ver10/schema}ConfigurationEntity">
 *       &lt;sequence>
 *         &lt;element name="Encoding" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Resolution" type="{http://www.onvif.org/ver10/schema}VideoResolution2"/>
 *         &lt;element name="RateControl" type="{http://www.onvif.org/ver10/schema}VideoRateControl2" minOccurs="0"/>
 *         &lt;element name="Multicast" type="{http://www.onvif.org/ver10/schema}MulticastConfiguration" minOccurs="0"/>
 *         &lt;element name="Quality" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="GovLength" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="Profile" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "VideoEncoder2Configuration",
        propOrder = {"encoding", "resolution", "rateControl", "multicast", "quality", "any"})
public class VideoEncoder2Configuration extends ConfigurationEntity {

    /**
     * -- GETTER --
     *  Ruft den Wert der encoding-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "Encoding", required = true)
    protected String encoding;

    /**
     * -- GETTER --
     *  Ruft den Wert der resolution-Eigenschaft ab.
     *
     * @return possible object is {@link VideoResolution2 }
     */
    @Getter @XmlElement(name = "Resolution", required = true)
    protected VideoResolution2 resolution;

    /**
     * -- GETTER --
     *  Ruft den Wert der rateControl-Eigenschaft ab.
     *
     * @return possible object is {@link VideoRateControl2 }
     */
    @Getter @XmlElement(name = "RateControl")
    protected VideoRateControl2 rateControl;

    /**
     * -- GETTER --
     *  Ruft den Wert der multicast-Eigenschaft ab.
     *
     * @return possible object is {@link MulticastConfiguration }
     */
    @Getter @XmlElement(name = "Multicast")
    protected MulticastConfiguration multicast;

    /**
     * -- GETTER --
     *  Ruft den Wert der quality-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "Quality")
    protected float quality;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    /**
     * -- GETTER --
     *  Ruft den Wert der govLength-Eigenschaft ab.
     *
     * @return possible object is {@link Integer }
     */
    @Getter @XmlAttribute(name = "GovLength")
    protected Integer govLength;

    /**
     * -- GETTER --
     *  Ruft den Wert der profile-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlAttribute(name = "Profile")
    protected String profile;

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
     * Legt den Wert der encoding-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setEncoding(String value) {
        this.encoding = value;
    }

    /**
     * Legt den Wert der resolution-Eigenschaft fest.
     *
     * @param value allowed object is {@link VideoResolution2 }
     */
    public void setResolution(VideoResolution2 value) {
        this.resolution = value;
    }

    /**
     * Legt den Wert der rateControl-Eigenschaft fest.
     *
     * @param value allowed object is {@link VideoRateControl2 }
     */
    public void setRateControl(VideoRateControl2 value) {
        this.rateControl = value;
    }

    /**
     * Legt den Wert der multicast-Eigenschaft fest.
     *
     * @param value allowed object is {@link MulticastConfiguration }
     */
    public void setMulticast(MulticastConfiguration value) {
        this.multicast = value;
    }

    /**
     * Legt den Wert der quality-Eigenschaft fest.
     */
    public void setQuality(float value) {
        this.quality = value;
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
     *    getAny().add(newItem);
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
     * Legt den Wert der govLength-Eigenschaft fest.
     *
     * @param value allowed object is {@link Integer }
     */
    public void setGovLength(Integer value) {
        this.govLength = value;
    }

    /**
     * Legt den Wert der profile-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setProfile(String value) {
        this.profile = value;
    }

}
