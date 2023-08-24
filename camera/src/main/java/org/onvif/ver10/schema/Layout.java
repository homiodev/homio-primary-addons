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
        name = "Layout",
        propOrder = {"paneLayout", "extension"})
public class Layout {

    @XmlElement(name = "PaneLayout", required = true)
    protected List<PaneLayout> paneLayout;


    @Getter @XmlElement(name = "Extension")
    protected LayoutExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<PaneLayout> getPaneLayout() {
        if (paneLayout == null) {
            paneLayout = new ArrayList<PaneLayout>();
        }
        return this.paneLayout;
    }


    public void setExtension(LayoutExtension value) {
        this.extension = value;
    }

}
