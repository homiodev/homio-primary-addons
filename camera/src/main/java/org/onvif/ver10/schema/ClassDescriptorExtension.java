package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ClassDescriptorExtension",
        propOrder = {"any", "otherTypes", "extension"})
public class ClassDescriptorExtension {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlElement(name = "OtherTypes", required = true)
    protected List<OtherType> otherTypes;

    @XmlElement(name = "Extension")
    protected ClassDescriptorExtension2 extension;

    /**
     * Gets the value of the any property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the any
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getAny().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link Element } {@link
     * java.lang.Object }
     */
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the otherTypes property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the otherTypes
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getOtherTypes().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link OtherType }
     */
    public List<OtherType> getOtherTypes() {
        if (otherTypes == null) {
            otherTypes = new ArrayList<OtherType>();
        }
        return this.otherTypes;
    }

    /**
     * Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link ClassDescriptorExtension2 }
     */
    public ClassDescriptorExtension2 getExtension() {
        return extension;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link ClassDescriptorExtension2 }
     */
    public void setExtension(ClassDescriptorExtension2 value) {
        this.extension = value;
    }
}
