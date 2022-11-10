package org.touchhome.bundle.zigbee.converter.impl.config;

import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclColorControlCluster;
import lombok.extern.log4j.Log4j2;

@Log4j2
// TODO: remove
public class ZclColorControlConfig implements ZclClusterConfigHandler {

  private static final String CONFIG_ID = "zigbee_color_";
  private static final String CONFIG_CONTROLMETHOD = CONFIG_ID + "controlmethod";

  private ZclColorControlCluster colorControlCluster;
  private ControlMethod controlMethod = ControlMethod.AUTO;

  @Override
  public boolean initialize(ZclCluster cluster) {
    colorControlCluster = (ZclColorControlCluster) cluster;

    // Build a list of configuration supported by this channel
   /* List<ParameterOption> options = new ArrayList<>();

    options = new ArrayList<>();
    options.add(new ParameterOption(ControlMethod.AUTO.toString(), "Auto"));
    options.add(new ParameterOption(ControlMethod.HUE.toString(), "Hue Commands"));
    options.add(new ParameterOption(ControlMethod.XY.toString(), "XY Commands"));
    parameters.add(ConfigDescriptionParameterBuilder.create(CONFIG_CONTROLMETHOD, Type.TEXT)
        .withLabel("Color Control Method")
        .withDescription(
            "The commands used to control color. AUTO will use HUE if the device supports, otherwise XY")
        .withDefault(ControlMethod.AUTO.toString()).withOptions(options).withLimitToOptions(true).build());

    return !parameters.isEmpty();*/
    return true;
  }

//  private final List<ConfigDescriptionParameter> parameters = new ArrayList<>();

 /* public ZclColorControlConfig(Channel channel) {
    Configuration configuration = channel.getConfiguration();
    if (configuration.containsKey(CONFIG_CONTROLMETHOD)) {
      controlMethod = ControlMethod.valueOf((String) configuration.get(CONFIG_CONTROLMETHOD));
    }
  }*/

  @Override
  public boolean updateConfiguration() {
    return false;
  }

  public ControlMethod getControlMethod() {
    return controlMethod;
  }

/*
  @Override
  public List<ConfigDescriptionParameter> getConfiguration() {
    return parameters;
  }
*/

/*  @Override
  public boolean updateConfiguration(@NonNull Configuration currentConfiguration,
      Map<String, Object> configurationParameters) {

    boolean updated = false;
    for (Entry<String, Object> configurationParameter : configurationParameters.entrySet()) {
      if (!configurationParameter.getKey().startsWith(CONFIG_ID)) {
        continue;
      }
      // Ignore any configuration parameters that have not changed
      if (Objects.equals(configurationParameter.getValue(),
          currentConfiguration.get(configurationParameter.getKey()))) {
        log.debug("Configuration update: Ignored {} as no change", configurationParameter.getKey());
        continue;
      }

      log.debug("{}: Update ColorControl configuration property {}->{} ({})",
          colorControlCluster.getZigBeeAddress(), configurationParameter.getKey(),
          configurationParameter.getValue(), configurationParameter.getValue().getClass().getSimpleName());
      switch (configurationParameter.getKey()) {
        case CONFIG_CONTROLMETHOD:
          controlMethod = ControlMethod.valueOf((String) configurationParameter.getValue());
          break;
        default:
          log.warn("{}: Unhandled configuration property {}", colorControlCluster.getZigBeeAddress(),
              configurationParameter.getKey());
          break;
      }
    }

    return updated;
  }*/

  public enum ControlMethod {
    AUTO,
    HUE,
    XY
  }
}
