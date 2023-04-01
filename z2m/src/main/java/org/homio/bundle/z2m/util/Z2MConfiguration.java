package org.homio.bundle.z2m.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private boolean availability = true;
    private Map<String, ObjectNode> devices;

    @Getter
    @Setter
    public static class Frontend extends UnknownOptions {

        private String host = "0.0.0.0";
        private int port = 8187;
        // private String url = "https://zigbee2mqtt.myhouse.org";
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
