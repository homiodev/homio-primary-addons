







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"certificateID"})
@XmlRootElement(name = "DeleteCertificates")
public class DeleteCertificates {

    @XmlElement(name = "CertificateID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> certificateID;


    public List<String> getCertificateID() {
        if (certificateID == null) {
            certificateID = new ArrayList<String>();
        }
        return this.certificateID;
    }
}
