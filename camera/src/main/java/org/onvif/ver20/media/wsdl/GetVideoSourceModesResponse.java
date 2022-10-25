package org.onvif.ver20.media.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "videoSourceModes"
})
@XmlRootElement(name = "GetVideoSourceModesResponse")
public class GetVideoSourceModesResponse {

  @XmlElement(name = "VideoSourceModes", required = true)
  protected List<VideoSourceMode> videoSourceModes;

  /**
   * Gets the value of the videoSourceModes property.
   *
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
   * This is why there is not a <CODE>set</CODE> method for the videoSourceModes property.
   *
   * <p>
   * For example, to add a new item, do as follows:
   * <pre>
   *    getVideoSourceModes().add(newItem);
   * </pre>
   *
   *
   * <p>
   * Objects of the following type(s) are allowed in the list {@link VideoSourceMode }
   */
  public List<VideoSourceMode> getVideoSourceModes() {
    if (videoSourceModes == null) {
      videoSourceModes = new ArrayList<VideoSourceMode>();
    }
    return this.videoSourceModes;
  }

}
