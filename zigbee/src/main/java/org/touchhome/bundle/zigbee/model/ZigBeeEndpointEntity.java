package org.touchhome.bundle.zigbee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.converter.JSONObjectConverter;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.entity.HasJsonData;
import org.touchhome.bundle.api.entity.HasStatusAndMsg;
import org.touchhome.bundle.api.exception.ProhibitedExecution;
import org.touchhome.bundle.api.service.EntityService;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldGroup;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.api.ui.field.UIFieldNumber;
import org.touchhome.bundle.zigbee.ZigBeeEndpointUUID;
import org.touchhome.bundle.zigbee.model.service.ZigbeeEndpointService;
import org.touchhome.common.util.Lang;

@Entity
@Setter
@Getter
@Accessors(chain = true)
public class ZigBeeEndpointEntity extends BaseEntity<ZigBeeEndpointEntity>
    implements HasJsonData, HasStatusAndMsg<ZigBeeEndpointEntity>, EntityService<ZigbeeEndpointService, ZigBeeEndpointEntity> {

  public static final String PREFIX = "zbe_";

  @ManyToOne(fetch = FetchType.LAZY)
  private ZigBeeDeviceEntity zigBeeDeviceEntity;

  @Lob
  @Getter
  @Column(length = 1000)
  @Convert(converter = JSONObjectConverter.class)
  private JSONObject jsonData = new JSONObject();

  @UIField(order = 1, readOnly = true)
  @UIFieldGroup(value = "General", order = 1, borderColor = "#317175")
  private String ieeeAddress;

  @UIField(order = 2, readOnly = true)
  @UIFieldGroup("General")
  private int clusterId;

  @UIField(order = 3, readOnly = true)
  @UIFieldGroup("General")
  private int endpointId;
  // The minimum time period in seconds between device state updates
  @UIField(onlyEdit = true, order = 100)
  @UIFieldNumber(min = 1, max = 86400)
  @UIFieldGroup(value = "Reporting", order = 2, borderColor = "#517531")
  private int reportingTimeMin = 1;
  // The maximum time period in seconds between device state updates
  @UIField(onlyEdit = true, order = 101)
  @UIFieldNumber(min = 1, max = 86400)
  @UIFieldGroup("Reporting")
  private int reportingTimeMax = 900;
  @UIField(onlyEdit = true, order = 102)
  @UIFieldNumber(min = 1, max = 86400)
  @UIFieldGroup("Reporting")
  private int reportingChange = 10;
  // The time period in seconds between subsequent polls
  @UIField(onlyEdit = true, order = 103)
  @UIFieldNumber(min = 15, max = 86400)
  @UIFieldGroup("Reporting")
  private int poolingPeriod = 900;

  @Override
  @UIField(order = 4, readOnly = true)
  @UIFieldGroup("General")
  public String getName() {
    return super.getName();
  }

  @Override
  public ZigBeeEndpointEntity setName(String name) {
    return super.setName(name);
  }

  @Override
  public String getDefaultName() {
    return "Zigbee endpoint";
  }

  @UIField(order = 5, readOnly = true)
  @UIFieldGroup("General")
  public String getDescription() {
    return Lang.getServerMessage("zigbee_description." + getName());
  }

  @Override
  public void getAllRelatedEntities(Set<BaseEntity> set) {
    set.add(zigBeeDeviceEntity);
  }

  @Override
  public String getEntityPrefix() {
    return PREFIX;
  }

  @Override
  @UIFieldIgnore
  @JsonIgnore
  public Date getCreationTime() {
    return super.getCreationTime();
  }

  @Override
  @UIFieldIgnore
  @JsonIgnore
  public Date getUpdateTime() {
    return super.getUpdateTime();
  }

  @Override
  public Class<ZigbeeEndpointService> getEntityServiceItemClass() {
    return ZigbeeEndpointService.class;
  }

  @Override
  public ZigbeeEndpointService createService(EntityContext entityContext) {
    throw new ProhibitedExecution();
  }

  @Override
  public String toString() {
    return "ZigBeeDeviceEndpoint{" +
        "ieeeAddress='" + ieeeAddress + '\'' +
        ", clusterId=" + clusterId +
        ", endpointId=" + endpointId +
        ", clusterName='" + getName() + '\'' +
        '}';
  }

  @JsonIgnore
  public ZigBeeEndpointUUID getEndpointUUID() {
    return new ZigBeeEndpointUUID(ieeeAddress, clusterId, endpointId, getName());
  }
}
