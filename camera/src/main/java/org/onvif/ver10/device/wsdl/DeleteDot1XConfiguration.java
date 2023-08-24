







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"dot1XConfigurationToken"})
@XmlRootElement(name = "DeleteDot1XConfiguration")
public class DeleteDot1XConfiguration {

    @XmlElement(name = "Dot1XConfigurationToken")
    protected List<String> dot1XConfigurationToken;


    public List<String> getDot1XConfigurationToken() {
        if (dot1XConfigurationToken == null) {
            dot1XConfigurationToken = new ArrayList<String>();
        }
        return this.dot1XConfigurationToken;
    }
}
