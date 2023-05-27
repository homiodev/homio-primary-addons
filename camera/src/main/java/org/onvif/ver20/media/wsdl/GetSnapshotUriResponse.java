package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"uri"})
@XmlRootElement(name = "GetSnapshotUriResponse")
public class GetSnapshotUriResponse {

    @XmlElement(name = "Uri", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String uri;

    /**
     * Ruft den Wert der uri-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getUri() {
        return uri;
    }

    /**
     * Legt den Wert der uri-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setUri(String value) {
        this.uri = value;
    }
}
