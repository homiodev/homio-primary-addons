







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.SystemLog;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"systemLog"})
@XmlRootElement(name = "GetSystemLogResponse")
public class GetSystemLogResponse {


    @XmlElement(name = "SystemLog", required = true)
    protected SystemLog systemLog;


    public void setSystemLog(SystemLog value) {
        this.systemLog = value;
    }
}
