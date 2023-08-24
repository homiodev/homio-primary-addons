package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "DeviceIOCapabilities",
        propOrder = {
                "xAddr",
                "videoSources",
                "videoOutputs",
                "audioSources",
                "audioOutputs",
                "relayOutputs",
                "any"
        })
public class DeviceIOCapabilities {

    @XmlElement(name = "XAddr", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String xAddr;

    /**
     * -- GETTER --
     *  Ruft den Wert der videoSources-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "VideoSources")
    protected int videoSources;

    /**
     * -- GETTER --
     *  Ruft den Wert der videoOutputs-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "VideoOutputs")
    protected int videoOutputs;

    /**
     * -- GETTER --
     *  Ruft den Wert der audioSources-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "AudioSources")
    protected int audioSources;

    /**
     * -- GETTER --
     *  Ruft den Wert der audioOutputs-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "AudioOutputs")
    protected int audioOutputs;

    /**
     * -- GETTER --
     *  Ruft den Wert der relayOutputs-Eigenschaft ab.
     */
    @Getter @XmlElement(name = "RelayOutputs")
    protected int relayOutputs;

    @XmlAnyElement(lax = true)
    protected List<java.lang.Object> any;

    /**
     * -- GETTER --
     *  Gets a map that contains attributes that aren't bound to any typed property on this class.
     *  <p>the map is keyed by the name of the attribute and the value is the string value of the
     *  attribute.
     *  <p>the map returned by this method is live, and you can add new attribute by updating the map
     *  directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Ruft den Wert der xAddr-Eigenschaft ab.
     *
     * @return possible object is {@link String }
     */
    public String getXAddr() {
        return xAddr;
    }

    /**
     * Legt den Wert der xAddr-Eigenschaft fest.
     *
     * @param value allowed object is {@link String }
     */
    public void setXAddr(String value) {
        this.xAddr = value;
    }

    /**
     * Legt den Wert der videoSources-Eigenschaft fest.
     */
    public void setVideoSources(int value) {
        this.videoSources = value;
    }

    /**
     * Legt den Wert der videoOutputs-Eigenschaft fest.
     */
    public void setVideoOutputs(int value) {
        this.videoOutputs = value;
    }

    /**
     * Legt den Wert der audioSources-Eigenschaft fest.
     */
    public void setAudioSources(int value) {
        this.audioSources = value;
    }

    /**
     * Legt den Wert der audioOutputs-Eigenschaft fest.
     */
    public void setAudioOutputs(int value) {
        this.audioOutputs = value;
    }

    /**
     * Legt den Wert der relayOutputs-Eigenschaft fest.
     */
    public void setRelayOutputs(int value) {
        this.relayOutputs = value;
    }

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

}
