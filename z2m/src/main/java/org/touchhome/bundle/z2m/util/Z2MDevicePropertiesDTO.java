package org.touchhome.bundle.z2m.util;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Z2MDevicePropertiesDTO {

    private String name;
    private String icon;
    private String iconColor;
    private int order;
    private String unit;
    private List<String> alias;
}