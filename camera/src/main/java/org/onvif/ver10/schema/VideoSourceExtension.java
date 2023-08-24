package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "VideoSourceExtension",
        propOrder = {"any", "imaging", "extension"})
public class VideoSourceExtension {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;


    @Getter @XmlElement(name = "Imaging")
    protected ImagingSettings20 imaging;


    @Getter @XmlElement(name = "Extension")
    protected VideoSourceExtension2 extension;


    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }


    public void setImaging(ImagingSettings20 value) {
        this.imaging = value;
    }


    public void setExtension(VideoSourceExtension2 value) {
        this.extension = value;
    }
}
