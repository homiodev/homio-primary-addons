package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "BinaryData",
        propOrder = {"data"})
public class BinaryData {

    @XmlElement(name = "Data", required = true)
    protected byte[] data;

    @XmlAttribute(name = "contentType", namespace = "http://www.w3.org/2005/05/xmlmime")
    protected String contentType;

    /**
     * Ruft den Wert der data-Eigenschaft ab.
     *
     * @return possible object is byte[]
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Legt den Wert der data-Eigenschaft fest.
     *
     * @param value allowed object is byte[]
     */
    public void setData(byte[] value) {
        this.data = value;
    }

    /**
     * Ruft den Wert der contentType-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Legt den Wert der contentType-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setContentType(String value) {
        this.contentType = value;
    }
}
