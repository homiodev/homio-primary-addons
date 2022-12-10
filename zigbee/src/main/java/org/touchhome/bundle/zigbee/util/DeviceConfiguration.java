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
public class DeviceConfiguration {

  private @Nullable String id;
  private @Nullable String category;
  private @NotNull Set<String> models;
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
    if (description != null) {
      if (description.containsKey(Lang.CURRENT_LANG)) {
        return description.get(Lang.CURRENT_LANG);
      }
      if (description.containsKey("en")) {
        return description.get("en");
      }
    }
    return getLabel();
  }

  public JsonNode findMetadata(int endpointId) {
    return endpoints.stream()
                    .filter(e -> e.getEndpoint() == endpointId)
                    .findAny()
                    .map(EndpointDefinition::getMetadata)
                    .orElse(null);
  }

  @Getter
  @Setter
  @ToString
  public static class EndpointDefinition {

    private @Nullable String id;
    private int endpoint;
    private @NotNull Set<Integer> inputClusters;
    private @NotNull String typeId;

    private Map<String, String> label;
    private Map<String, String> description;

    private JsonNode metadata;

    public String getId() {
      return id == null ? typeId : id;
    }
  }
}
