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
        name = "LayoutOptions",
        propOrder = {"paneLayoutOptions", "extension"})
public class LayoutOptions {

    @XmlElement(name = "PaneLayoutOptions", required = true)
    protected List<PaneLayoutOptions> paneLayoutOptions;


    @Getter @XmlElement(name = "Extension")
    protected LayoutOptionsExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<PaneLayoutOptions> getPaneLayoutOptions() {
        if (paneLayoutOptions == null) {
            paneLayoutOptions = new ArrayList<PaneLayoutOptions>();
        }
        return this.paneLayoutOptions;
    }


    public void setExtension(LayoutOptionsExtension value) {
        this.extension = value;
    }

}
