package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Capabilities",
        propOrder = {"profileCapabilities", "streamingCapabilities", "any"})
public class Capabilities {

    @XmlElement(name = "ProfileCapabilities", required = true)
    protected ProfileCapabilities profileCapabilities;

    @XmlElement(name = "StreamingCapabilities", required = true)
    protected StreamingCapabilities streamingCapabilities;

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    @XmlAttribute(name = "SnapshotUri")
    protected Boolean snapshotUri;

    @XmlAttribute(name = "Rotation")
    protected Boolean rotation;

    @XmlAttribute(name = "VideoSourceMode")
    protected Boolean videoSourceMode;

    @XmlAttribute(name = "OSD")
    protected Boolean osd;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }
}
