package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"uri"})
@XmlRootElement(name = "GetStreamUriResponse")
public class GetStreamUriResponse {


    @XmlElement(name = "Uri", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String uri;


    public void setUri(String value) {
        this.uri = value;
    }
}
