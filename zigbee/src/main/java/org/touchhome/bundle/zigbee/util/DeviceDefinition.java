package org.touchhome.bundle.zigbee.util;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.touchhome.common.util.Lang;

@Getter
@Setter
public class DeviceDefinition {

  private @Nullable String id;
  private @Nullable String category;
  private @NotNull Set<String> modelId;
  private @NotNull String vendor;
  private @Nullable String image;

  private @Nullable Map<String, String> label;
  private @Nullable Map<String, String> description;

  private List<EndpointDefinition> endpoints = new ArrayList<>();

  public String getLabel() {
    if (label == null) {
      return id;
    }
    return label.getOrDefault(Lang.CURRENT_LANG, label.getOrDefault("en", id));
  }

  public String getDescription() {
    if (description == null) {
      return id;
    }
    return description.getOrDefault(Lang.CURRENT_LANG, label.getOrDefault("en", id));
  }

  @Getter
  @Setter
  @ToString
  public static class EndpointDefinition {

    private @Nullable String id;
    private int endpoint;
    private @NotNull List<Integer> inputClusters;
    private @NotNull List<Integer> outputClusters;
    private @NotNull String typeId;

    private Map<String, String> label;
    private Map<String, String> description;

    private JsonNode metadata;

    public String getId() {
      return id == null ? typeId : id;
    }
  }
}
