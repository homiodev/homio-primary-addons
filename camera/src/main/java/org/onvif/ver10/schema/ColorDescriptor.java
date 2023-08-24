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
        name = "ColorDescriptor",
        propOrder = {"colorCluster", "extension"})
public class ColorDescriptor {

    @XmlElement(name = "ColorCluster")
    protected List<ColorDescriptor.ColorCluster> colorCluster;


    @Getter @XmlElement(name = "Extension")
    protected ColorDescriptorExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<ColorDescriptor.ColorCluster> getColorCluster() {
        if (colorCluster == null) {
            colorCluster = new ArrayList<ColorDescriptor.ColorCluster>();
        }
        return this.colorCluster;
    }


    public void setExtension(ColorDescriptorExtension value) {
        this.extension = value;
    }


    @Getter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"color", "weight", "covariance"})
    public static class ColorCluster {


        @XmlElement(name = "Color", required = true)
        protected Color color;


        @XmlElement(name = "Weight")
        protected Float weight;


        @XmlElement(name = "Covariance")
        protected ColorCovariance covariance;


        public void setColor(Color value) {
            this.color = value;
        }


        public void setWeight(Float value) {
            this.weight = value;
        }


        public void setCovariance(ColorCovariance value) {
            this.covariance = value;
        }
    }
}
