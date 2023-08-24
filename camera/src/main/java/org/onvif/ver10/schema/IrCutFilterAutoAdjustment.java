package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "IrCutFilterAutoAdjustment",
        propOrder = {"boundaryType", "boundaryOffset", "responseTime", "extension"})
public class IrCutFilterAutoAdjustment {


    @XmlElement(name = "BoundaryType", required = true)
    protected String boundaryType;


    @XmlElement(name = "BoundaryOffset")
    protected Float boundaryOffset;


    @XmlElement(name = "ResponseTime")
    protected Duration responseTime;


    @XmlElement(name = "Extension")
    protected IrCutFilterAutoAdjustmentExtension extension;


    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setBoundaryType(String value) {
        this.boundaryType = value;
    }


    public void setBoundaryOffset(Float value) {
        this.boundaryOffset = value;
    }


    public void setResponseTime(Duration value) {
        this.responseTime = value;
    }


    public void setExtension(IrCutFilterAutoAdjustmentExtension value) {
        this.extension = value;
    }

}
