package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "OSDTextOptions",
        propOrder = {
                "type",
                "fontSizeRange",
                "dateFormat",
                "timeFormat",
                "fontColor",
                "backgroundColor",
                "extension"
        })
public class OSDTextOptions {

    @XmlElement(name = "Type", required = true)
    protected List<String> type;


    @Getter @XmlElement(name = "FontSizeRange")
    protected IntRange fontSizeRange;

    @XmlElement(name = "DateFormat")
    protected List<String> dateFormat;

    @XmlElement(name = "TimeFormat")
    protected List<String> timeFormat;


    @Getter @XmlElement(name = "FontColor")
    protected OSDColorOptions fontColor;


    @Getter @XmlElement(name = "BackgroundColor")
    protected OSDColorOptions backgroundColor;


    @Getter @XmlElement(name = "Extension")
    protected OSDTextOptionsExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<String> getType() {
        if (type == null) {
            type = new ArrayList<String>();
        }
        return this.type;
    }


    public void setFontSizeRange(IntRange value) {
        this.fontSizeRange = value;
    }


    public List<String> getDateFormat() {
        if (dateFormat == null) {
            dateFormat = new ArrayList<String>();
        }
        return this.dateFormat;
    }


    public List<String> getTimeFormat() {
        if (timeFormat == null) {
            timeFormat = new ArrayList<String>();
        }
        return this.timeFormat;
    }


    public void setFontColor(OSDColorOptions value) {
        this.fontColor = value;
    }


    public void setBackgroundColor(OSDColorOptions value) {
        this.backgroundColor = value;
    }


    public void setExtension(OSDTextOptionsExtension value) {
        this.extension = value;
    }

}
