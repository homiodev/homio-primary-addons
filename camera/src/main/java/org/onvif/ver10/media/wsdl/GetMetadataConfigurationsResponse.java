







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.MetadataConfiguration;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurations"})
@XmlRootElement(name = "GetMetadataConfigurationsResponse")
public class GetMetadataConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<MetadataConfiguration> configurations;

    
    public List<MetadataConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<MetadataConfiguration>();
        }
        return this.configurations;
    }
}
