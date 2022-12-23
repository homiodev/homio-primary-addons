package org.touchhome.bundle.mqtt;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.mqtt.entity.MQTTLocalClientEntity;

@Log4j2
@Component
@RequiredArgsConstructor
public class MQTTEntrypoint implements BundleEntrypoint {

  private final EntityContext entityContext;

  @SneakyThrows
  public void init() {
    entityContext.ui().registerConsolePluginName("MQTT");
    entityContext.bgp().builder("check-mqtt").execute(() -> {
      Set<String> existIps = entityContext.findAll(MQTTLocalClientEntity.class).stream()
          .map(MQTTLocalClientEntity::getHostname).collect(Collectors.toSet());
      TouchHomeUtils.scanForDevice(entityContext, 1883, "MQTT", ip -> {
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

  @Override
  public String getBundleId() {
    return "mqtt";
  }

  @Override
  public int order() {
    return 2000;
  }
}
