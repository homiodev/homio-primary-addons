package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"name", "token"})
@XmlRootElement(name = "CreateProfile")
public class CreateProfile {

    @XmlElement(name = "Name", required = true)
    protected String name;

    @XmlElement(name = "Token")
    protected String token;
}
