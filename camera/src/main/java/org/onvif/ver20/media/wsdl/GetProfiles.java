package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"token", "type"})
@XmlRootElement(name = "GetProfiles")
public class GetProfiles {


    @XmlElement(name = "Token")
    protected String token;

    @XmlElement(name = "Type")
    protected List<String> type;


    public void setToken(String value) {
        this.token = value;
    }


    public List<String> getType() {
        if (type == null) {
            type = new ArrayList<String>();
        }
        return this.type;
    }
}
