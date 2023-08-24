package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.MetadataConfiguration;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configuration"})
@XmlRootElement(name = "SetMetadataConfiguration")
public class SetMetadataConfiguration {


    @XmlElement(name = "Configuration", required = true)
    protected MetadataConfiguration configuration;


    public void setConfiguration(MetadataConfiguration value) {
        this.configuration = value;
    }
}
