package org.oasis_open.docs.wsn.t_1;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Documentation",
        propOrder = {"content"})
public class Documentation {

    @XmlMixed
    @XmlAnyElement(lax = true)
    protected List<Object> content;

    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }
}
