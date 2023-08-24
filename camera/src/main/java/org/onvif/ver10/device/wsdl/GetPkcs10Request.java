







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import org.onvif.ver10.schema.BinaryData;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"certificateID", "subject", "attributes"})
@XmlRootElement(name = "GetPkcs10Request")
public class GetPkcs10Request {


    @XmlElement(name = "CertificateID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String certificateID;


    @XmlElement(name = "Subject")
    protected String subject;


    @XmlElement(name = "Attributes")
    protected BinaryData attributes;


    public void setCertificateID(String value) {
        this.certificateID = value;
    }


    public void setSubject(String value) {
        this.subject = value;
    }


    public void setAttributes(BinaryData value) {
        this.attributes = value;
    }
}
