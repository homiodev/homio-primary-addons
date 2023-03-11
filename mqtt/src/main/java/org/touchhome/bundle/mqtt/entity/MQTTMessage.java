package org.touchhome.bundle.mqtt.entity;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.touchhome.bundle.api.storage.DataStorageEntity;
import org.touchhome.bundle.api.util.TouchHomeUtils;

@Getter
@Setter
@NoArgsConstructor
public class MQTTMessage extends DataStorageEntity {

    private String topic;

    private Object value;

    private boolean retained;

    private boolean duplicated;

    private int qos;

    public MQTTMessage(String topic, Object value, boolean retained, boolean duplicated, int qos) {
        this.value = value;
        this.topic = topic;
        this.retained = retained;
        this.duplicated = duplicated;
        this.qos = qos;
    }

    @Override
    public String toString() {
        return "{payload=" + getValue() + ", time=" + TouchHomeUtils.DATE_TIME_FORMAT.format(new Date(getId())) + ", QoS=" +
            qos + ", ret=" + retained + ", dup=" + duplicated + "}";
    }
}
