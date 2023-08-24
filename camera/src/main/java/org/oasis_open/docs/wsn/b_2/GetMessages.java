







package org.oasis_open.docs.wsn.b_2;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"maximumNumber", "any"})
@XmlRootElement(name = "GetMessages")
public class GetMessages {


    @XmlElement(name = "MaximumNumber")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger maximumNumber;

    @XmlAnyElement(lax = true)
    protected List<Object> any;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public void setMaximumNumber(BigInteger value) {
        this.maximumNumber = value;
    }


    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

}
