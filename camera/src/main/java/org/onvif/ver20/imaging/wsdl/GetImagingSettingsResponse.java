package org.onvif.ver20.imaging.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.ImagingSettings20;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"imagingSettings"})
@XmlRootElement(name = "GetImagingSettingsResponse")
public class GetImagingSettingsResponse {


    @XmlElement(name = "ImagingSettings", required = true)
    protected ImagingSettings20 imagingSettings;


    public void setImagingSettings(ImagingSettings20 value) {
        this.imagingSettings = value;
    }
}
