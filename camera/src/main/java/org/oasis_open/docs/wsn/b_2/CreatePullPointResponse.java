







package org.oasis_open.docs.wsn.b_2;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3._2005._08.addressing.EndpointReferenceType;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"pullPoint", "any"})
@XmlRootElement(name = "CreatePullPointResponse")
public class CreatePullPointResponse {


    @XmlElement(name = "PullPoint", required = true)
    protected EndpointReferenceType pullPoint;

    @XmlAnyElement(lax = true)
    protected List<Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setPullPoint(EndpointReferenceType value) {
        this.pullPoint = value;
    }


    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

}
