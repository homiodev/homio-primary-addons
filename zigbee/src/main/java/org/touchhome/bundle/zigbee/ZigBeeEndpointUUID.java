package org.touchhome.bundle.zigbee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ZigBeeEndpointUUID {

  private final String ieeeAddress;
  private final Integer clusterId;
  private final Integer endpointId;
  @JsonIgnore
  private final String clusterName;

  @JsonIgnore
  private boolean leftSide;

  public ZigBeeEndpointUUID(@NotNull String ieeeAddress, @NotNull Integer clusterId, Integer endpointId, String clusterName) {
    this.ieeeAddress = ieeeAddress;
    this.clusterId = clusterId;
    this.endpointId = endpointId;
    this.clusterName = clusterName;
  }

  public static ZigBeeEndpointUUID require(String ieeeAddress, int clusterId, Integer endpoint, String clusterName) {
    ZigBeeEndpointUUID uuid = new ZigBeeEndpointUUID(ieeeAddress, clusterId, endpoint, clusterName);
    uuid.leftSide = true;
    return uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZigBeeEndpointUUID that = (ZigBeeEndpointUUID) o;
    if (!Objects.equals(ieeeAddress, that.ieeeAddress) || !Objects.equals(clusterId, that.clusterId)) {
      return false;
    }

    ZigBeeEndpointUUID left = this.leftSide ? this : that;
    ZigBeeEndpointUUID right = this.leftSide ? that : that;

    if (left.endpointId != null && !left.endpointId.equals(right.endpointId)) {
      return false;
    }
    return left.clusterName == null || left.clusterName.equals(right.clusterName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ieeeAddress, clusterId);
  }

  @Override
  public String toString() {
    return "UUID{ieeeAddress='" + ieeeAddress + "', clusterId=" + clusterId + ", endpointId=" + endpointId + ", clusterName=" + clusterName + "}";
  }

  public String asKey() {
    return ieeeAddress + "_" + clusterId + "_" + endpointId;
  }
}
