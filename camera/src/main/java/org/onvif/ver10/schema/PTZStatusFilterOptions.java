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
        name = "PTZStatusFilterOptions",
        propOrder = {
                "panTiltStatusSupported",
                "zoomStatusSupported",
                "any",
                "panTiltPositionSupported",
                "zoomPositionSupported",
                "extension"
        })
public class PTZStatusFilterOptions {


    @Getter @XmlElement(name = "PanTiltStatusSupported")
    protected boolean panTiltStatusSupported;


    @Getter @XmlElement(name = "ZoomStatusSupported")
    protected boolean zoomStatusSupported;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlElement(name = "PanTiltPositionSupported")
    protected Boolean panTiltPositionSupported;

    @XmlElement(name = "ZoomPositionSupported")
    protected Boolean zoomPositionSupported;


    @Getter @XmlElement(name = "Extension")
    protected PTZStatusFilterOptionsExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setPanTiltStatusSupported(boolean value) {
        this.panTiltStatusSupported = value;
    }


    public void setZoomStatusSupported(boolean value) {
        this.zoomStatusSupported = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public Boolean isPanTiltPositionSupported() {
        return panTiltPositionSupported;
    }


    public void setPanTiltPositionSupported(Boolean value) {
        this.panTiltPositionSupported = value;
    }


    public Boolean isZoomPositionSupported() {
        return zoomPositionSupported;
    }


    public void setZoomPositionSupported(Boolean value) {
        this.zoomPositionSupported = value;
    }


    public void setExtension(PTZStatusFilterOptionsExtension value) {
        this.extension = value;
    }

}
