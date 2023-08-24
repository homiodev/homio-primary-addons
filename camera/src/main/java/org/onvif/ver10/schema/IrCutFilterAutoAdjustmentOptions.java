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
        name = "IrCutFilterAutoAdjustmentOptions",
        propOrder = {"boundaryType", "boundaryOffset", "responseTimeRange", "extension"})
public class IrCutFilterAutoAdjustmentOptions {

    @XmlElement(name = "BoundaryType", required = true)
    protected List<String> boundaryType;

    @XmlElement(name = "BoundaryOffset")
    protected Boolean boundaryOffset;


    @Getter @XmlElement(name = "ResponseTimeRange")
    protected DurationRange responseTimeRange;


    @Getter @XmlElement(name = "Extension")
    protected IrCutFilterAutoAdjustmentOptionsExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<String> getBoundaryType() {
        if (boundaryType == null) {
            boundaryType = new ArrayList<String>();
        }
        return this.boundaryType;
    }


    public Boolean isBoundaryOffset() {
        return boundaryOffset;
    }


    public void setBoundaryOffset(Boolean value) {
        this.boundaryOffset = value;
    }


    public void setResponseTimeRange(DurationRange value) {
        this.responseTimeRange = value;
    }


    public void setExtension(IrCutFilterAutoAdjustmentOptionsExtension value) {
        this.extension = value;
    }

}
