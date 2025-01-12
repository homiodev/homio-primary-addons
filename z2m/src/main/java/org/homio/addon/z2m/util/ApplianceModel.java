package org.homio.addon.z2m.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * Do not change file names. It read from mqtt
 */
@Getter
@Setter
public class ApplianceModel extends UnknownOptions {

  public static final String COMPOSITE_TYPE = "composite";
  public static final String NUMBER_TYPE = "numeric";
  public static final String BINARY_TYPE = "binary";
  public static final String SWITCH_TYPE = "switch";
  public static final String ENUM_TYPE = "enum";
  public static final String LIST_TYPE = "list";
  public static final String UNKNOWN_TYPE = "unknown";

  @JsonProperty("definition")
  private Z2MDeviceDefinition definition = new Z2MDeviceDefinition();

  @JsonProperty("endpoints")
  private Map<String, Z2MDeviceEndpoint> endpoints;

  @JsonProperty("friendly_name")
  private String friendlyName;

  @JsonProperty("ieee_address")
  private String ieeeAddress;

  @JsonProperty("interview_completed")
  private boolean interviewCompleted;

  @JsonProperty("interviewing")
  private boolean interviewing;

  @JsonProperty("manufacturer")
  private String manufacturer;

  @JsonProperty("model_id")
  private String modelId;

  @JsonProperty("network_address")
  private int networkAddress;

  @JsonProperty("power_source")
  private String powerSource;

  @JsonProperty("supported")
  private boolean supported;

  @JsonProperty("type")
  private String type;

  @JsonProperty("date_code")
  private String firmwareBuildDate;

  @JsonProperty("disabled")
  private boolean disabled;

  @JsonProperty("software_build_id")
  private String firmwareVersion;

  public String getName() {
    return friendlyName;
  }

  @Override
  public String toString() {
    return "Z2MDevice{ieeeAddress='%s', manufacturer='%s', model='%s'}".formatted(ieeeAddress, manufacturer, definition.model);
  }

  public String getGroupDescription() {
    String name = getName();
    if (!name.equals(ieeeAddress)) {
      return "${%s} [%s]".formatted(name, ieeeAddress);
    }
    return name;
  }

  public String getMQTTTopic() {
    return defaultIfEmpty(friendlyName, ieeeAddress);
  }

  public boolean isInterviewFailed() {
    return !interviewCompleted && !interviewing;
  }

  public Set<String> getExposes() {
    return definition.getExposes().stream().map(Options::getName).collect(Collectors.toSet());
  }

  @Getter
  @Setter
  public static class Z2MDeviceDefinition extends UnknownOptions {

    @JsonProperty("description")
    private String description;

    @JsonProperty("exposes")
    private List<Options> exposes = Collections.emptyList();

    @JsonProperty("supportsOta")
    private boolean supportsOta;

    @JsonProperty("vendor")
    private String vendor;

    @JsonProperty("model")
    private String model;

    @JsonProperty("options")
    private List<Options> options = Collections.emptyList();

    @Getter
    @Setter
    public static class Options extends UnknownOptions {

      @JsonProperty("access")
      private @Nullable Integer access;

      @JsonProperty("description")
      private String description;

      @JsonProperty("name")
      private String name;

      @JsonProperty("property")
      private @Nullable String property;

      @JsonProperty("type")
      private @NotNull String type;

      @JsonProperty("unit")
      private @Nullable String unit;

      @JsonProperty("endpoint")
      private @Nullable String endpoint;

      @JsonProperty("value_max")
      private @Nullable Integer valueMax;

      @JsonProperty("value_min")
      private @Nullable Integer valueMin;

      @JsonProperty("value_step")
      private @Nullable Integer valueStep;
      /**
       * For binary type
       */
      @JsonProperty("value_off")
      private @Nullable Object valueOff;

      @JsonProperty("value_on")
      private @Nullable Object valueOn;

      @JsonProperty("value_toggle")
      private @Nullable String valueToggle;

      @JsonProperty("features")
      private @Nullable List<Options> features;
      /**
       * For numeric type
       */
      @JsonProperty("presets")
      private @Nullable List<Presets> presets;
      /**
       * For enum type
       */
      @JsonProperty("values")
      private Set<String> values;
      /**
       * List type
       */
      @JsonProperty("item_type")
      private @Nullable Options itemType;

      public static Options dynamicEndpoint(String endpoint, String type) {
        Options options = new Options();
        options.setProperty(endpoint);
        options.setName(endpoint);
        options.setType(type);
        return options;
      }

      // for debug purposes
      @Override
      public String toString() {
        return name;
      }

      // Bit 1: The property can be found in the published state of this device.
      public boolean isPublished() {
        return access != null && (access & 1) == 1;
      }

      // Bit 2: The property can be set with a /set command
      public boolean isWritable() {
        return access != null && ((access >> 1) & 1) == 1;
      }

      // Bit 3: The property can be retrieved with a /get command (when this bit is true, bit
      // 1 will also be true)
      public boolean isReadable() {
        return access != null && ((access >> 2) & 1) == 1;
      }

      @Getter
      @Setter
      public static class Presets {

        private String name;
        private Object value;
        private String description;
      }
    }
  }

  @Getter
  @Setter
  public static class Z2MDeviceEndpoint extends UnknownOptions {

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
