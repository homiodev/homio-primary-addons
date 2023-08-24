package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ColorDescriptor",
        propOrder = {"colorCluster", "extension"})
public class ColorDescriptor {

    @XmlElement(name = "ColorCluster")
    protected List<ColorDescriptor.ColorCluster> colorCluster;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link ColorDescriptorExtension }
     */
    @Getter @XmlElement(name = "Extension")
    protected ColorDescriptorExtension extension;

    /**
     * -- GETTER --
     *  Gets a map that contains attributes that aren't bound to any typed property on this class.
     *  <p>the map is keyed by the name of the attribute and the value is the string value of the
     *  attribute.
     *  <p>the map returned by this method is live, and you can add new attribute by updating the map
     *  directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the colorCluster property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the
     * colorCluster property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getColorCluster().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link ColorDescriptor.ColorCluster
     * }
     */
    public List<ColorDescriptor.ColorCluster> getColorCluster() {
        if (colorCluster == null) {
            colorCluster = new ArrayList<ColorDescriptor.ColorCluster>();
        }
        return this.colorCluster;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link ColorDescriptorExtension }
     */
    public void setExtension(ColorDescriptorExtension value) {
        this.extension = value;
    }

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
     *         <element name="Color" type="{http://www.onvif.org/ver10/schema}Color"/>
     *         <element name="Weight" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
     *         <element name="Covariance" type="{http://www.onvif.org/ver10/schema}ColorCovariance" minOccurs="0"/>
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
            propOrder = {"color", "weight", "covariance"})
    public static class ColorCluster {

        /**
         * -- GETTER --
         *  Ruft den Wert der color-Eigenschaft ab.
         *
         * @return possible object is {@link Color }
         */
        @XmlElement(name = "Color", required = true)
        protected Color color;

        /**
         * -- GETTER --
         *  Ruft den Wert der weight-Eigenschaft ab.
         *
         * @return possible object is {@link Float }
         */
        @XmlElement(name = "Weight")
        protected Float weight;

        /**
         * -- GETTER --
         *  Ruft den Wert der covariance-Eigenschaft ab.
         *
         * @return possible object is {@link ColorCovariance }
         */
        @XmlElement(name = "Covariance")
        protected ColorCovariance covariance;

        /**
         * Legt den Wert der color-Eigenschaft fest.
         *
         * @param value allowed object is {@link Color }
         */
        public void setColor(Color value) {
            this.color = value;
        }

        /**
         * Legt den Wert der weight-Eigenschaft fest.
         *
         * @param value allowed object is {@link Float }
         */
        public void setWeight(Float value) {
            this.weight = value;
        }

        /**
         * Legt den Wert der covariance-Eigenschaft fest.
         *
         * @param value allowed object is {@link ColorCovariance }
         */
        public void setCovariance(ColorCovariance value) {
            this.covariance = value;
        }
    }
}
