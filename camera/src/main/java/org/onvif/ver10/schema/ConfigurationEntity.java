package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ConfigurationEntity",
        propOrder = {"name", "useCount"})
@XmlSeeAlso({
        AudioSourceConfiguration.class,
        PTZConfiguration.class,
        VideoSourceConfiguration.class,
        AudioEncoderConfiguration.class,
        VideoEncoderConfiguration.class,
        AudioDecoderConfiguration.class,
        VideoAnalyticsConfiguration.class,
        AudioOutputConfiguration.class,
        MetadataConfiguration.class,
        AnalyticsEngineInput.class,
        AnalyticsEngineControl.class,
        AnalyticsEngine.class,
        VideoOutputConfiguration.class
})
public class ConfigurationEntity {

    /**
     * -- GETTER --
     *  Ruft den Wert der name-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Name", required = true)
    protected String name;

    /**
     * -- GETTER --
     *  Ruft den Wert der useCount-Eigenschaft ab.
     */
    @XmlElement(name = "UseCount")
    protected int useCount;

    /**
     * -- GETTER --
     *  Ruft den Wert der token-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlAttribute(name = "token", required = true)
    protected String token;

    /**
     * Legt den Wert der name-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Legt den Wert der useCount-Eigenschaft fest.
     */
    public void setUseCount(int value) {
        this.useCount = value;
    }

    /**
     * Legt den Wert der token-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setToken(String value) {
        this.token = value;
    }
}
