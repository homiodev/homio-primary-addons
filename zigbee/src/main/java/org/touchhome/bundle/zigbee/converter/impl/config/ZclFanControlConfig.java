package org.touchhome.bundle.zigbee.converter.impl.config;

import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclFanControlCluster;
import java.util.concurrent.ExecutionException;
import lombok.extern.log4j.Log4j2;

@Log4j2
// TODO: remove
public class ZclFanControlConfig implements ZclClusterConfigHandler {

  private static final String CONFIG_ID = "zigbee_fancontrol_";
  private static final String CONFIG_MODESEQUENCE = CONFIG_ID + "modesequence";

  private ZclFanControlCluster fanControlCluster;

//  private final List<ConfigDescriptionParameter> parameters = new ArrayList<>();

  @Override
  public boolean initialize(ZclCluster cluster) {
    fanControlCluster = (ZclFanControlCluster) cluster;
    try {
      Boolean result = fanControlCluster.discoverAttributes(false).get();
      if (!result) {
        log.debug("{}: Unable to get supported attributes for {}.", fanControlCluster.getZigBeeAddress(),
            fanControlCluster.getClusterName());
      }
    } catch (InterruptedException | ExecutionException e) {
      log.error("{}: Error getting supported attributes for {}. ", fanControlCluster.getZigBeeAddress(),
          fanControlCluster.getClusterName(), e);
    }

    // Build a list of configuration supported by this channel based on the attributes the cluster supports
   /* List<ParameterOption> options = new ArrayList<>();
    if (fanControlCluster.isAttributeSupported(ZclFanControlCluster.ATTR_FANMODESEQUENCE)) {
      options.add(new ParameterOption("0", "Low/Med/High"));
      options.add(new ParameterOption("1", "Low/High"));
      options.add(new ParameterOption("2", "Low/Med/High/Auto"));
      options.add(new ParameterOption("3", "Low/High/Auto"));
      options.add(new ParameterOption("4", "On/Auto"));

      parameters.add(ConfigDescriptionParameterBuilder.create(CONFIG_MODESEQUENCE, Type.INTEGER)
          .withLabel("Fan Mode Sequence").withDescription("Possible fan modes that may be selected")
          .withOptions(options).withMinimum(new BigDecimal(0)).withMaximum(new BigDecimal(4)).build());
    }

    return !parameters.isEmpty();*/
    return false;
  }

  @Override
  public boolean updateConfiguration() {
    return false;
  }

  /*@Override
  public List<ConfigDescriptionParameter> getConfiguration() {
    return parameters;
  }*/

  /*@Override
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

      log.debug("{}: Update LevelControl configuration property {}->{} ({})",
          fanControlCluster.getZigBeeAddress(), configurationParameter.getKey(),
          configurationParameter.getValue(), configurationParameter.getValue().getClass().getSimpleName());
      Integer response = null;
      switch (configurationParameter.getKey()) {
        case CONFIG_MODESEQUENCE:
          response = configureAttribute(ZclFanControlCluster.ATTR_FANMODESEQUENCE,
              configurationParameter.getValue());
          break;
        default:
          log.warn("{}: Unhandled configuration property {}", fanControlCluster.getZigBeeAddress(),
              configurationParameter.getKey());
          break;
      }

      if (response != null) {
        currentConfiguration.put(configurationParameter.getKey(), BigInteger.valueOf(response));
        updated = true;
      }
    }

    return updated;
  }*/

 /* private Integer configureAttribute(int attributeId, Object value) {
    ZclAttribute attribute = fanControlCluster.getAttribute(attributeId);
    attribute.writeValue(((BigDecimal) (value)).intValue());
    return (Integer) attribute.readValue(0);
  }*/
}
