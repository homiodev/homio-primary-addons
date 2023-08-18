//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.04 um 12:22:03 PM CET
//

package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.Certificate;

import java.util.ArrayList;
import java.util.List;

/**
 * Java-Klasse f�r anonymous complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="NVTCertificate" type="{http://www.onvif.org/ver10/schema}Certificate" maxOccurs="unbounded"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"nvtCertificate"})
@XmlRootElement(name = "LoadCertificates")
public class LoadCertificates {

    @XmlElement(name = "NVTCertificate", required = true)
    protected List<Certificate> nvtCertificate;

    /**
     * Gets the value of the nvtCertificate property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the
     * nvtCertificate property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getNVTCertificate().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link Certificate }
     */
    public List<Certificate> getNVTCertificate() {
        if (nvtCertificate == null) {
            nvtCertificate = new ArrayList<Certificate>();
        }
        return this.nvtCertificate;
    }
}
