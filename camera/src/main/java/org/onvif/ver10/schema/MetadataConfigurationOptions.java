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
        name = "MetadataConfigurationOptions",
        propOrder = {"ptzStatusFilterOptions", "any"})
public class MetadataConfigurationOptions {

    @XmlElement(name = "PTZStatusFilterOptions", required = true)
    protected PTZStatusFilterOptions ptzStatusFilterOptions;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public PTZStatusFilterOptions getPTZStatusFilterOptions() {
        return ptzStatusFilterOptions;
    }


    public void setPTZStatusFilterOptions(PTZStatusFilterOptions value) {
        this.ptzStatusFilterOptions = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
