//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.19 um 02:35:56 PM CET
//

package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.AudioEncoderConfiguration;

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
 *         <element name="Configuration" type="{http://www.onvif.org/ver10/schema}AudioEncoderConfiguration"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configuration"})
@XmlRootElement(name = "GetAudioEncoderConfigurationResponse")
public class GetAudioEncoderConfigurationResponse {

    @XmlElement(name = "Configuration", required = true)
    protected AudioEncoderConfiguration configuration;

    /**
     * Ruft den Wert der configuration-Eigenschaft ab.
     *
     * @return possible object is {@link AudioEncoderConfiguration }
     */
    public AudioEncoderConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Legt den Wert der configuration-Eigenschaft fest.
     *
     * @param value allowed object is {@link AudioEncoderConfiguration }
     */
    public void setConfiguration(AudioEncoderConfiguration value) {
        this.configuration = value;
    }
}
