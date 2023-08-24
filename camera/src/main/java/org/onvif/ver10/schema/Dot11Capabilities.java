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
 * Java-Klasse f�r Dot11Capabilities complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Dot11Capabilities">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="TKIP" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="ScanAvailableNetworks" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="MultipleConfiguration" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="AdHocStationMode" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="WEP" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
        name = "Dot11Capabilities",
        propOrder = {
                "tkip",
                "scanAvailableNetworks",
                "multipleConfiguration",
                "adHocStationMode",
                "wep",
                "any"
        })
public class Dot11Capabilities {

    @XmlElement(name = "TKIP")
    protected boolean tkip;

    /**
     * -- GETTER --
     *  Ruft den Wert der scanAvailableNetworks-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "ScanAvailableNetworks")
    protected boolean scanAvailableNetworks;

    /**
     * -- GETTER --
     *  Ruft den Wert der multipleConfiguration-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "MultipleConfiguration")
    protected boolean multipleConfiguration;

    /**
     * -- GETTER --
     *  Ruft den Wert der adHocStationMode-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "AdHocStationMode")
    protected boolean adHocStationMode;

    @XmlElement(name = "WEP")
    protected boolean wep;

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
     * Ruft den Wert der tkip-Eigenschaft ab.
     */
    public boolean isTKIP() {
        return tkip;
    }

    /**
     * Legt den Wert der tkip-Eigenschaft fest.
     */
    public void setTKIP(boolean value) {
        this.tkip = value;
    }

    /**
     * Legt den Wert der scanAvailableNetworks-Eigenschaft fest.
     */
    public void setScanAvailableNetworks(boolean value) {
        this.scanAvailableNetworks = value;
    }

    /**
     * Legt den Wert der multipleConfiguration-Eigenschaft fest.
     */
    public void setMultipleConfiguration(boolean value) {
        this.multipleConfiguration = value;
    }

    /**
     * Legt den Wert der adHocStationMode-Eigenschaft fest.
     */
    public void setAdHocStationMode(boolean value) {
        this.adHocStationMode = value;
    }

    /**
     * Ruft den Wert der wep-Eigenschaft ab.
     */
    public boolean isWEP() {
        return wep;
    }

    /**
     * Legt den Wert der wep-Eigenschaft fest.
     */
    public void setWEP(boolean value) {
        this.wep = value;
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
