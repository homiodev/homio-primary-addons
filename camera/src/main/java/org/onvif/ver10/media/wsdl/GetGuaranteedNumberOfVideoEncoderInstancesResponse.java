







package org.onvif.ver10.media.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"totalNumber", "jpeg", "h264", "mpeg4"})
@XmlRootElement(name = "GetGuaranteedNumberOfVideoEncoderInstancesResponse")
public class GetGuaranteedNumberOfVideoEncoderInstancesResponse {


    @Getter @XmlElement(name = "TotalNumber")
    protected int totalNumber;

    @XmlElement(name = "JPEG")
    protected Integer jpeg;


    @Getter @XmlElement(name = "H264")
    protected Integer h264;

    @XmlElement(name = "MPEG4")
    protected Integer mpeg4;


    public void setTotalNumber(int value) {
        this.totalNumber = value;
    }


    public Integer getJPEG() {
        return jpeg;
    }


    public void setJPEG(Integer value) {
        this.jpeg = value;
    }


    public void setH264(Integer value) {
        this.h264 = value;
    }


    public Integer getMPEG4() {
        return mpeg4;
    }


    public void setMPEG4(Integer value) {
        this.mpeg4 = value;
    }
}
