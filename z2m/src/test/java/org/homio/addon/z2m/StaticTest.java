package org.homio.addon.z2m;

import static org.homio.api.util.CommonUtils.OBJECT_MAPPER;
import static org.homio.api.util.CommonUtils.YAML_OBJECT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.homio.addon.z2m.util.Z2MConfiguration;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionsModel;
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

        Z2MConfiguration configuration = YAML_OBJECT_MAPPER.readValue(getClass().getClassLoader().getResource("z2m_config.yaml"), Z2MConfiguration.class);
        assertNotNull(configuration.otherFields());
    }
}
