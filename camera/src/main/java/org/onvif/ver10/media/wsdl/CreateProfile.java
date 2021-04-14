package org.onvif.ver10.media.wsdl;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"name", "token"})
@XmlRootElement(name = "CreateProfile")
public class CreateProfile {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Token")
    protected String token;
}
