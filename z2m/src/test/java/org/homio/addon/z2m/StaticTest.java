package org.homio.addon.z2m;

import static org.homio.api.util.CommonUtils.OBJECT_MAPPER;
import static org.homio.api.util.CommonUtils.YAML_OBJECT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.SneakyThrows;
import org.homio.addon.z2m.util.Z2MConfiguration;
import org.homio.addon.z2m.util.Z2MDeviceDTO;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionDTO;
import org.homio.addon.z2m.util.Z2MDevicePropertiesDTO;
import org.junit.jupiter.api.Test;

public class StaticTest {

    @SneakyThrows
    @Test
    public void startupTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Z2MDeviceDTO> z2MDeviceDTOS = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("z2m_devices.json"),
            new TypeReference<>() {});
        assertEquals(2, z2MDeviceDTOS.size());

        List<Z2MDeviceDefinitionDTO> devices = OBJECT_MAPPER.readValue(getClass().getClassLoader().getResource("zigbee-devices.json"),
            new TypeReference<>() {});
        assertFalse(devices.isEmpty());

        List<Z2MDevicePropertiesDTO> z2MDevicePropertiesDTOS = objectMapper.readValue(
            getClass().getClassLoader().getResourceAsStream("zigbee-device-properties.json"),
            new TypeReference<>() {});
        assertFalse(z2MDevicePropertiesDTOS.isEmpty());

        YAML_OBJECT_MAPPER.readValue(getClass().getClassLoader().getResource("z2m_config.yaml"), Z2MConfiguration.class);
    }
}
