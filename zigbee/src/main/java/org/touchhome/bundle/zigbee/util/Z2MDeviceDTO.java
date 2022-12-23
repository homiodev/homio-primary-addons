package org.touchhome.bundle.zigbee.util;

import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class Z2MDeviceDTO {

  private Z2MDeviceDefinition definition = new Z2MDeviceDefinition();
  private Map<String, Z2MDeviceEndpoint> endpoints;
  @JsonProperty("friendly_name")
  private String friendlyName;
  @JsonProperty("ieee_address")
  private String ieeeAddress;
  @JsonProperty("interview_completed")
  private boolean interviewCompleted;
  private boolean interviewing;
  private String manufacturer;
  @JsonProperty("model_id")
  private String modelId;
  @JsonProperty("network_address")
  private int networkAddress;
  @JsonProperty("power_source")
  private String powerSource;
  private boolean supported;
  private String type;

  public String getName() {
    if (modelId != null || definition.getModel() != null) {
      return "zigbee.device." + StringUtils.defaultIfEmpty(modelId, definition.getModel());
    }
    return StringUtils.defaultString(friendlyName, ieeeAddress);
  }

  @Override
  public String toString() {
    return "Z2MDevice{" +
        "ieeeAddress='" + ieeeAddress + '\'' +
        ", manufacturer='" + manufacturer + '\'' +
        ", modelId='" + modelId + '\'' +
        '}';
  }

  public String getGroupDescription() {
    String name = getName();
    if (!name.equals(ieeeAddress)) {
      return format("${%s} [%s]", name, ieeeAddress);
    }
    return name;
  }

  @Getter
  @Setter
  public static class Z2MDeviceDefinition {

    private String description;
    private List<Options> exposes = Collections.emptyList();
    @JsonProperty("supports_ota")
    private boolean supportsOta;
    private String vendor;
    private String model;
    private List<Options> options = Collections.emptyList();

    @Getter
    @Setter
    public static class Options {

      private int access;
      private String description;
      private String name;
      private String property;
      private String type;
      private String unit;
      @JsonProperty("value_max")
      private Integer valueMax;
      @JsonProperty("value_min")
      private Integer valueMin;
      @JsonProperty("value_off")
      private Boolean valueOff;
      @JsonProperty("value_on")
      private Boolean valueOn;
      @JsonProperty("item_type")
      private Options itemType;
    }
  }

  @Getter
  @Setter
  public static class Z2MDeviceEndpoint {

    private List<Object> bindings;
    private List<Object> scenes;
    @JsonProperty("configured_reportings")
    private List<Object> configuredReportings;

    private Clusters clusters;

    @Getter
    @Setter
    public static class Clusters {

      private List<String> input;
      private List<String> output;
    }
  }
}
