package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.PTZNode;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"ptzNode"})
@XmlRootElement(name = "GetNodeResponse")
public class GetNodeResponse {

    @XmlElement(name = "PTZNode", required = true)
    protected PTZNode ptzNode;


    public PTZNode getPTZNode() {
        return ptzNode;
    }


    public void setPTZNode(PTZNode value) {
        this.ptzNode = value;
    }
}
