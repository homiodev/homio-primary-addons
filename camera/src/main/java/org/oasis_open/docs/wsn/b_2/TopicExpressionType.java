package org.oasis_open.docs.wsn.b_2;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TopicExpressionType", propOrder = { "content" })
@ToString
public class TopicExpressionType {

	@XmlMixed
	@XmlAnyElement(lax = true)
	protected List<Object> content;
	@XmlAttribute(name = "Dialect", required = true)
	@XmlSchemaType(name = "anyURI")
	protected String dialect;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	public List<Object> getContent() {
		if (content == null) {
			content = new ArrayList<>();
		}
		return this.content;
	}
}
