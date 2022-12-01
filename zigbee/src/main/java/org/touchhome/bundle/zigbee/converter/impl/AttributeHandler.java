package org.touchhome.bundle.zigbee.converter.impl;

import static org.touchhome.bundle.zigbee.ZigBeeConstants.POLLING_PERIOD_DEFAULT;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.converter.impl.cluster.ZigBeeGeneralApplication;
import org.touchhome.bundle.zigbee.util.ClusterAttributeConfiguration;

@Log4j2
@Getter
public abstract class AttributeHandler {

  protected int pollingPeriod = POLLING_PERIOD_DEFAULT;
  protected int minimalReportingPeriod = Integer.MAX_VALUE;
  private long lastPoll;

  private State value;
  private long updated;
  @Setter
  private String variableID;

  protected ZclAttribute zclAttribute;
  protected ClusterAttributeConfiguration attributeConfiguration;
  protected ZclCluster zclCluster;
  @Setter
  protected Status status = Status.UNKNOWN;
  protected ZigBeeGeneralApplication application;

  private String entityID;

  public void postConstruct(ZclAttribute zclAttribute, ClusterAttributeConfiguration attributeConfiguration, ZigBeeGeneralApplication application) {
    this.application = application;
    this.entityID = application.getEntityID() + "_" + zclAttribute.getId();
    this.zclCluster = application.getZclCluster();
    this.zclAttribute = zclAttribute;
    this.attributeConfiguration = attributeConfiguration;
  }

  protected Optional<AttributeHandler> getAttributeHandler(int attributeID) {
    return application.getAttributeHandler(attributeID);
  }

  public void setValue(State value) {
    log.debug("[{}]: Update attribute {} value {}. {}", application.getDeviceEntityID(), zclAttribute.getName(), value, entityID);
    this.value = value;
    this.status = Status.ONLINE;
    this.lastPoll = System.currentTimeMillis();
    this.updated = System.currentTimeMillis();
  }

  public <T> T readAttribute(ZclCluster zclCluster, int attributeID, T defaultValue) {
    ZclAttribute divisorAttribute = zclCluster.getAttribute(attributeID);
    Object value = divisorAttribute.readValue(Long.MAX_VALUE);
    return value == null ? defaultValue : (T) value;
  }

  public void initialize(boolean bindSuccess) throws Exception {
    if (bindSuccess) {
      handleReporting(zclAttribute, attributeConfiguration);
    } else {
      Integer value = attributeConfiguration.getBindFailedPollingPeriod();
      if (value != null) {
        pollingPeriod = value;
      }
    }
  }

  public abstract @Nullable State convertValue(Object value);

  protected void handleReporting(ZclAttribute attribute, ClusterAttributeConfiguration configuration)
      throws Exception {
    CommandResult reportingResponse = attribute.setReporting(configuration.getMinInterval(), configuration.getMaxInterval(),
        configuration.getReportableChange()).get();

    handleReportingResponse(reportingResponse, configuration.getReportingFailedPollingInterval(),
        configuration.getReportingSuccessMaxReportInterval());
  }

  protected void handleReportingResponse(CommandResult reportResponse, int reportingFailedPollingInterval,
      int reportingSuccessMaxReportInterval) {
    if (!reportResponse.isSuccess()) {
      pollingPeriod = Math.min(pollingPeriod, reportingFailedPollingInterval);
    } else {
      minimalReportingPeriod = Math.min(minimalReportingPeriod, reportingSuccessMaxReportInterval);
    }
  }

  @SneakyThrows
  public void handleRefresh(boolean force) {
    if (force || (System.currentTimeMillis() - lastPoll) / 1000 > pollingPeriod) {
      lastPoll = System.currentTimeMillis();
      zclAttribute.readValue(0);
      Thread.sleep(100); // sleep between send commands
    }
  }
}
