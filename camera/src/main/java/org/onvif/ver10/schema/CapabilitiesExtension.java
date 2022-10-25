package org.onvif.ver10.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapabilitiesExtension", propOrder = {"any", "deviceIO", "display", "recording", "search", "replay", "receiver", "analyticsDevice",
    "extensions"})
public class CapabilitiesExtension {

  @XmlAnyElement(lax = true)
  protected List<java.lang.Object> any;
  @XmlElement(name = "DeviceIO")
  protected DeviceIOCapabilities deviceIO;
  @XmlElement(name = "Display")
  protected DisplayCapabilities display;
  @XmlElement(name = "Recording")
  protected RecordingCapabilities recording;
  @XmlElement(name = "Search")
  protected SearchCapabilities search;
  @XmlElement(name = "Replay")
  protected ReplayCapabilities replay;
  @XmlElement(name = "Receiver")
  protected ReceiverCapabilities receiver;
  @XmlElement(name = "AnalyticsDevice")
  protected AnalyticsDeviceCapabilities analyticsDevice;
  @XmlElement(name = "Extensions")
  protected CapabilitiesExtension2 extensions;

  /**
   * Gets the value of the any property.
   *
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
   * This is why there is not a <CODE>set</CODE> method for the any property.
   *
   * <p>
   * For example, to add a new item, do as follows:
   *
   * <pre>
   * getAny().add(newItem);
   * </pre>
   *
   *
   * <p>
   * Objects of the following type(s) are allowed in the list {@link Element } {@link java.lang.Object }
   */
  public List<java.lang.Object> getAny() {
    if (any == null) {
      any = new ArrayList<java.lang.Object>();
    }
    return this.any;
  }

  /**
   * Ruft den Wert der deviceIO-Eigenschaft ab.
   *
   * @return possible object is {@link DeviceIOCapabilities }
   */
  public DeviceIOCapabilities getDeviceIO() {
    return deviceIO;
  }

  /**
   * Legt den Wert der deviceIO-Eigenschaft fest.
   *
   * @param value allowed object is {@link DeviceIOCapabilities }
   */
  public void setDeviceIO(DeviceIOCapabilities value) {
    this.deviceIO = value;
  }

  /**
   * Ruft den Wert der display-Eigenschaft ab.
   *
   * @return possible object is {@link DisplayCapabilities }
   */
  public DisplayCapabilities getDisplay() {
    return display;
  }

  /**
   * Legt den Wert der display-Eigenschaft fest.
   *
   * @param value allowed object is {@link DisplayCapabilities }
   */
  public void setDisplay(DisplayCapabilities value) {
    this.display = value;
  }

  /**
   * Ruft den Wert der recording-Eigenschaft ab.
   *
   * @return possible object is {@link RecordingCapabilities }
   */
  public RecordingCapabilities getRecording() {
    return recording;
  }

  /**
   * Legt den Wert der recording-Eigenschaft fest.
   *
   * @param value allowed object is {@link RecordingCapabilities }
   */
  public void setRecording(RecordingCapabilities value) {
    this.recording = value;
  }

  /**
   * Ruft den Wert der search-Eigenschaft ab.
   *
   * @return possible object is {@link SearchCapabilities }
   */
  public SearchCapabilities getSearch() {
    return search;
  }

  /**
   * Legt den Wert der search-Eigenschaft fest.
   *
   * @param value allowed object is {@link SearchCapabilities }
   */
  public void setSearch(SearchCapabilities value) {
    this.search = value;
  }

  /**
   * Ruft den Wert der replay-Eigenschaft ab.
   *
   * @return possible object is {@link ReplayCapabilities }
   */
  public ReplayCapabilities getReplay() {
    return replay;
  }

  /**
   * Legt den Wert der replay-Eigenschaft fest.
   *
   * @param value allowed object is {@link ReplayCapabilities }
   */
  public void setReplay(ReplayCapabilities value) {
    this.replay = value;
  }

  /**
   * Ruft den Wert der receiver-Eigenschaft ab.
   *
   * @return possible object is {@link ReceiverCapabilities }
   */
  public ReceiverCapabilities getReceiver() {
    return receiver;
  }

  /**
   * Legt den Wert der receiver-Eigenschaft fest.
   *
   * @param value allowed object is {@link ReceiverCapabilities }
   */
  public void setReceiver(ReceiverCapabilities value) {
    this.receiver = value;
  }

  /**
   * Ruft den Wert der analyticsDevice-Eigenschaft ab.
   *
   * @return possible object is {@link AnalyticsDeviceCapabilities }
   */
  public AnalyticsDeviceCapabilities getAnalyticsDevice() {
    return analyticsDevice;
  }

  /**
   * Legt den Wert der analyticsDevice-Eigenschaft fest.
   *
   * @param value allowed object is {@link AnalyticsDeviceCapabilities }
   */
  public void setAnalyticsDevice(AnalyticsDeviceCapabilities value) {
    this.analyticsDevice = value;
  }

  /**
   * Ruft den Wert der extensions-Eigenschaft ab.
   *
   * @return possible object is {@link CapabilitiesExtension2 }
   */
  public CapabilitiesExtension2 getExtensions() {
    return extensions;
  }

  /**
   * Legt den Wert der extensions-Eigenschaft fest.
   *
   * @param value allowed object is {@link CapabilitiesExtension2 }
   */
  public void setExtensions(CapabilitiesExtension2 value) {
    this.extensions = value;
  }

}
