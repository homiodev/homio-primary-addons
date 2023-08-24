package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Java-Klasse f�r VideoEncoderOptionsExtension complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="VideoEncoderOptionsExtension">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="JPEG" type="{http://www.onvif.org/ver10/schema}JpegOptions2" minOccurs="0"/>
 *         <element name="MPEG4" type="{http://www.onvif.org/ver10/schema}Mpeg4Options2" minOccurs="0"/>
 *         <element name="H264" type="{http://www.onvif.org/ver10/schema}H264Options2" minOccurs="0"/>
 *         <element name="Extension" type="{http://www.onvif.org/ver10/schema}VideoEncoderOptionsExtension2" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "VideoEncoderOptionsExtension",
        propOrder = {"any", "jpeg", "mpeg4", "h264", "extension"})
public class VideoEncoderOptionsExtension {

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    @XmlElement(name = "JPEG")
    protected JpegOptions2 jpeg;

    @XmlElement(name = "MPEG4")
    protected Mpeg4Options2 mpeg4;

    /**
     * -- GETTER --
     *  Ruft den Wert der h264-Eigenschaft ab.
     *
     * @return possible object is {@link H264Options2 }
     */
    @Getter @XmlElement(name = "H264")
    protected H264Options2 h264;

    /**
     * -- GETTER --
     *  Ruft den Wert der extension-Eigenschaft ab.
     *
     * @return possible object is {@link VideoEncoderOptionsExtension2 }
     */
    @Getter @XmlElement(name = "Extension")
    protected VideoEncoderOptionsExtension2 extension;

    /**
     * Gets the value of the any property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the any
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getAny().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link Element } {@link
     * java.lang.Object }
     */
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
    }

    /**
     * Ruft den Wert der jpeg-Eigenschaft ab.
     *
     * @return possible object is {@link JpegOptions2 }
     */
    public JpegOptions2 getJPEG() {
        return jpeg;
    }

    /**
     * Legt den Wert der jpeg-Eigenschaft fest.
     *
     * @param value allowed object is {@link JpegOptions2 }
     */
    public void setJPEG(JpegOptions2 value) {
        this.jpeg = value;
    }

    /**
     * Ruft den Wert der mpeg4-Eigenschaft ab.
     *
     * @return possible object is {@link Mpeg4Options2 }
     */
    public Mpeg4Options2 getMPEG4() {
        return mpeg4;
    }

    /**
     * Legt den Wert der mpeg4-Eigenschaft fest.
     *
     * @param value allowed object is {@link Mpeg4Options2 }
     */
    public void setMPEG4(Mpeg4Options2 value) {
        this.mpeg4 = value;
    }

    /**
     * Legt den Wert der h264-Eigenschaft fest.
     *
     * @param value allowed object is {@link H264Options2 }
     */
    public void setH264(H264Options2 value) {
        this.h264 = value;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     *
     * @param value allowed object is {@link VideoEncoderOptionsExtension2 }
     */
    public void setExtension(VideoEncoderOptionsExtension2 value) {
        this.extension = value;
    }
}
