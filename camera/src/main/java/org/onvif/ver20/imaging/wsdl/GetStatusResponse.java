package org.onvif.ver20.imaging.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.ImagingStatus20;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"status"})
@XmlRootElement(name = "GetStatusResponse")
public class GetStatusResponse {


    @XmlElement(name = "Status", required = true)
    protected ImagingStatus20 status;


    public void setStatus(ImagingStatus20 value) {
        this.status = value;
    }
}
