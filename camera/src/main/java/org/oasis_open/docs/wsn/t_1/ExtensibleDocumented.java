package org.oasis_open.docs.wsn.t_1;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtensibleDocumented", propOrder = { "documentation" })
@XmlSeeAlso({ TopicSetType.class, TopicNamespaceType.class, TopicType.class })
public abstract class ExtensibleDocumented {

	protected Documentation documentation;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();
}
