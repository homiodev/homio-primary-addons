package org.touchhome.bundle.zigbee.converter;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeCommand;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.ZigBeeProfileType;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.converter.config.ReportingChangeModel;
import org.touchhome.bundle.zigbee.converter.config.ZclDoorLockConfig;
import org.touchhome.bundle.zigbee.converter.config.ZclFanControlConfig;
import org.touchhome.bundle.zigbee.converter.config.ZclLevelControlConfig;
import org.touchhome.bundle.zigbee.converter.config.ZclOnOffSwitchConfig;
import org.touchhome.bundle.zigbee.converter.config.ZclReportingConfig;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;
import org.touchhome.bundle.zigbee.model.service.ZigBeeDeviceService;
import org.touchhome.bundle.zigbee.model.service.ZigbeeEndpointService;
import org.touchhome.bundle.zigbee.setting.ZigBeeDiscoveryClusterTimeoutSetting;
import tec.uom.se.unit.Units;

public abstract class ZigBeeBaseChannelConverter {

  /**
   * Default polling period (in seconds).
   */
  public static final int POLLING_PERIOD_DEFAULT = 7200;
  public static final int REPORTING_PERIOD_DEFAULT_MAX = 7200;
  public static final int POLLING_PERIOD_HIGH = 300;
  protected final Logger log = LogManager.getLogger(getClass());
  protected int pollingPeriod = POLLING_PERIOD_DEFAULT;
  @Getter
  protected int minimalReportingPeriod = Integer.MAX_VALUE;
  protected ZigBeeEndpoint endpoint;
  // device entityID
  protected String entityID;
  @Getter
  @Nullable
  protected ZclReportingConfig configReporting;
  @Getter
  @Nullable
  protected ZclLevelControlConfig configLevelControl;
  @Getter
  @Nullable
  protected ZclOnOffSwitchConfig configOnOff;
  @Getter
  @Nullable
  protected ZclFanControlConfig configFanControl;
  @Getter
  @Nullable
  protected ZclDoorLockConfig configDoorLock;
  @Getter
  protected boolean supportConfigColorControl;
  @Getter
  @Setter
  private ZigBeeConverter annotation;
  @Getter
  private ZigbeeEndpointService endpointService;

  public int getPollingPeriod() {
    return configReporting == null ? pollingPeriod : configReporting.getPollingPeriod();
  }

  public void initialize(ZigbeeEndpointService endpointService, ZigBeeEndpoint endpoint) {
    this.endpointService = endpointService;
    this.endpoint = endpoint;
    this.entityID = endpointService.getZigBeeDeviceService().getEntityID();
  }

  public int getDiscoveryTimeout(EntityContext entityContext) {
    return entityContext.setting().getValue(ZigBeeDiscoveryClusterTimeoutSetting.class);
  }

  public ZigBeeEndpointEntity getEntity() {
    return endpointService.getEntity();
  }

  protected <T> T getInputCluster(int clusterId) {
    return (T) endpoint.getInputCluster(clusterId);
  }

  protected <T> T getOutputCluster(int clusterId) {
    return (T) endpoint.getOutputCluster(clusterId);
  }

  /**
   * Configures the device. This method should perform the one off device configuration such as performing the bind and reporting configuration.
   * <p>
   * The binding should initialize reporting using one of the {@link ZclCluster#setReporting} commands.
   * <p>
   * Note that this method should be self contained, and may not make any assumptions about the initialization of any internal fields of the converter other than those initialized
   * in the #initialize method.
   *
   * @return true if the device was configured correctly
   */
  public abstract void initializeDevice() throws Exception;

  /**
   * Initialize the converter. This is called by the {@link ZigBeeDeviceService} when the channel is created. The converter should initialize any internal states, open any
   * clusters, add reporting and binding that it needs to operate.
   * <p>
   */
  public abstract void initializeConverter() throws Exception;

  /**
   * Closes the converter and releases any resources.
   */
  public void disposeConverter() {
    // Overridable if the converter has cleanup to perform
  }

  public Future<CommandResult> handleCommand(final ZigBeeCommand command) {
    // Overridable if a channel can be commanded
    return null;
  }

  /**
   * Execute refresh method. This method is called every time a binding item is refreshed and the corresponding node should be sent a message.
   * <p>
   * This is run in a separate thread by the Thing Handler so the converter doesn't need to worry about returning quickly.
   */
  protected void handleRefresh() {
    // Overridable if a channel can be refreshed
  }

  public final void fireHandleRefresh() {
    this.handleRefresh();
  }

  /**
   * Check if this converter supports features from the {@link ZigBeeEndpoint} If the converter doesn't support any features, it returns null.
   * <p>
   * The converter should perform the following -:
   * <ul>
   * <li>Check if the device supports the cluster(s) required by the converter
   * <li>Check if the cluster supports the attributes or commands required by the converter
   * </ul>
   * Only if the device supports the features required by the channel should the channel be implemented.
   *
   * @param endpoint      The {@link ZigBeeEndpoint} to search for zigbeeRequireEndpoints
   * @param entityContext
   * @return a  if the converter supports features from the {@link ZigBeeEndpoint}, otherwise null.
   */
  public abstract boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext);

  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext, int clusterID, int attributeId, boolean discoverAttribute,
      boolean readAttribute) {
    ZclCluster cluster = endpoint.getInputCluster(clusterID);
    if (cluster == null) {
      log.trace("[{}]: Cluster '{}' not found {}", entityID, clusterID, endpoint);
      return false;
    }

    if (discoverAttribute) {
      try {
        if (!cluster.discoverAttributes(false).get(getDiscoveryTimeout(entityContext), TimeUnit.SECONDS) && !cluster
            .isAttributeSupported(attributeId)) {
          log.debug("[{}]: Error discover attribute {}. {}", entityID, attributeId, endpoint);
          return false;
        }
      } catch (Exception e) {
        log.debug("[{}]: Exception discovering attributes in {}", entityID, endpoint, e);
        return false;
      }
    }

    if (readAttribute) {
      ZclAttribute zclAttribute = cluster.getAttribute(attributeId);
      Object value = zclAttribute.readValue(Long.MAX_VALUE);
      if (value == null) {
        log.debug("[{}]: Exception reading attribute {} in cluster, {}", entityID, attributeId, endpoint);
        return false;
      }
    }
    return true;
  }

  protected void handleReportingResponse(CommandResult reportResponse) {
    handleReportingResponse(reportResponse, REPORTING_PERIOD_DEFAULT_MAX, REPORTING_PERIOD_DEFAULT_MAX);
  }

  protected void handleReportingResponseHigh(CommandResult reportResponse) {
    handleReportingResponse(reportResponse, POLLING_PERIOD_HIGH, REPORTING_PERIOD_DEFAULT_MAX);
  }

  /**
   * Sets the {@code pollingPeriod} and {@code maxReportingPeriod} depending on the success or failure of the given reporting response.
   *
   * @param reportResponse                    a {@link CommandResult} representing the response to a reporting request
   * @param reportingFailedPollingInterval    the polling interval to be used in case configuring reporting has failed
   * @param reportingSuccessMaxReportInterval the maximum reporting interval in case reporting is successfully configured
   */
  protected void handleReportingResponse(CommandResult reportResponse, int reportingFailedPollingInterval,
      int reportingSuccessMaxReportInterval) {
    if (!reportResponse.isSuccess()) {
      // we want the minimum of all pollingPeriods
      pollingPeriod = Math.min(pollingPeriod, reportingFailedPollingInterval);
    } else {
      // we want to know the minimum of all maximum reporting periods to be used as a timeout value
      minimalReportingPeriod = Math.min(minimalReportingPeriod, reportingSuccessMaxReportInterval);
    }
  }

  /**
   * Converts a ZigBee 8 bit level as used in Level Control cluster and others to a percentage
   *
   * @param level an integer between 0 and 254
   * @return the scaled {@link int}
   */
  protected DecimalType levelToPercent(int level) {
    return new DecimalType((int) (level * 100.0 / 254.0 + 0.5));
  }

  /**
   * Converts a {@link int} to an 8 bit level scaled between 0 and 254
   *
   * @param percent the {@link int} to convert
   * @return a scaled value between 0 and 254
   */
  protected int percentToLevel(DecimalType percent) {
    return (int) (percent.floatValue() * 254.0f / 100.0f + 0.5f);
  }

  /**
   * Converts an integer value into a {@link QuantityType}. The temperature as an integer is assumed to be multiplied by 100 as per the ZigBee standard format.
   *
   * @param value the integer value to convert
   * @return the {@link QuantityType}
   */
  protected QuantityType valueToTemperature(int value) {
    return new QuantityType<>(BigDecimal.valueOf(value, 2), Units.CELSIUS);
  }

  /**
   * Converts an 0-100 numeric value into a Percentage {@link QuantityType}.
   *
   * @param value the integer value to convert
   * @return the {@link QuantityType}
   */
  protected QuantityType valueToPercentDimensionless(Number value) {
    return new QuantityType<>(value, Units.PERCENT);
  }

  /**
   * Creates a binding from the remote cluster to the local {@link ZigBeeProfileType#ZIGBEE_HOME_AUTOMATION} endpoint
   *
   * @param cluster the remote {@link ZclCluster} to bind to
   * @return the future {@link CommandResult}
   */
  protected Future<CommandResult> bind(ZclCluster cluster) {
    return cluster.bind(endpointService.getLocalIpAddress(), endpointService.getLocalEndpointId());
  }

  protected void updateChannelState(State state) {
    log.debug("[{}]: Channel <{}> updated to <{}> for {}", entityID, getClass().getSimpleName(), state, endpoint);
    endpointService.updateValue(state);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZigBeeBaseChannelConverter that = (ZigBeeBaseChannelConverter) o;
    return Objects.equals(endpointService.getEntity().getEndpointUUID(),
        that.endpointService.getEntity().getEndpointUUID());
  }

  @Override
  public int hashCode() {
    return endpointService.getEntity().getEndpointUUID().hashCode();
  }

  // Configure reporting
  protected void updateDeviceReporting(@NotNull ZclCluster serverCluster, int attributeId, @NotNull ZclReportingConfig configReporting) {
    try {
      CommandResult reportingResponse = serverCluster.setReporting(
          attributeId,
          configReporting.getReportingTimeMin(),
          configReporting.getReportingTimeMax(),
          configReporting.getReportingChange()).get();
      handleReportingResponse(
          reportingResponse,
          configReporting.getPollingPeriod(),
          configReporting.getReportingTimeMax());
    } catch (Exception e) {
      log.debug("[{}]: Exception setting reporting", entityID, e);
    }
  }

  protected void updateServerPollingPeriodNoChange(ZclCluster serverCluster, int attributeId) {
    try {
      CommandResult reportingResponse = serverCluster.setReporting(
          attributeId,
          configReporting.getReportingTimeMin(),
          configReporting.getReportingTimeMax()).get();
      handleReportingResponse(
          reportingResponse,
          configReporting.getPollingPeriod(),
          configReporting.getReportingTimeMax());
    } catch (Exception e) {
      log.debug("[{}]: Exception setting reporting", entityID, e);
    }
  }

  public void updateConfiguration() {

  }

  public Integer getMinPoolingInterval() {
    return Math.min(this.pollingPeriod, this.minimalReportingPeriod);
  }

  // return value if analogue cluster
  public ReportingChangeModel getReportingChangeModel() {
    return null;
  }

  public <T> T readAttribute(ZclCluster zclCluster, int attributeID, T defaultValue) {
    ZclAttribute divisorAttribute = zclCluster.getAttribute(attributeID);
    Object value = divisorAttribute.readValue(Long.MAX_VALUE);
    return value == null ? defaultValue : (T) value;
  }
}
