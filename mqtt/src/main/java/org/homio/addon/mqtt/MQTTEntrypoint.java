package org.homio.addon.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.homio.addon.mqtt.entity.MQTTClientEntity;
import org.homio.api.AddonEntrypoint;
import org.homio.api.Context;
import org.homio.api.ContextService;
import org.homio.api.util.HardwareUtils;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Log4j2
@Component
@RequiredArgsConstructor
public class MQTTEntrypoint implements AddonEntrypoint {

    private final Context context;

    @SneakyThrows
    public void init() {
        context.service().registerEntityTypeForSelection(MQTTClientEntity.class, ContextService.MQTT_SERVICE);
        context.ui().console().registerPluginName("MQTT");
        context.bgp().builder("check-mqtt").execute(() -> {
            Set<String> existIps = context.db().findAll(MQTTClientEntity.class).stream()
                    .map(MQTTClientEntity::getHostname).collect(Collectors.toSet());
            HardwareUtils.scanForDevice(context, 1883, "MQTT", ip -> {
                if (existIps.contains(ip)) {
                    return false;
                }
                String serverURL = format("tcp://%s:%d", ip, 1883);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setConnectionTimeout(10_000);
                MqttClient mqttClient = new MqttClient(serverURL, UUID.randomUUID().toString(), new MemoryPersistence());
                mqttClient.connect(options);
                mqttClient.disconnectForcibly();
                return true;
            }, ip -> {
                MQTTClientEntity entity = new MQTTClientEntity();
                entity.setHostname(ip);
                context.db().save(entity);
            });
        });
    }
}
