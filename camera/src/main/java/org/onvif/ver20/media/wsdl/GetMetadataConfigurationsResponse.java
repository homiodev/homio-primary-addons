package org.onvif.ver20.media.wsdl;

import org.onvif.ver10.schema.MetadataConfiguration;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "configurations"
})
@XmlRootElement(name = "GetMetadataConfigurationsResponse")
public class GetMetadataConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<MetadataConfiguration> configurations;

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
     * {@link MetadataConfiguration }
     *
     *
     */
    public List<MetadataConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<MetadataConfiguration>();
        }
        return this.configurations;
    }

}
