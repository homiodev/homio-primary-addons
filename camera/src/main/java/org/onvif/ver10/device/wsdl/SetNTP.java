







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.NetworkHost;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"fromDHCP", "ntpManual"})
@XmlRootElement(name = "SetNTP")
public class SetNTP {


    @Getter @XmlElement(name = "FromDHCP")
    protected boolean fromDHCP;

    @XmlElement(name = "NTPManual")
    protected List<NetworkHost> ntpManual;


    public void setFromDHCP(boolean value) {
        this.fromDHCP = value;
    }


    public List<NetworkHost> getNTPManual() {
        if (ntpManual == null) {
            ntpManual = new ArrayList<NetworkHost>();
        }
        return this.ntpManual;
    }
}
