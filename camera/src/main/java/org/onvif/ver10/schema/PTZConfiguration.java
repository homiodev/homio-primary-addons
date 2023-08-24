package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Java-Klasse f�r PTZConfiguration complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="PTZConfiguration">
 *   <complexContent>
 *     <extension base="{http://www.onvif.org/ver10/schema}ConfigurationEntity">
 *       <sequence>
 *         <element name="NodeToken" type="{http://www.onvif.org/ver10/schema}ReferenceToken"/>
 *         <element name="DefaultAbsolutePantTiltPositionSpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         <element name="DefaultAbsoluteZoomPositionSpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         <element name="DefaultRelativePanTiltTranslationSpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         <element name="DefaultRelativeZoomTranslationSpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         <element name="DefaultContinuousPanTiltVelocitySpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         <element name="DefaultContinuousZoomVelocitySpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         <element name="DefaultPTZSpeed" type="{http://www.onvif.org/ver10/schema}PTZSpeed" minOccurs="0"/>
 *         <element name="DefaultPTZTimeout" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/>
 *         <element name="PanTiltLimits" type="{http://www.onvif.org/ver10/schema}PanTiltLimits" minOccurs="0"/>
 *         <element name="ZoomLimits" type="{http://www.onvif.org/ver10/schema}ZoomLimits" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}PTZConfigurationExtension" minOccurs="0"/>
 *       </sequence>
 *       <anyAttribute processContents='lax'/>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTZConfiguration",
        propOrder = {
                "nodeToken",
                "defaultAbsolutePantTiltPositionSpace",
                "defaultAbsoluteZoomPositionSpace",
                "defaultRelativePanTiltTranslationSpace",
                "defaultRelativeZoomTranslationSpace",
                "defaultContinuousPanTiltVelocitySpace",
                "defaultContinuousZoomVelocitySpace",
                "defaultPTZSpeed",
                "defaultPTZTimeout",
                "panTiltLimits",
                "zoomLimits",
                "extension"
        })
public class PTZConfiguration extends ConfigurationEntity {

    /**
     * -- GETTER --
     *  Ruft den Wert der nodeToken-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "NodeToken", required = true)
    protected String nodeToken;

    /**
     * -- GETTER --
     *  Ruft den Wert der defaultAbsolutePantTiltPositionSpace-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "DefaultAbsolutePantTiltPositionSpace")
    @XmlSchemaType(name = "anyURI")
    protected String defaultAbsolutePantTiltPositionSpace;

    /**
     * -- GETTER --
     *  Ruft den Wert der defaultAbsoluteZoomPositionSpace-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "DefaultAbsoluteZoomPositionSpace")
    @XmlSchemaType(name = "anyURI")
    protected String defaultAbsoluteZoomPositionSpace;

    /**
     * -- GETTER --
     *  Ruft den Wert der defaultRelativePanTiltTranslationSpace-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "DefaultRelativePanTiltTranslationSpace")
    @XmlSchemaType(name = "anyURI")
    protected String defaultRelativePanTiltTranslationSpace;

    /**
     * -- GETTER --
     *  Ruft den Wert der defaultRelativeZoomTranslationSpace-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "DefaultRelativeZoomTranslationSpace")
    @XmlSchemaType(name = "anyURI")
    protected String defaultRelativeZoomTranslationSpace;

    /**
     * -- GETTER --
     *  Ruft den Wert der defaultContinuousPanTiltVelocitySpace-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "DefaultContinuousPanTiltVelocitySpace")
    @XmlSchemaType(name = "anyURI")
    protected String defaultContinuousPanTiltVelocitySpace;

    /**
     * -- GETTER --
     *  Ruft den Wert der defaultContinuousZoomVelocitySpace-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "DefaultContinuousZoomVelocitySpace")
    @XmlSchemaType(name = "anyURI")
    protected String defaultContinuousZoomVelocitySpace;

    /**
     * -- GETTER --
     *  Ruft den Wert der defaultPTZSpeed-Eigenschaft ab.
     *
     * @return possible object is {@link PTZSpeed }
     */
    @XmlElement(name = "DefaultPTZSpeed")
    protected PTZSpeed defaultPTZSpeed;

    /**
     * -- GETTER --
     *  Ruft den Wert der defaultPTZTimeout-Eigenschaft ab.
     *
     * @return possible object is {@link Duration }
     */
    @XmlElement(name = "DefaultPTZTimeout")
    protected Duration defaultPTZTimeout;

    /**
     * -- GETTER --
     *  Ruft den Wert der panTiltLimits-Eigenschaft ab.
     *
     * @return possible object is {@link PanTiltLimits }
     */
    @XmlElement(name = "PanTiltLimits")
    protected PanTiltLimits panTiltLimits;

    /**
     * -- GETTER --
     *  Ruft den Wert der zoomLimits-Eigenschaft ab.
     *
     * @return possible object is {@link ZoomLimits }
     */
    @XmlElement(name = "ZoomLimits")
    protected ZoomLimits zoomLimits;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link PTZConfigurationExtension }
     */
    @XmlElement(name = "Extension")
    protected PTZConfigurationExtension extension;

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
     * Legt den Wert der nodeToken-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setNodeToken(String value) {
        this.nodeToken = value;
    }

    /**
     * Legt den Wert der defaultAbsolutePantTiltPositionSpace-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setDefaultAbsolutePantTiltPositionSpace(String value) {
        this.defaultAbsolutePantTiltPositionSpace = value;
    }

    /**
     * Legt den Wert der defaultAbsoluteZoomPositionSpace-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setDefaultAbsoluteZoomPositionSpace(String value) {
        this.defaultAbsoluteZoomPositionSpace = value;
    }

    /**
     * Legt den Wert der defaultRelativePanTiltTranslationSpace-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setDefaultRelativePanTiltTranslationSpace(String value) {
        this.defaultRelativePanTiltTranslationSpace = value;
    }

    /**
     * Legt den Wert der defaultRelativeZoomTranslationSpace-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setDefaultRelativeZoomTranslationSpace(String value) {
        this.defaultRelativeZoomTranslationSpace = value;
    }

    /**
     * Legt den Wert der defaultContinuousPanTiltVelocitySpace-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setDefaultContinuousPanTiltVelocitySpace(String value) {
        this.defaultContinuousPanTiltVelocitySpace = value;
    }

    /**
     * Legt den Wert der defaultContinuousZoomVelocitySpace-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setDefaultContinuousZoomVelocitySpace(String value) {
        this.defaultContinuousZoomVelocitySpace = value;
    }

    /**
     * Legt den Wert der defaultPTZSpeed-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZSpeed }
     */
    public void setDefaultPTZSpeed(PTZSpeed value) {
        this.defaultPTZSpeed = value;
    }

    /**
     * Legt den Wert der defaultPTZTimeout-Eigenschaft fest.
     *
     * @param value allowed object is {@link Duration }
     */
    public void setDefaultPTZTimeout(Duration value) {
        this.defaultPTZTimeout = value;
    }

    /**
     * Legt den Wert der panTiltLimits-Eigenschaft fest.
     *
     * @param value allowed object is {@link PanTiltLimits }
     */
    public void setPanTiltLimits(PanTiltLimits value) {
        this.panTiltLimits = value;
    }

    /**
     * Legt den Wert der zoomLimits-Eigenschaft fest.
     *
     * @param value allowed object is {@link ZoomLimits }
     */
    public void setZoomLimits(ZoomLimits value) {
        this.zoomLimits = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZConfigurationExtension }
     */
    public void setExtension(PTZConfigurationExtension value) {
        this.extension = value;
    }

}
