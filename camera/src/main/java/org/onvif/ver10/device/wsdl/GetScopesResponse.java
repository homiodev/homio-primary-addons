







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.Scope;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"scopes"})
@XmlRootElement(name = "GetScopesResponse")
public class GetScopesResponse {

    @XmlElement(name = "Scopes", required = true)
    protected List<Scope> scopes;


    public List<Scope> getScopes() {
        if (scopes == null) {
            scopes = new ArrayList<Scope>();
        }
        return this.scopes;
    }
}
