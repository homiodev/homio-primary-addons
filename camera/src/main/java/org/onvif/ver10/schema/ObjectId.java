package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import java.math.BigInteger;
import lombok.Getter;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectId")
@XmlSeeAlso({Object.class})
public class ObjectId {


    @XmlAttribute(name = "ObjectId")
    protected BigInteger objectId;


    public void setObjectId(BigInteger value) {
        this.objectId = value;
    }
}
