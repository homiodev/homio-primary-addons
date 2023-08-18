//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.5-2 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2014.02.04 um 12:22:03 PM CET
//

package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.User;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"user"})
@XmlRootElement(name = "CreateUsers")
public class CreateUsers {

    @XmlElement(name = "User", required = true)
    protected List<User> user;

    /**
     * Gets the value of the user property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the user
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getUser().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link User }
     */
    public List<User> getUser() {
        if (user == null) {
            user = new ArrayList<User>();
        }
        return this.user;
    }
}
