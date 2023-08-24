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


    @XmlElement(name = "Name", required = true)
    protected String name;


    @XmlElement(name = "UseCount")
    protected int useCount;


    @XmlAttribute(name = "token", required = true)
    protected String token;


    public void setName(String value) {
        this.name = value;
    }


    public void setUseCount(int value) {
        this.useCount = value;
    }


    public void setToken(String value) {
        this.token = value;
    }
}
