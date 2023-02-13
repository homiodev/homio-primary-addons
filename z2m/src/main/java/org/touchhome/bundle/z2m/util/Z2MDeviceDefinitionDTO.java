package org.touchhome.bundle.z2m.util;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.touchhome.bundle.api.entity.zigbee.ZigBeeProperty;
import org.touchhome.bundle.z2m.model.Z2MDeviceEntity;

@Getter
@Setter
public class Z2MDeviceDefinitionDTO {

    private String name;
    private String icon;
    private String iconColor;
    private Set<String> model;
    private List<WidgetDefinition> widgets;
    private JsonNode options;

    @Getter
    @Setter
    public static class WidgetDefinition {

        private String type;
        private boolean autoDiscovery;
        private List<PropertyDefinition> properties;

        public List<ZigBeeProperty> getProperties(Z2MDeviceEntity z2MDeviceEntity) {
            if (properties != null) {
                return properties.stream()
                                 .map(p -> z2MDeviceEntity.getDeviceService().getProperties().get(p.getName()))
                                 .collect(Collectors.toList());
            }
            if (this.isAutoDiscovery()) {
                if ("button".equals(type)) {
                    return z2MDeviceEntity.getDeviceService().getProperties().values().stream()
                                          .filter(p -> p.getExpose().getName().startsWith("state"))
                                          .collect(Collectors.toList());
                }
            }
            return Collections.emptyList();
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PropertyDefinition {

            private String name;
        }
    }
}
