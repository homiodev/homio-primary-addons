package org.oasis_open.docs.wsn.t_1;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryExpressionType", propOrder = { "content" })
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
