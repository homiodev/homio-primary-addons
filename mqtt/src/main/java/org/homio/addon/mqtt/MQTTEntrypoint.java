package org.homio.addon.mqtt;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.homio.addon.mqtt.entity.MQTTLocalClientEntity;
import org.homio.api.AddonEntrypoint;
import org.homio.api.EntityContext;
import org.homio.api.model.Icon;
import org.homio.api.util.CommonUtils;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class MQTTEntrypoint implements AddonEntrypoint {

    private final EntityContext entityContext;
    public static final String MQTT_RESOURCE = "ROLE_MQTT";

    @SneakyThrows
    public void init() {
        entityContext.registerResource(MQTT_RESOURCE);
        entityContext.ui().registerConsolePluginName("MQTT", MQTT_RESOURCE);
        entityContext.bgp().builder("check-mqtt").execute(() -> {
            Set<String> existIps = entityContext.findAll(MQTTLocalClientEntity.class).stream()
                                                .map(MQTTLocalClientEntity::getHostname).collect(Collectors.toSet());
            CommonUtils.scanForDevice(entityContext, 1883, "MQTT", ip -> {
                if (existIps.contains(ip)) {
                    return false;
                }
                String serverURL = String.format("tcp://%s:%d", ip, 1883);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setConnectionTimeout(10_000);
                MqttClient mqttClient = new MqttClient(serverURL, UUID.randomUUID().toString(), new MemoryPersistence());
                mqttClient.connect(options);
                mqttClient.disconnectForcibly();
                return true;
            }, ip -> {
                MQTTLocalClientEntity entity = new MQTTLocalClientEntity();
                entity.setHostname(ip);
                entityContext.save(entity);
            });
        });
    }
}
