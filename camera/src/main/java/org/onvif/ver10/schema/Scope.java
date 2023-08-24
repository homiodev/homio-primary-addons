package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

/**
 * Java-Klasse f�r Scope complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="Scope">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="ScopeDef" type="{http://www.onvif.org/ver10/schema}ScopeDefinition"/>
 *         <element name="ScopeItem" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Scope",
        propOrder = {"scopeDef", "scopeItem"})
public class Scope {

    /**
     * -- GETTER --
     *  Ruft den Wert der scopeDef-Eigenschaft ab.
     *
     * @return possible object is {@link ScopeDefinition }
     */
    @XmlElement(name = "ScopeDef", required = true)
    protected ScopeDefinition scopeDef;

    /**
     * -- GETTER --
     *  Ruft den Wert der scopeItem-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlElement(name = "ScopeItem", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String scopeItem;

    /**
     * Legt den Wert der scopeDef-Eigenschaft fest.
     *
     * @param value allowed object is {@link ScopeDefinition }
     */
    public void setScopeDef(ScopeDefinition value) {
        this.scopeDef = value;
    }

    /**
     * Legt den Wert der scopeItem-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setScopeItem(String value) {
        this.scopeItem = value;
    }
}
