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
        name = "EFlipOptions",
        propOrder = {"mode", "extension"})
public class EFlipOptions {

    @XmlElement(name = "Mode")
    protected List<EFlipMode> mode;

    
    @Getter @XmlElement(name = "Extension")
    protected EFlipOptionsExtension extension;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public List<EFlipMode> getMode() {
        if (mode == null) {
            mode = new ArrayList<EFlipMode>();
        }
        return this.mode;
    }

    
    public void setExtension(EFlipOptionsExtension value) {
        this.extension = value;
    }

}
