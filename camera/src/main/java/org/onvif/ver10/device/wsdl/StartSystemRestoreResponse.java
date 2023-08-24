







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.Duration;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"uploadUri", "expectedDownTime"})
@XmlRootElement(name = "StartSystemRestoreResponse")
public class StartSystemRestoreResponse {


    @XmlElement(name = "UploadUri", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String uploadUri;


    @XmlElement(name = "ExpectedDownTime", required = true)
    protected Duration expectedDownTime;


    public void setUploadUri(String value) {
        this.uploadUri = value;
    }


    public void setExpectedDownTime(Duration value) {
        this.expectedDownTime = value;
    }
}
