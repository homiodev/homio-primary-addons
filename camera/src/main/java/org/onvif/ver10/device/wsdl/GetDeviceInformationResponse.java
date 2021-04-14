package org.onvif.ver10.device.wsdl;

import lombok.Getter;
import lombok.Setter;
import org.xmlsoap.schemas.soap.envelope.Fault;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"manufacturer", "model", "firmwareVersion", "serialNumber", "hardwareId", "fault"})
@XmlRootElement(name = "GetDeviceInformationResponse")
public class GetDeviceInformationResponse {

    @XmlElement(name = "Manufacturer", required = true)
    protected String manufacturer;
    @XmlElement(name = "Model", required = true)
    protected String model;
    @XmlElement(name = "FirmwareVersion", required = true)
    protected String firmwareVersion;
    @XmlElement(name = "SerialNumber", required = true)
    protected String serialNumber;
    @XmlElement(name = "HardwareId", required = true)
    protected String hardwareId;
    @XmlElement(name = "Fault")
    protected Fault fault;
}
