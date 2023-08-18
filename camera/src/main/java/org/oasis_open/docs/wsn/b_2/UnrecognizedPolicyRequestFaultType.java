package org.oasis_open.docs.wsn.b_2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.oasis_open.docs.wsrf.bf_2.BaseFaultType;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "UnrecognizedPolicyRequestFaultType",
        propOrder = {"unrecognizedPolicy"})
public class UnrecognizedPolicyRequestFaultType extends BaseFaultType {

    @XmlElement(name = "UnrecognizedPolicy")
    protected List<QName> unrecognizedPolicy;

    public List<QName> getUnrecognizedPolicy() {
        if (unrecognizedPolicy == null) {
            unrecognizedPolicy = new ArrayList<>();
        }
        return this.unrecognizedPolicy;
    }
}
