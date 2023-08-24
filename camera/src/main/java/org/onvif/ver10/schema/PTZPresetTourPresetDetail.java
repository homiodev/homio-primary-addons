package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PTZPresetTourPresetDetail",
        propOrder = {"presetToken", "home", "ptzPosition", "typeExtension", "any"})
public class PTZPresetTourPresetDetail {


    @XmlElement(name = "PresetToken")
    protected String presetToken;

    @XmlElement(name = "Home")
    protected Boolean home;

    @XmlElement(name = "PTZPosition")
    protected PTZVector ptzPosition;


    @Getter @XmlElement(name = "TypeExtension")
    protected PTZPresetTourTypeExtension typeExtension;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setPresetToken(String value) {
        this.presetToken = value;
    }


    public Boolean isHome() {
        return home;
    }


    public void setHome(Boolean value) {
        this.home = value;
    }


    public PTZVector getPTZPosition() {
        return ptzPosition;
    }


    public void setPTZPosition(PTZVector value) {
        this.ptzPosition = value;
    }


    public void setTypeExtension(PTZPresetTourTypeExtension value) {
        this.typeExtension = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
