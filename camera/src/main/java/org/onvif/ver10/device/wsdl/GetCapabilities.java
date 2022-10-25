package org.onvif.ver10.device.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.onvif.ver10.schema.CapabilityCategory;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"category"})
@XmlRootElement(name = "GetCapabilities")
public class GetCapabilities {

  @XmlElement(name = "Category")
  protected List<CapabilityCategory> category;

  public List<CapabilityCategory> getCategory() {
    if (category == null) {
      category = new ArrayList<>();
    }
    return this.category;
  }
}
