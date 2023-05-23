//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.04 um 12:22:03 PM CET
//

package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.IPAddressFilter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"ipAddressFilter"})
@XmlRootElement(name = "AddIPAddressFilter")
public class AddIPAddressFilter {

    @XmlElement(name = "IPAddressFilter", required = true)
    protected IPAddressFilter ipAddressFilter;

    /**
     * Ruft den Wert der ipAddressFilter-Eigenschaft ab.
     *
     * @return possible object is {@link IPAddressFilter }
     */
    public IPAddressFilter getIPAddressFilter() {
        return ipAddressFilter;
    }

    /**
     * Legt den Wert der ipAddressFilter-Eigenschaft fest.
     *
     * @param value allowed object is {@link IPAddressFilter }
     */
    public void setIPAddressFilter(IPAddressFilter value) {
        this.ipAddressFilter = value;
    }
}
