//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.04 um 12:22:03 PM CET
//

package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"certificateID", "subject", "validNotBefore", "validNotAfter"})
@XmlRootElement(name = "CreateCertificate")
public class CreateCertificate {

    /**
     * -- GETTER --
     *  Ruft den Wert der certificateID-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "CertificateID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String certificateID;

    /**
     * -- GETTER --
     *  Ruft den Wert der subject-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "Subject")
    protected String subject;

    /**
     * -- GETTER --
     *  Ruft den Wert der validNotBefore-Eigenschaft ab.
     *
     * @return possible object is {@link XMLGregorianCalendar }
     */
    @XmlElement(name = "ValidNotBefore")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar validNotBefore;

    /**
     * -- GETTER --
     *  Ruft den Wert der validNotAfter-Eigenschaft ab.
     *
     * @return possible object is {@link XMLGregorianCalendar }
     */
    @XmlElement(name = "ValidNotAfter")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar validNotAfter;

    /**
     * Legt den Wert der certificateID-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setCertificateID(String value) {
        this.certificateID = value;
    }

    /**
     * Legt den Wert der subject-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setSubject(String value) {
        this.subject = value;
    }

    /**
     * Legt den Wert der validNotBefore-Eigenschaft fest.
     *
     * @param value allowed object is {@link XMLGregorianCalendar }
     */
    public void setValidNotBefore(XMLGregorianCalendar value) {
        this.validNotBefore = value;
    }

    /**
     * Legt den Wert der validNotAfter-Eigenschaft fest.
     *
     * @param value allowed object is {@link XMLGregorianCalendar }
     */
    public void setValidNotAfter(XMLGregorianCalendar value) {
        this.validNotAfter = value;
    }
}
