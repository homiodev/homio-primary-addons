package org.homio.addon.z2m;

import static org.homio.api.util.CommonUtils.OBJECT_MAPPER;
import static org.homio.api.util.CommonUtils.YAML_OBJECT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.Z2MConfiguration;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.ModelGroups;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionsModel;
import org.homio.addon.z2m.util.Z2MPropertyModel;
import org.junit.jupiter.api.Test;

public class StaticTest {

    @Test
    public void startupTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ApplianceModel> applianceModels = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("z2m_devices.json"),
            new TypeReference<>() {});
        assertEquals(2, applianceModels.size());

        Z2MDeviceDefinitionsModel deviceConfigurations = OBJECT_MAPPER.readValue(getClass()
            .getClassLoader().getResource("zigbee-devices.json"), Z2MDeviceDefinitionsModel.class);
        assertFalse(deviceConfigurations.getDevices().isEmpty());
        assertFalse(deviceConfigurations.getProperties().isEmpty());
        // assert avoid duplications
        Map<String, Z2MPropertyModel> propertyMap = deviceConfigurations.getProperties().stream()
                                                                        .collect(Collectors.toMap(Z2MPropertyModel::getName, Function.identity()));
        assertEquals(propertyMap.size(), deviceConfigurations.getProperties().size());
        //
        assertFalse(deviceConfigurations.getGroups().isEmpty());
        assertNotNull(deviceConfigurations.getGroups().get(0).getName());
        for (Z2MDeviceDefinitionModel device : deviceConfigurations.getDevices()) {
            assertNotNull(device.getName());
        }
        for (ModelGroups group : deviceConfigurations.getGroups()) {
            assertNotNull(group.getName());
            assertFalse(group.getModels().isEmpty());
            assertEquals(group.getModels().size(), new HashSet<>(group.getModels()).size());
        }

        for (Z2MDeviceDefinitionModel node : deviceConfigurations.getDevices()) {
            if (node.getGroupRef() != null) {
                ModelGroups modelGroups = deviceConfigurations.getGroups().stream()
                                                              .filter(g -> g.getName().equals(node.getGroupRef()))
                                                              .findAny()
                                                              .orElseThrow(
                                                                  () -> new IllegalStateException("Unable to find z2m model group: " + node.getGroupRef()));
                assertNotNull(modelGroups);
            }
        }

        Z2MConfiguration configuration = YAML_OBJECT_MAPPER.readValue(getClass().getClassLoader().getResource("z2m_config.yaml"), Z2MConfiguration.class);
        assertNotNull(configuration.otherFields());
    }
}
