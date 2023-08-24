







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"scopeItem"})
@XmlRootElement(name = "RemoveScopesResponse")
public class RemoveScopesResponse {

    @XmlElement(name = "ScopeItem")
    @XmlSchemaType(name = "anyURI")
    protected List<String> scopeItem;


    public List<String> getScopeItem() {
        if (scopeItem == null) {
            scopeItem = new ArrayList<String>();
        }
        return this.scopeItem;
    }
}
