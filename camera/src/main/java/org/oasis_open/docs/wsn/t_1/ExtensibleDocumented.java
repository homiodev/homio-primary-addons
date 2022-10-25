package org.oasis_open.docs.wsn.t_1;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtensibleDocumented", propOrder = {"documentation"})
@XmlSeeAlso({TopicSetType.class, TopicNamespaceType.class, TopicType.class})
public abstract class ExtensibleDocumented {

  protected Documentation documentation;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<QName, String>();
}
