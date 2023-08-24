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
        name = "AnalyticsEngineInput",
        propOrder = {"sourceIdentification", "videoInput", "metadataInput", "any"})
public class AnalyticsEngineInput extends ConfigurationEntity {

    
    @Getter @XmlElement(name = "SourceIdentification", required = true)
    protected SourceIdentification sourceIdentification;

    
    @Getter @XmlElement(name = "VideoInput", required = true)
    protected VideoEncoderConfiguration videoInput;

    
    @Getter @XmlElement(name = "MetadataInput", required = true)
    protected MetadataInput metadataInput;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setSourceIdentification(SourceIdentification value) {
        this.sourceIdentification = value;
    }

    
    public void setVideoInput(VideoEncoderConfiguration value) {
        this.videoInput = value;
    }

    
    public void setMetadataInput(MetadataInput value) {
        this.metadataInput = value;
    }

    
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
