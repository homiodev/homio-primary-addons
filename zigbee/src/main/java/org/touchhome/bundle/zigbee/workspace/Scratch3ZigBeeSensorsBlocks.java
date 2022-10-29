package org.touchhome.bundle.zigbee.workspace;

import static org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeBlocks.ZIGBEE_ALARM_URL;
import static org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeBlocks.ZIGBEE_CLUSTER_ID_URL;
import static org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeBlocks.ZIGBEE_CLUSTER_NAME_URL;
import static org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeBlocks.fetchState;
import static org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeBlocks.getZigBeeDevice;

import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIlluminanceMeasurementCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclOccupancySensingCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclPressureMeasurementCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclRelativeHumidityMeasurementCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclTemperatureMeasurementCluster;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.MenuBlock;
import org.touchhome.bundle.zigbee.ZigBeeBundleEntryPoint;
import org.touchhome.bundle.zigbee.ZigBeeDeviceStateUUID;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverterIasFireIndicator;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverterIasWaterSensor;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;

@Getter
@Component
public class Scratch3ZigBeeSensorsBlocks extends Scratch3ZigBeeExtensionBlocks {

  private static final String ALARM_SENSOR = "ALARM_SENSOR";
  private static final String WATER_SENSOR = "WATER_SENSOR";
  private static final String SMOKE_SENSOR = "SMOKE_SENSOR";
  private static final String ILLUMINANCE_SENSOR = "ILLUMINANCE_SENSOR";
  private static final String OCCUPANCY_SENSOR = "OCCUPANCY_SENSOR";
  private static final String TEMPERATURE_SENSOR = "TEMPERATURE_SENSOR";
  private static final String PRESSURE_SENSOR = "PRESSURE_SENSOR";
  private static final String HUMIDITY_SENSOR = "HUMIDITY_SENSOR";

  private final MenuBlock.ServerMenuBlock alarmSensorMenu;
  private final MenuBlock.ServerMenuBlock smokeSensorMenu;
  private final MenuBlock.ServerMenuBlock illuminanceSensorMenu;
  private final MenuBlock.ServerMenuBlock occupancySensorMenu;
  private final MenuBlock.ServerMenuBlock temperatureSensorMenu;
  private final MenuBlock.ServerMenuBlock pressureSensorMenu;
  private final MenuBlock.ServerMenuBlock humiditySensorMenu;
  private final MenuBlock.ServerMenuBlock waterSensorMenu;

  private final ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener;

  public Scratch3ZigBeeSensorsBlocks(EntityContext entityContext,
      ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener,
      ZigBeeBundleEntryPoint zigBeeBundleEntryPoint) {
    super("#8a6854", entityContext, zigBeeBundleEntryPoint, "sensor");
    this.zigBeeDeviceUpdateValueListener = zigBeeDeviceUpdateValueListener;

    // Menu
    this.alarmSensorMenu = menuServer("alarmSensorMenu", ZIGBEE_ALARM_URL, "Alarm Sensor");
    this.smokeSensorMenu = menuServer("smokeSensorMenu", ZIGBEE_CLUSTER_NAME_URL + ZigBeeConverterIasFireIndicator.CLUSTER_NAME,
        "Smoke sensor");
    this.waterSensorMenu = menuServer("waterSensorMenu", ZIGBEE_CLUSTER_NAME_URL + ZigBeeConverterIasWaterSensor.CLUSTER_NAME,
        "Water sensor");
    this.illuminanceSensorMenu = menuServer("illuminanceSensorMenu", ZIGBEE_CLUSTER_ID_URL + ZclIlluminanceMeasurementCluster.CLUSTER_ID,
        "Illuminance Sensor", "-", ZclIlluminanceMeasurementCluster.CLUSTER_ID);
    this.occupancySensorMenu = menuServer("occupancySensorMenu", ZIGBEE_CLUSTER_ID_URL + ZclOccupancySensingCluster.CLUSTER_ID,
        "Occupancy Sensor", "-", ZclOccupancySensingCluster.CLUSTER_ID);

    this.temperatureSensorMenu = menuServer("temperatureSensorMenu", ZIGBEE_CLUSTER_ID_URL + ZclTemperatureMeasurementCluster.CLUSTER_ID,
        "Temperature Sensor", "-", ZclTemperatureMeasurementCluster.CLUSTER_ID);
    this.pressureSensorMenu = menuServer("pressureSensorMenu", ZIGBEE_CLUSTER_ID_URL + ZclPressureMeasurementCluster.CLUSTER_ID,
        "Pressure Sensor", "-", ZclPressureMeasurementCluster.CLUSTER_ID);
    this.humiditySensorMenu = menuServer("humiditySensorMenu", ZIGBEE_CLUSTER_ID_URL + ZclRelativeHumidityMeasurementCluster.CLUSTER_ID,
        "Humidity Sensor", "-", ZclRelativeHumidityMeasurementCluster.CLUSTER_ID);

    // illuminance sensor
    blockTargetReporter(10, "illuminance_value",
        "illuminance [ILLUMINANCE_SENSOR]", this::illuminanceValueEvaluate, Scratch3ZigBeeBlock.class, block -> {
          block.overrideColor("#802F59");
          block.addArgument(ILLUMINANCE_SENSOR, illuminanceSensorMenu);
          block.setDefaultLinkFloatHandler(entityContext, zigBeeDeviceUpdateValueListener,
              "Illuminance", ILLUMINANCE_SENSOR, illuminanceSensorMenu, ZclIlluminanceMeasurementCluster.CLUSTER_ID,
              null, "zigbee-sensor");
        });
    // motion sensor
    blockTargetReporter(20, "motion_value",
        "motion detected [OCCUPANCY_SENSOR]", this::motionDetectedEvaluate, Scratch3ZigBeeBlock.class, block -> {
          block.overrideColor("#802F59");
          block.addArgument(OCCUPANCY_SENSOR, occupancySensorMenu);
          block.setDefaultLinkFloatHandler(entityContext, zigBeeDeviceUpdateValueListener, "Occupancy",
              OCCUPANCY_SENSOR, occupancySensorMenu, ZclOccupancySensingCluster.CLUSTER_ID, null, "zigbee-sensor");
          block.appendSpace();
        });

    // temperature sensor
    blockTargetReporter(30, "temperature_value",
        "temperature value [TEMPERATURE_SENSOR]", this::temperatureValueEvaluate, Scratch3ZigBeeBlock.class, block -> {
          block.overrideColor("#633582");
          block.addArgument(TEMPERATURE_SENSOR, temperatureSensorMenu);
          block.setDefaultLinkFloatHandler(entityContext, zigBeeDeviceUpdateValueListener,
              "Temperature", TEMPERATURE_SENSOR, temperatureSensorMenu, ZclTemperatureMeasurementCluster.CLUSTER_ID,
              null, "zigbee-sensor");
        });
    // pressure sensor
    blockTargetReporter(50, "pressure_value",
        "pressure value[PRESSURE_SENSOR]", this::pressureValueEvaluate, Scratch3ZigBeeBlock.class, block -> {
          block.overrideColor("#633582");
          block.addArgument(PRESSURE_SENSOR, pressureSensorMenu);
          block.setDefaultLinkFloatHandler(entityContext, zigBeeDeviceUpdateValueListener,
              "Pressure", PRESSURE_SENSOR, pressureSensorMenu, ZclPressureMeasurementCluster.CLUSTER_ID,
              null, "zigbee-sensor");
        });

    // humidity sensor
    blockTargetReporter(60, "humidity_value",
        "humidity value [HUMIDITY_SENSOR]", this::humidityValueEvaluate, Scratch3ZigBeeBlock.class, block -> {
          block.overrideColor("#633582");
          block.addArgument(HUMIDITY_SENSOR, humiditySensorMenu);
          block.setDefaultLinkFloatHandler(entityContext, zigBeeDeviceUpdateValueListener, "Humidity",
              HUMIDITY_SENSOR, humiditySensorMenu, ZclRelativeHumidityMeasurementCluster.CLUSTER_ID, null, "zigbee-sensor");
          block.appendSpace();
        });

    // smoke sensor
    blockTargetReporter(70, "smoke_sensor_value",
        "Smoke sensor [SMOKE_SENSOR]", this::smokeSensorValueEval, Scratch3ZigBeeBlock.class, block -> {
          block.addArgument(SMOKE_SENSOR, this.smokeSensorMenu);
          block.setDefaultLinkBooleanHandler(entityContext, zigBeeDeviceUpdateValueListener,
              "Smoke sensor", SMOKE_SENSOR, smokeSensorMenu, ZclIasZoneCluster.CLUSTER_ID,
              ZigBeeConverterIasFireIndicator.CLUSTER_NAME, "zigbee-sensor");
        });

    // water sensor
    blockTargetReporter(80, "water_sensor_value",
        "water sensor [WATER_SENSOR]", this::waterSensorValueEval, Scratch3ZigBeeBlock.class, block -> {
          block.addArgument(WATER_SENSOR, this.waterSensorMenu);
          block.setDefaultLinkBooleanHandler(entityContext, zigBeeDeviceUpdateValueListener,
              "Water sensor", WATER_SENSOR, waterSensorMenu, ZclIasZoneCluster.CLUSTER_ID,
              ZigBeeConverterIasWaterSensor.CLUSTER_NAME, "zigbee-sensor");
        });

    blockHat(90, "when_alarm_event_detected",
        "alarm [ALARM_SENSOR] detected", this::whenAlarmEventDetectedHandler, block -> {
          block.addArgument(ALARM_SENSOR, this.alarmSensorMenu);
        });
  }

  private void whenAlarmEventDetectedHandler(WorkspaceBlock workspaceBlock) {
    workspaceBlock.handleNext(next -> {
      String[] keys = workspaceBlock.getMenuValue(ALARM_SENSOR, alarmSensorMenu).split("/");
      ZigBeeDeviceEntity zigBeeDevice = getZigBeeDevice(workspaceBlock, keys[0]);
      String alarmCluster = keys[1];
      BroadcastLock lock = workspaceBlock.getBroadcastLockManager().getOrCreateLock(workspaceBlock);
      ZigBeeDeviceStateUUID zigBeeDeviceStateUUID = ZigBeeDeviceStateUUID.require(zigBeeDevice.getIeeeAddress(),
          ZclIasZoneCluster.CLUSTER_ID, null, alarmCluster);
      this.zigBeeDeviceUpdateValueListener.addListener(zigBeeDeviceStateUUID, deviceState -> {
        if (deviceState.getState() == OnOffType.ON) {
          lock.signalAll();
        }
      });
      workspaceBlock.subscribeToLock(lock, next::handle);
    });
  }

  private State waterSensorValueEval(WorkspaceBlock workspaceBlock) {
    return fetchState(Scratch3ZigBeeBlocks.fetchValueFromDevice(zigBeeDeviceUpdateValueListener, workspaceBlock,
        ZclIasZoneCluster.CLUSTER_ID, ZigBeeConverterIasWaterSensor.CLUSTER_NAME, WATER_SENSOR, waterSensorMenu));
  }

  private State smokeSensorValueEval(WorkspaceBlock workspaceBlock) {
    return fetchState(Scratch3ZigBeeBlocks.fetchValueFromDevice(zigBeeDeviceUpdateValueListener, workspaceBlock,
        ZclIasZoneCluster.CLUSTER_ID, ZigBeeConverterIasFireIndicator.CLUSTER_NAME, SMOKE_SENSOR, smokeSensorMenu));
  }

  private ScratchDeviceState fetchValueFromDevice(WorkspaceBlock workspaceBlock, int clustersId, String sensor,
      MenuBlock.ServerMenuBlock menuBlock) {
    return Scratch3ZigBeeBlocks.fetchValueFromDevice(zigBeeDeviceUpdateValueListener, workspaceBlock,
        new Integer[]{clustersId}, sensor, menuBlock);
  }

  private State motionDetectedEvaluate(WorkspaceBlock workspaceBlock) {
    ScratchDeviceState scratchDeviceState =
        fetchValueFromDevice(workspaceBlock, ZclOccupancySensingCluster.CLUSTER_ID, OCCUPANCY_SENSOR,
            occupancySensorMenu);
    if (scratchDeviceState != null && !scratchDeviceState.isHandled()) {
      scratchDeviceState.setHandled(true);
      return OnOffType.of(true);
    }
    return OnOffType.of(false);
  }

  private State humidityValueEvaluate(WorkspaceBlock workspaceBlock) {
    return fetchState(fetchValueFromDevice(workspaceBlock, ZclRelativeHumidityMeasurementCluster.CLUSTER_ID, HUMIDITY_SENSOR,
        humiditySensorMenu));
  }

  private State pressureValueEvaluate(WorkspaceBlock workspaceBlock) {
    return fetchState(fetchValueFromDevice(workspaceBlock, ZclPressureMeasurementCluster.CLUSTER_ID, PRESSURE_SENSOR,
        pressureSensorMenu));
  }

  private State temperatureValueEvaluate(WorkspaceBlock workspaceBlock) {
    return fetchState(fetchValueFromDevice(workspaceBlock, ZclTemperatureMeasurementCluster.CLUSTER_ID, TEMPERATURE_SENSOR,
        temperatureSensorMenu));
  }

  private State illuminanceValueEvaluate(WorkspaceBlock workspaceBlock) {
    return fetchState(fetchValueFromDevice(workspaceBlock, ZclIlluminanceMeasurementCluster.CLUSTER_ID, ILLUMINANCE_SENSOR,
        illuminanceSensorMenu));
  }
}
