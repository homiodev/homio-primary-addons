







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.Certificate;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"caCertificate"})
@XmlRootElement(name = "LoadCACertificates")
public class LoadCACertificates {

    @XmlElement(name = "CACertificate", required = true)
    protected List<Certificate> caCertificate;


    public List<Certificate> getCACertificate() {
        if (caCertificate == null) {
            caCertificate = new ArrayList<Certificate>();
        }
        return this.caCertificate;
    }
}
