package org.onvif.ver20.media.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.AudioDecoderConfiguration;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"configurations"})
@XmlRootElement(name = "GetAudioDecoderConfigurationsResponse")
public class GetAudioDecoderConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<AudioDecoderConfiguration> configurations;

    /**
     * Gets the value of the configurations property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the
     * configurations property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     *    getConfigurations().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link AudioDecoderConfiguration }
     */
    public List<AudioDecoderConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<AudioDecoderConfiguration>();
        }
        return this.configurations;
    }
}
