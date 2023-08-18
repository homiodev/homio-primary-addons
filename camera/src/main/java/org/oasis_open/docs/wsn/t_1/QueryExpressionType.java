package org.oasis_open.docs.wsn.t_1;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "QueryExpressionType",
        propOrder = {"content"})
public class QueryExpressionType {

    @XmlMixed
    @XmlAnyElement(lax = true)
    protected List<Object> content;

    @XmlAttribute(name = "Dialect", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String dialect;

    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }
}
