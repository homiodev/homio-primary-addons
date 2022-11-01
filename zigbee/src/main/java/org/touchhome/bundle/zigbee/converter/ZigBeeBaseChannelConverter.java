package org.touchhome.bundle.zigbee.converter;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeCommand;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.ZigBeeProfileType;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.ZigBeeDevice;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;
import org.touchhome.bundle.zigbee.model.service.ZigbeeEndpointService;
import tec.uom.se.unit.Units;

@Log4j2
public abstract class ZigBeeBaseChannelConverter {

  /**
   * Default polling period (in seconds).
   */
  public static final int POLLING_PERIOD_DEFAULT = 7200;

  public static final int REPORTING_PERIOD_DEFAULT_MAX = 7200;

  public static final int POLLING_PERIOD_HIGH = 300;

  @Getter
  protected int pollingPeriod = POLLING_PERIOD_DEFAULT;

  @Getter
  protected int minimalReportingPeriod = Integer.MAX_VALUE;

  private boolean pooling = false;

  @Getter
  @Setter
  private ZigBeeConverter annotation;

  @Setter
  @Getter
  private ZigbeeEndpointService endpointService;

  protected ZigBeeEndpointEntity getEndpointEntity() {
    return endpointService.getZigBeeEndpointEntity();
  }

  protected ZigBeeEndpoint getEndpoint() {
    return endpointService.getZigBeeEndpoint();
  }

  protected <T> T getInputCluster(int clusterId) {
    return (T) endpointService.getZigBeeEndpoint().getInputCluster(clusterId);
  }

  protected <T> T getOutputCluster(int clusterId) {
    return (T) endpointService.getZigBeeEndpoint().getOutputCluster(clusterId);
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
  public boolean initializeDevice() {
    return true;
  }

  /**
   * Initialise the converter. This is called by the {@link ZigBeeDevice} when the channel is created. The converter should initialise any internal states, open any clusters, add
   * reporting and binding that it needs to operate.
   * <p>
   *
   * @return true if the converter was initialised successfully
   */
  public abstract boolean initializeConverter();

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

  public void fireHandleRefresh() {
    this.pooling = true;
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
   * @param endpoint The {@link ZigBeeEndpoint} to search for zigbeeRequireEndpoints
   * @return a  if the converter supports features from the {@link ZigBeeEndpoint}, otherwise null.
   */
  public abstract boolean acceptEndpoint(ZigBeeEndpoint endpoint);

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
    log.debug("{}: Channel <{}> updated to <{}>", endpointService.getZigBeeEndpoint(), getClass().getSimpleName(), state);
    endpointService.updateValue(state);
    this.pooling = false;
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
    return Objects.equals(endpointService.getZigBeeEndpointEntity().getEndpointUUID(),
        that.endpointService.getZigBeeEndpointEntity().getEndpointUUID());
  }

  @Override
  public int hashCode() {
    return endpointService.getZigBeeEndpointEntity().getEndpointUUID().hashCode();
  }

  // Configure reporting
  protected void updateServerPoolingPeriod(ZclCluster serverCluster, int attributeId, boolean isUpdate) {
    ZigBeeEndpointEntity zbe = endpointService.getZigBeeEndpointEntity();
    try {
      CommandResult reportingResponse = serverCluster.setReporting(attributeId, zbe.getReportingTimeMin(),
          zbe.getReportingTimeMax(), zbe.getReportingChange()).get();
      if (isUpdate) {
        handleReportingResponse(reportingResponse, zbe.getPoolingPeriod(), zbe.getReportingTimeMax());
      } else {
        handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, zbe.getPoolingPeriod());
      }
    } catch (InterruptedException | ExecutionException e) {
      log.debug("{}: Exception setting reporting", zbe, e);
    }
  }

  public void updateConfiguration() {

  }

  public Integer getMinPoolingInterval() {
    return Math.min(this.pollingPeriod, this.minimalReportingPeriod);
  }
}
