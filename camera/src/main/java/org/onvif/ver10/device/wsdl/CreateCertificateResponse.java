//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.04 um 12:22:03 PM CET
//

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

    /**
     * -- GETTER --
     *  Ruft den Wert der nvtCertificate-Eigenschaft ab.
     *
     * @return possible object is {@link Certificate }
     */
    @XmlElement(name = "NvtCertificate", required = true)
    protected Certificate nvtCertificate;

    /**
     * Legt den Wert der nvtCertificate-Eigenschaft fest.
     *
     * @param value allowed object is {@link Certificate }
     */
    public void setNvtCertificate(Certificate value) {
        this.nvtCertificate = value;
    }
}
