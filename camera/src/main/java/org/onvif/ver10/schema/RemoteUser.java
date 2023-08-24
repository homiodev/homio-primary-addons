package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "RemoteUser",
        propOrder = {"username", "password", "useDerivedPassword", "any"})
public class RemoteUser {


    @Getter @XmlElement(name = "Username", required = true)
    protected String username;


    @Getter @XmlElement(name = "Password")
    protected String password;


    @Getter @XmlElement(name = "UseDerivedPassword")
    protected boolean useDerivedPassword;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setUsername(String value) {
        this.username = value;
    }


    public void setPassword(String value) {
        this.password = value;
    }


    public void setUseDerivedPassword(boolean value) {
        this.useDerivedPassword = value;
    }


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

}
