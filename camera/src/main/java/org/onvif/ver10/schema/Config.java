package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Config",
        propOrder = {"parameters"})
public class Config {

    /**
     * -- GETTER --
     *  Ruft den Wert der parameters-Eigenschaft ab.
     *
     * @return possible object is {@link ItemList }
     */
    @XmlElement(name = "Parameters", required = true)
    protected ItemList parameters;

    /**
     * -- GETTER --
     *  Ruft den Wert der name-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    @XmlAttribute(name = "Name", required = true)
    protected String name;

    /**
     * -- GETTER --
     *  Ruft den Wert der type-Eigenschaft ab.
     *
     * @return possible object is {@link QName }
     */
    @XmlAttribute(name = "Type", required = true)
    protected QName type;

    /**
     * Legt den Wert der parameters-Eigenschaft fest.
     *
     * @param value allowed object is {@link ItemList }
     */
    public void setParameters(ItemList value) {
        this.parameters = value;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     *
     * @param value allowed object is {@link QName }
     */
    public void setType(QName value) {
        this.type = value;
    }
}
