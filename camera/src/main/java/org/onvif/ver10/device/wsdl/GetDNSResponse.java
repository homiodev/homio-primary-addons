package org.onvif.ver10.device.wsdl;

import lombok.Getter;
import lombok.Setter;
import org.onvif.ver10.schema.DNSInformation;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "dnsInformation" })
@XmlRootElement(name = "GetDNSResponse")
public class GetDNSResponse {

	@XmlElement(name = "DNSInformation", required = true)
	protected DNSInformation dnsInformation;
}
