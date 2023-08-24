package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTZPresetTourPresetDetailOptions",
        propOrder = {"presetToken", "home", "panTiltPositionSpace", "zoomPositionSpace", "extension"})
public class PTZPresetTourPresetDetailOptions {

    @XmlElement(name = "PresetToken")
    protected List<String> presetToken;

    @XmlElement(name = "Home")
    protected Boolean home;


    @Getter @XmlElement(name = "PanTiltPositionSpace")
    protected Space2DDescription panTiltPositionSpace;


    @Getter @XmlElement(name = "ZoomPositionSpace")
    protected Space1DDescription zoomPositionSpace;


    @Getter @XmlElement(name = "Extension")
    protected PTZPresetTourPresetDetailOptionsExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<String> getPresetToken() {
        if (presetToken == null) {
            presetToken = new ArrayList<String>();
        }
        return this.presetToken;
    }


    public Boolean isHome() {
        return home;
    }


    public void setHome(Boolean value) {
        this.home = value;
    }


    public void setPanTiltPositionSpace(Space2DDescription value) {
        this.panTiltPositionSpace = value;
    }


    public void setZoomPositionSpace(Space1DDescription value) {
        this.zoomPositionSpace = value;
    }


    public void setExtension(PTZPresetTourPresetDetailOptionsExtension value) {
        this.extension = value;
    }

}
