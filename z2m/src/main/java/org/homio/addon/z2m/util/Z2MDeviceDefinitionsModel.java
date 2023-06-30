package org.homio.addon.z2m.util;

import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Z2MDeviceDefinitionsModel {

    // not uses for now
    private int version;
    private List<Z2MDeviceDefinitionModel> devices;
    // full list of all possible properties that contains property icon/color/etc...
    private List<Z2MPropertyModel> properties;
    // list of model grouped by common name
    private List<Z2MDeviceDefinitionModel.ModelGroups> groups;
    // set of properties that should be created but do not create variable for storing in db
    private Set<String> propertiesWithoutVariables;
    // set of properties hide from UI
    private Set<String> hiddenProperties;
    // set of properties that should be fully ignored
    private Set<String> ignoreProperties;
}
