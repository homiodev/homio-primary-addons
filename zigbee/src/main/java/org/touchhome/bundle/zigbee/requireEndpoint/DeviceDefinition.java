package org.touchhome.bundle.zigbee.requireEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class DeviceDefinition {

  private @Nullable String id;
  private @Nullable String category;
  private @Nullable String modelId;
  private @NotNull String vendor;
  private @Nullable String image;

  private Map<String, String> label;
  private Map<String, String> description;

  private List<EndpointDefinition> endpoints = new ArrayList<>();

  public String getLabel(@Nullable String lang) {
    if (label == null) {
      return id;
    }
    if (lang == null) {
      return label.getOrDefault("en", id);
    }
    return label.getOrDefault(lang, label.getOrDefault("en", id));
  }

  public String getDescription(@Nullable String lang) {
    if (description == null) {
      return id;
    }
    if (lang == null) {
      return description.getOrDefault("en", id);
    }
    return description.getOrDefault(lang, label.getOrDefault("en", id));
  }

  @Getter
  @Setter
  @ToString
  public static class EndpointDefinition {

    private @Nullable String id;
    private int endpoint;
    private Integer inputCluster;
    private @NotNull String typeId;

    private Map<String, String> label;
    private Map<String, String> description;

    private Map<String, Object> metadata;

    public String getId() {
      return id == null ? typeId : id;
    }
  }
}
