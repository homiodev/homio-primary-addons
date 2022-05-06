package org.onvif.ver20.media.wsdl;

import org.onvif.ver10.schema.VideoSourceConfiguration;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse f�r anonymous complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Configurations" type="{http://www.onvif.org/ver10/schema}VideoSourceConfiguration" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "configurations"
})
@XmlRootElement(name = "GetVideoSourceConfigurationsResponse")
public class GetVideoSourceConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<VideoSourceConfiguration> configurations;

    /**
     * Gets the value of the configurations property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the configurations property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConfigurations().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VideoSourceConfiguration }
     *
     *
     */
    public List<VideoSourceConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<VideoSourceConfiguration>();
        }
        return this.configurations;
    }

}
