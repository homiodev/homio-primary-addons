package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import java.math.BigInteger;
import lombok.Getter;

/**
 * Java-Klasse f�r ObjectId complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="ObjectId">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <attribute name="ObjectId" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectId")
@XmlSeeAlso({Object.class})
public class ObjectId {

    /**
     * -- GETTER --
     *  Ruft den Wert der objectId-Eigenschaft ab.
     *
     * @return possible object is {@link BigInteger }
     */
    @XmlAttribute(name = "ObjectId")
    protected BigInteger objectId;

    /**
     * Legt den Wert der objectId-Eigenschaft fest.
     *
     * @param value allowed object is {@link BigInteger }
     */
    public void setObjectId(BigInteger value) {
        this.objectId = value;
    }
}
