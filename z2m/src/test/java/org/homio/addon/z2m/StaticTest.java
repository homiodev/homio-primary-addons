package org.homio.addon.z2m;

import static org.homio.api.util.JsonUtils.YAML_OBJECT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.SystemUtils;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.Z2MConfiguration;
import org.homio.api.model.device.ConfigDeviceDefinition;
import org.homio.api.model.device.ConfigDeviceDefinitionService;
import org.homio.api.model.device.ConfigDeviceEndpoint;
import org.junit.jupiter.api.Test;

public class StaticTest {

    @Test
    public void startupTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ApplianceModel> applianceModels = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("z2m_devices.json"),
                new TypeReference<>() {
                });
        assertEquals(2, applianceModels.size());
        System.setProperty("rootPath", SystemUtils.getUserHome().toString());

        ConfigDeviceDefinitionService service = new ConfigDeviceDefinitionService("zigbee-devices.json");

        assertFalse(service.getDeviceDefinitions().isEmpty());
        assertFalse(service.getDeviceEndpoints().isEmpty());
        // assert avoid duplications
        Map<String, ConfigDeviceEndpoint> propertyMap =
                service.getDeviceEndpoints().values().stream()
                        .collect(Collectors.toMap(ConfigDeviceEndpoint::getName, Function.identity()));
        assertEquals(propertyMap.size(), service.getDeviceEndpoints().size());
        //
        for (ConfigDeviceDefinition deviceDefinition : service.getDeviceDefinitions().values()) {
            assertNotNull(deviceDefinition.getName());
        }

        Z2MConfiguration configuration = YAML_OBJECT_MAPPER.readValue(getClass().getClassLoader().getResource("z2m_config.yaml"), Z2MConfiguration.class);
        assertNotNull(configuration.otherFields());
    }
}
