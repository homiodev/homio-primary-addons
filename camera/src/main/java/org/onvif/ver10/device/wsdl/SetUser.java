







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.User;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"user"})
@XmlRootElement(name = "SetUser")
public class SetUser {

    @XmlElement(name = "User", required = true)
    protected List<User> user;


    public List<User> getUser() {
        if (user == null) {
            user = new ArrayList<User>();
        }
        return this.user;
    }
}
