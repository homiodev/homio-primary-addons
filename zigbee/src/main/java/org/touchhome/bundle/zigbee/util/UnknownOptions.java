package org.touchhome.bundle.zigbee.util;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;

public class UnknownOptions {

  @JsonIgnore
  private final Map<String, Object> unknownFields = new HashMap<>();

  @SuppressWarnings("unused")
  @JsonAnyGetter
  public Map<String, Object> otherFields() {
    return unknownFields;
  }

  @JsonAnySetter
  public void setOtherField(String name, Object value) {
    unknownFields.put(name, value);
  }
}
