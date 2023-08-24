package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3._2004._08.xop.include.Include;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AttachmentData",
        propOrder = {"include"})
public class AttachmentData {

    
    @XmlElement(
            name = "Include",
            namespace = "http://www.w3.org/2004/08/xop/include",
            required = true)
    protected Include include;

    
    @XmlAttribute(name = "contentType", namespace = "http://www.w3.org/2005/05/xmlmime")
    protected String contentType;

    
    public void setInclude(Include value) {
        this.include = value;
    }

    
    public void setContentType(String value) {
        this.contentType = value;
    }
}
