package org.onvif.ver10.device.wsdl;

import org.onvif.ver10.schema.CapabilityCategory;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"category"})
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
