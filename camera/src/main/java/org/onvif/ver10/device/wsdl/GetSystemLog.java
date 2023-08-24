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
import org.onvif.ver10.schema.SystemLogType;

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
 *         <element name="LogType" type="{http://www.onvif.org/ver10/schema}SystemLogType"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"logType"})
@XmlRootElement(name = "GetSystemLog")
public class GetSystemLog {

    /**
     * -- GETTER --
     *  Ruft den Wert der logType-Eigenschaft ab.
     *
     * @return possible object is {@link SystemLogType }
     */
    @XmlElement(name = "LogType", required = true)
    protected SystemLogType logType;

    /**
     * Legt den Wert der logType-Eigenschaft fest.
     *
     * @param value allowed object is {@link SystemLogType }
     */
    public void setLogType(SystemLogType value) {
        this.logType = value;
    }
}
