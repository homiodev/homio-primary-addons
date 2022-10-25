package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PTZSpeed", propOrder = {"panTilt", "zoom"})
public class PTZSpeed {

  @XmlElement(name = "PanTilt")
  protected Vector2D panTilt;
  @XmlElement(name = "Zoom")
  protected Vector1D zoom;

  public PTZSpeed(float x, float y, float z) {
    this.panTilt = new Vector2D();
    this.panTilt.x = x;
    this.panTilt.y = y;
    this.zoom = new Vector1D();
    this.zoom.x = z;
  }
}
