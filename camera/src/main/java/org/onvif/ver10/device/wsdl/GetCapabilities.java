package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.CapabilityCategory;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"category"})
@XmlRootElement(name = "GetCapabilities")
public class GetCapabilities {

    @XmlElement(name = "Category")
    protected List<CapabilityCategory> category;

    public List<CapabilityCategory> getCategory() {
        if (category == null) {
            category = new ArrayList<>();
        }
        return this.category;
    }
}
