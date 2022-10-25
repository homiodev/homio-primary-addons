package org.oasis_open.docs.wsn.t_1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TopicNamespaceType", propOrder = {"topic", "any"})
public class TopicNamespaceType extends ExtensibleDocumented {

  @XmlElement(name = "Topic")
  protected List<TopicNamespaceType.Topic> topic;
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  @XmlAttribute(name = "name")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "NCName")
  protected String name;
  @XmlAttribute(name = "targetNamespace", required = true)
  @XmlSchemaType(name = "anyURI")
  protected String targetNamespace;
  @XmlAttribute(name = "final")
  protected Boolean _final;

  public List<TopicNamespaceType.Topic> getTopic() {
    if (topic == null) {
      topic = new ArrayList<>();
    }
    return this.topic;
  }

  public List<Object> getAny() {
    if (any == null) {
      any = new ArrayList<>();
    }
    return this.any;
  }

  public boolean isFinal() {
    if (_final == null) {
      return false;
    } else {
      return _final;
    }
  }

  @Getter
  @Setter
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "")
  public static class Topic extends TopicType {

    @XmlAttribute(name = "parent")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String parent;
  }
}
