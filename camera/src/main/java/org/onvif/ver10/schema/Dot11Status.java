package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.HexBinaryAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java-Klasse f�r Dot11Status complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Dot11Status">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="SSID" type="{http://www.onvif.org/ver10/schema}Dot11SSIDType"/>
 *         <element name="BSSID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="PairCipher" type="{http://www.onvif.org/ver10/schema}Dot11Cipher" minOccurs="0"/>
 *         <element name="GroupCipher" type="{http://www.onvif.org/ver10/schema}Dot11Cipher" minOccurs="0"/>
 *         <element name="SignalStrength" type="{http://www.onvif.org/ver10/schema}Dot11SignalStrength" minOccurs="0"/>
 *         <element name="ActiveConfigAlias" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
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
        name = "Dot11Status",
        propOrder = {
                "ssid",
                "bssid",
                "pairCipher",
                "groupCipher",
                "signalStrength",
                "activeConfigAlias",
                "any"
        })
public class Dot11Status {

    @XmlElement(name = "SSID", required = true, type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    protected byte[] ssid;

    @XmlElement(name = "BSSID")
    protected String bssid;

    /**
     * -- GETTER --
     *  Ruft den Wert der pairCipher-Eigenschaft ab.
     *
     * @return possible object is {@link Dot11Cipher }
     */
    @Getter @XmlElement(name = "PairCipher")
    protected Dot11Cipher pairCipher;

    /**
     * -- GETTER --
     *  Ruft den Wert der groupCipher-Eigenschaft ab.
     *
     * @return possible object is {@link Dot11Cipher }
     */
    @Getter @XmlElement(name = "GroupCipher")
    protected Dot11Cipher groupCipher;

    /**
     * -- GETTER --
     *  Ruft den Wert der signalStrength-Eigenschaft ab.
     *
     * @return possible object is {@link Dot11SignalStrength }
     */
    @Getter @XmlElement(name = "SignalStrength")
    protected Dot11SignalStrength signalStrength;

    /**
     * -- GETTER --
     *  Ruft den Wert der activeConfigAlias-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @Getter @XmlElement(name = "ActiveConfigAlias", required = true)
    protected String activeConfigAlias;

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
     * Ruft den Wert der ssid-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public byte[] getSSID() {
        return ssid;
    }

    /**
     * Legt den Wert der ssid-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setSSID(byte[] value) {
        this.ssid = value;
    }

    /**
     * Ruft den Wert der bssid-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getBSSID() {
        return bssid;
    }

    /**
     * Legt den Wert der bssid-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setBSSID(String value) {
        this.bssid = value;
    }

    /**
     * Legt den Wert der pairCipher-Eigenschaft fest.
     *
     * @param value allowed object is {@link Dot11Cipher }
     */
    public void setPairCipher(Dot11Cipher value) {
        this.pairCipher = value;
    }

    /**
     * Legt den Wert der groupCipher-Eigenschaft fest.
     *
     * @param value allowed object is {@link Dot11Cipher }
     */
    public void setGroupCipher(Dot11Cipher value) {
        this.groupCipher = value;
    }

    /**
     * Legt den Wert der signalStrength-Eigenschaft fest.
     *
     * @param value allowed object is {@link Dot11SignalStrength }
     */
    public void setSignalStrength(Dot11SignalStrength value) {
        this.signalStrength = value;
    }

    /**
     * Legt den Wert der activeConfigAlias-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setActiveConfigAlias(String value) {
        this.activeConfigAlias = value;
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
