package org.oasis_open.docs.wsn.b_2;

import org.oasis_open.docs.wsrf.bf_2.BaseFaultType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnrecognizedPolicyRequestFaultType", propOrder = {"unrecognizedPolicy"})
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
