







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.Certificate;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"nvtCertificate"})
@XmlRootElement(name = "CreateCertificateResponse")
public class CreateCertificateResponse {


    @XmlElement(name = "NvtCertificate", required = true)
    protected Certificate nvtCertificate;


    public void setNvtCertificate(Certificate value) {
        this.nvtCertificate = value;
    }
}
