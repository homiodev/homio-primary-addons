







package org.oasis_open.docs.wsn.b_2;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"topic", "any"})
@XmlRootElement(name = "GetCurrentMessage")
public class GetCurrentMessage {


    @XmlElement(name = "Topic", required = true)
    protected TopicExpressionType topic;

    @XmlAnyElement(lax = true)
    protected List<Object> any;


    public void setTopic(TopicExpressionType value) {
        this.topic = value;
    }


    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }
}
