package org.onvif.ver10.schema;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectId")
@XmlSeeAlso({Object.class})
public class ObjectId {

    @XmlAttribute(name = "ObjectId")
    protected BigInteger objectId;

    /**
     * Ruft den Wert der objectId-Eigenschaft ab.
     *
     * @return possible object is {@link BigInteger }
     */
    public BigInteger getObjectId() {
        return objectId;
    }

    /**
     * Legt den Wert der objectId-Eigenschaft fest.
     *
     * @param value allowed object is {@link BigInteger }
     */
    public void setObjectId(BigInteger value) {
        this.objectId = value;
    }
}
