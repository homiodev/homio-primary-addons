







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurationToken", "any"})
@XmlRootElement(name = "GetOSDOptions")
public class GetOSDOptions {


    @Getter @XmlElement(name = "ConfigurationToken", required = true)
    protected String configurationToken;

    @XmlAnyElement(lax = true)
    protected List<Object> any;


    public void setConfigurationToken(String value) {
        this.configurationToken = value;
    }


    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }
}
