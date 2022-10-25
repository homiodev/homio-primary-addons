package org.onvif.ver10.device.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"manufacturer", "model", "firmwareVersion", "serialNumber", "hardwareId"})
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
