//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.04 um 12:22:03 PM CET
//

package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"scopeItem"})
@XmlRootElement(name = "AddScopes")
public class AddScopes {

    @XmlElement(name = "ScopeItem", required = true)
    @XmlSchemaType(name = "anyURI")
    protected List<String> scopeItem;

    
    public List<String> getScopeItem() {
        if (scopeItem == null) {
            scopeItem = new ArrayList<String>();
        }
        return this.scopeItem;
    }
}
