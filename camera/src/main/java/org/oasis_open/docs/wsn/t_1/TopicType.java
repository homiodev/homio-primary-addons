package org.oasis_open.docs.wsn.t_1;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TopicType", propOrder = {"messagePattern", "topic", "any"})
@XmlSeeAlso({org.oasis_open.docs.wsn.t_1.TopicNamespaceType.Topic.class})
public class TopicType extends ExtensibleDocumented {

    @XmlElement(name = "MessagePattern")
    protected QueryExpressionType messagePattern;
    @XmlElement(name = "Topic")
    protected List<TopicType> topic;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(name = "messageTypes")
    protected List<QName> messageTypes;
    @XmlAttribute(name = "final")
    protected Boolean _final;

    public List<TopicType> getTopic() {
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

    public List<QName> getMessageTypes() {
        if (messageTypes == null) {
            messageTypes = new ArrayList<QName>();
        }
        return this.messageTypes;
    }

    public boolean isFinal() {
        if (_final == null) {
            return false;
        } else {
            return _final;
        }
    }
}
