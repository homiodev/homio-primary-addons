







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"enabled"})
@XmlRootElement(name = "GetClientCertificateModeResponse")
public class GetClientCertificateModeResponse {


    @XmlElement(name = "Enabled")
    protected boolean enabled;


    public void setEnabled(boolean value) {
        this.enabled = value;
    }
}
