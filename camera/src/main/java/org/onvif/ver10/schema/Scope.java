package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Scope",
        propOrder = {"scopeDef", "scopeItem"})
public class Scope {


    @XmlElement(name = "ScopeDef", required = true)
    protected ScopeDefinition scopeDef;


    @XmlElement(name = "ScopeItem", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String scopeItem;


    public void setScopeDef(ScopeDefinition value) {
        this.scopeDef = value;
    }


    public void setScopeItem(String value) {
        this.scopeItem = value;
    }
}
