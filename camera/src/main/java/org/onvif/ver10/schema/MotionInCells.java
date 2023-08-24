package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "MotionInCells",
        propOrder = {"any"})
public class MotionInCells {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlAttribute(name = "Columns", required = true)
    protected BigInteger columns;


    @Getter @XmlAttribute(name = "Rows", required = true)
    protected BigInteger rows;


    @Getter @XmlAttribute(name = "Cells", required = true)
    protected byte[] cells;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public void setColumns(BigInteger value) {
        this.columns = value;
    }


    public void setRows(BigInteger value) {
        this.rows = value;
    }


    public void setCells(byte[] value) {
        this.cells = value;
    }

}
