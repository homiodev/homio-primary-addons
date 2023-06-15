package org.homio.addon.z2m.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Z2MConfiguration extends UnknownOptions {

    @JsonProperty("permit_join")
    private boolean permitJoin;

    private Mqtt mqtt = new Mqtt();
    private Serial serial = new Serial();
    private Frontend frontend = new Frontend();
    private Map<String, ObjectNode> devices = new HashMap<>();
    private Advanced advanced = new Advanced();

    @Getter
    @Setter
    public static class Advanced extends UnknownOptions {

        private String last_seen = "epoch";
    }

    @Getter
    @Setter
    public static class Frontend extends UnknownOptions {

        private String host = "0.0.0.0";
        private int port = 8187;
    }

    @Getter
    @Setter
    public static class Mqtt extends UnknownOptions {

        @JsonProperty("base_topic")
        @Nullable
        private String baseTopic;

        @Nullable private String server;
        @Nullable private String user;
        @Nullable private String password;
    }

    @Getter
    @Setter
    public static class Serial extends UnknownOptions {

        @Nullable private String port;

        @JsonProperty("disable_led")
        private boolean disableLed = false;
    }
}
