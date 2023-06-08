package org.homio.addon.z2m.util;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Z2MDeviceDefinitionsModel {

    // for description inside json file only
    private int version;
    private List<Z2MDeviceDefinitionModel> devices;
    private List<Z2MDevicePropertiesModel> properties;
}
