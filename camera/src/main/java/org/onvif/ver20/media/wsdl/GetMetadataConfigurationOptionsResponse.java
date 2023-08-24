package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.MetadataConfigurationOptions;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"options"})
@XmlRootElement(name = "GetMetadataConfigurationOptionsResponse")
public class GetMetadataConfigurationOptionsResponse {

    /**
     * -- GETTER --
     *  Ruft den Wert der options-Eigenschaft ab.
     *
     * @return possible object is {@link MetadataConfigurationOptions }
     */
    @XmlElement(name = "Options", required = true)
    protected MetadataConfigurationOptions options;

    /**
     * Legt den Wert der options-Eigenschaft fest.
     *
     * @param value allowed object is {@link MetadataConfigurationOptions }
     */
    public void setOptions(MetadataConfigurationOptions value) {
        this.options = value;
    }
}
