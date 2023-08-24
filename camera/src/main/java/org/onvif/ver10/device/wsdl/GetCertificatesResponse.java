







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.Certificate;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"nvtCertificate"})
@XmlRootElement(name = "GetCertificatesResponse")
public class GetCertificatesResponse {

    @XmlElement(name = "NvtCertificate")
    protected List<Certificate> nvtCertificate;


    public List<Certificate> getNvtCertificate() {
        if (nvtCertificate == null) {
            nvtCertificate = new ArrayList<Certificate>();
        }
        return this.nvtCertificate;
    }
}
