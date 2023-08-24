package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "VideoSourceConfiguration",
        propOrder = {"sourceToken", "bounds", "any", "extension"})
public class VideoSourceConfiguration extends ConfigurationEntity {


    @Getter @XmlElement(name = "SourceToken", required = true)
    protected String sourceToken;


    @Getter @XmlElement(name = "Bounds", required = true)
    protected IntRectangle bounds;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlElement(name = "Extension")
    protected VideoSourceConfigurationExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setSourceToken(String value) {
        this.sourceToken = value;
    }


    public void setBounds(IntRectangle value) {
        this.bounds = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public void setExtension(VideoSourceConfigurationExtension value) {
        this.extension = value;
    }

}
