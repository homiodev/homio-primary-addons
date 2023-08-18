package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.PTZNode;

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
 *         <element name="PTZNode" type="{http://www.onvif.org/ver10/schema}PTZNode"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"ptzNode"})
@XmlRootElement(name = "GetNodeResponse")
public class GetNodeResponse {

    @XmlElement(name = "PTZNode", required = true)
    protected PTZNode ptzNode;

    /**
     * Ruft den Wert der ptzNode-Eigenschaft ab.
     *
     * @return possible object is {@link PTZNode }
     */
    public PTZNode getPTZNode() {
        return ptzNode;
    }

    /**
     * Legt den Wert der ptzNode-Eigenschaft fest.
     *
     * @param value allowed object is {@link PTZNode }
     */
    public void setPTZNode(PTZNode value) {
        this.ptzNode = value;
    }
}
