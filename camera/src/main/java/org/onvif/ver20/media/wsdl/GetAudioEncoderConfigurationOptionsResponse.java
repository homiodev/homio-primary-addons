package org.onvif.ver20.media.wsdl;

import org.onvif.ver10.schema.AudioEncoder2ConfigurationOptions;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "options"
})
@XmlRootElement(name = "GetAudioEncoderConfigurationOptionsResponse")
public class GetAudioEncoderConfigurationOptionsResponse {

    @XmlElement(name = "Options", required = true)
    protected List<AudioEncoder2ConfigurationOptions> options;

    /**
     * Gets the value of the options property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the options property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOptions().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AudioEncoder2ConfigurationOptions }
     *
     *
     */
    public List<AudioEncoder2ConfigurationOptions> getOptions() {
        if (options == null) {
            options = new ArrayList<AudioEncoder2ConfigurationOptions>();
        }
        return this.options;
    }

}
