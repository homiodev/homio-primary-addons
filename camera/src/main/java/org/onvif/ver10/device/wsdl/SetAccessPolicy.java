







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.BinaryData;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"policyFile"})
@XmlRootElement(name = "SetAccessPolicy")
public class SetAccessPolicy {


    @XmlElement(name = "PolicyFile", required = true)
    protected BinaryData policyFile;


    public void setPolicyFile(BinaryData value) {
        this.policyFile = value;
    }
}
