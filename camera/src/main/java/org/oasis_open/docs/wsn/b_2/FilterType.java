







package org.oasis_open.docs.wsn.b_2;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.EventFilter;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "FilterType",
        propOrder = {"any"})
@XmlSeeAlso({EventFilter.class})
public class FilterType {

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }
}
