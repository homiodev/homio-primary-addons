package org.touchhome.bundle.zigbee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.touchhome.bundle.api.util.Units;
import org.touchhome.bundle.zigbee.util.ClusterConfigurations;
import org.touchhome.bundle.zigbee.util.DeviceConfigurations;
import org.touchhome.bundle.zigbee.util.Z2MConfiguration;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO;
import org.touchhome.bundle.zigbee.util.Z2MPropertyDTO;
import org.touchhome.common.util.CommonUtils;

public class StaticTest {

    @SneakyThrows
    @Test
    public void startupTest() {
        DeviceConfigurations.getDefineEndpoints();
        ClusterConfigurations.getClusterConfigurations();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Z2MDeviceDTO> z2MDeviceDTOS = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("z2m_devices.json"), new TypeReference<>() {});
        assertEquals(2, z2MDeviceDTOS.size());

        ArrayNode devices = CommonUtils.OBJECT_MAPPER.readValue(getClass().getClassLoader().getResource("zigbee-devices.json"), ArrayNode.class);
        assertFalse(devices.isEmpty());

        List<Z2MPropertyDTO> z2MPropertyDTOS = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("zigbee-device-properties.json"), new TypeReference<>() {});
        assertFalse(z2MPropertyDTOS.isEmpty());

        CommonUtils.YAML_OBJECT_MAPPER.readValue(getClass().getClassLoader().getResource("z2m_config.yaml"), Z2MConfiguration.class);

        assertNotNull(Units.findUnit("kWh"));
    }
}
