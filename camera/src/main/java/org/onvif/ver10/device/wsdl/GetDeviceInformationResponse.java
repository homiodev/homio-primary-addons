package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"manufacturer", "model", "firmwareVersion", "serialNumber", "hardwareId"})
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
}
