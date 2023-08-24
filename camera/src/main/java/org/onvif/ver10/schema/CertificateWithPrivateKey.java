package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "CertificateWithPrivateKey",
        propOrder = {"certificateID", "certificate", "privateKey", "any"})
public class CertificateWithPrivateKey {

    
    @Getter @XmlElement(name = "CertificateID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String certificateID;

    
    @Getter @XmlElement(name = "Certificate", required = true)
    protected BinaryData certificate;

    
    @Getter @XmlElement(name = "PrivateKey", required = true)
    protected BinaryData privateKey;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    
    public void setCertificateID(String value) {
        this.certificateID = value;
    }

    
    public void setCertificate(BinaryData value) {
        this.certificate = value;
    }

    
    public void setPrivateKey(BinaryData value) {
        this.privateKey = value;
    }

    
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
