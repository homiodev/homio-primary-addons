package org.onvif.ver20.ptz.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.PTZNode;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"ptzNode"})
@XmlRootElement(name = "GetNodesResponse")
public class GetNodesResponse {

    @XmlElement(name = "PTZNode")
    protected List<PTZNode> ptzNode;


    public List<PTZNode> getPTZNode() {
        if (ptzNode == null) {
            ptzNode = new ArrayList<PTZNode>();
        }
        return this.ptzNode;
    }
}
