package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTZStream",
        propOrder = {"ptzStatusOrExtension"})
public class PTZStream {

    @XmlElements({
            @XmlElement(name = "PTZStatus", type = PTZStatus.class),
            @XmlElement(name = "Extension", type = PTZStreamExtension.class)
    })
    protected List<java.lang.Object> ptzStatusOrExtension;


    public List<java.lang.Object> getPTZStatusOrExtension() {
        if (ptzStatusOrExtension == null) {
            ptzStatusOrExtension = new ArrayList<java.lang.Object>();
        }
        return this.ptzStatusOrExtension;
    }
}
