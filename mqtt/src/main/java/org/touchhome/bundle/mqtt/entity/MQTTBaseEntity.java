package org.touchhome.bundle.mqtt.entity;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import javax.persistence.Entity;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.TreeConfiguration;
import org.touchhome.bundle.api.entity.storage.BaseFileSystemEntity;
import org.touchhome.bundle.api.entity.types.StorageEntity;
import org.touchhome.bundle.api.entity.widget.AggregationType;
import org.touchhome.bundle.api.entity.widget.PeriodRequest;
import org.touchhome.bundle.api.entity.widget.ability.HasAggregateValueFromSeries;
import org.touchhome.bundle.api.entity.widget.ability.HasGetStatusValue;
import org.touchhome.bundle.api.entity.widget.ability.HasSetStatusValue;
import org.touchhome.bundle.api.entity.widget.ability.HasTimeValueSeries;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.service.EntityService;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldGroup;
import org.touchhome.bundle.api.ui.field.UIFieldPort;
import org.touchhome.bundle.api.ui.field.UIFieldProgress;
import org.touchhome.bundle.api.ui.field.UIFieldSlider;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.api.ui.field.selection.dynamic.DynamicParameterFields;
import org.touchhome.bundle.api.ui.field.selection.dynamic.DynamicRequestType;
import org.touchhome.bundle.api.ui.field.selection.dynamic.SelectionWithDynamicParameterFields;
import org.touchhome.bundle.api.util.SecureString;
import org.touchhome.bundle.mqtt.entity.parameters.MQTTPublishQueryParameter;
import org.touchhome.bundle.mqtt.entity.parameters.MQTTTopicQueryParameter;

@Log4j2
@Entity
public abstract class MQTTBaseEntity extends StorageEntity<MQTTBaseEntity>
    implements EntityService<MQTTService, MQTTBaseEntity>, HasTimeValueSeries,
    SelectionWithDynamicParameterFields, HasAggregateValueFromSeries,
    HasGetStatusValue, HasSetStatusValue,
    BaseFileSystemEntity<MQTTBaseEntity, MQTTFileSystem>,
    UIFieldSelection.SelectionConfiguration {

    static String normalize(String topic) {
        if (topic.startsWith("/")) {
            topic = topic.substring(1);
        }
        return topic;
    }

    @Override
    public String selectionIcon() {
        return "fas fa-mattress-pillow";
    }

    @Override
    public String getFileSystemAlias() {
        return "MQTT";
    }

    @Override
    public boolean isShowInFileManager() {
        return false;
    }

    @Override
    public TreeConfiguration buildFileSystemConfiguration(EntityContext entityContext) {
        return getService().getValue().iterator().next();
    }

    @Override
    public String getFileSystemIcon() {
        return selectionIcon();
    }

    @Override
    public String getFileSystemIconColor() {
        return selectionIconColor();
    }

    @Override
    public boolean requireConfigure() {
        return false;
    }

    @Override
    public MQTTFileSystem buildFileSystem(EntityContext entityContext) {
        return new MQTTFileSystem(this);
    }

    @Override
    public long getConnectionHashCode() {
        return 0;
    }

    @UIField(order = 1)
    @UIFieldGroup(order = 1, value = "Server", borderColor = "#0EBC97")
    public String getHostname() {
        return getJsonData("host", "127.0.0.1");
    }

    public void setHostname(String value) {
        setJsonData("host", value);
    }

    @UIField(order = 2)
    @UIFieldPort
    @UIFieldGroup("Server")
    public int getMqttPort() {
        return getJsonData("port", 1883);
    }

    public void setMqttPort(String value) {
        setJsonData("port", value);
    }

    @UIField(order = 1)
    @UIFieldGroup(order = 2, value = "Security", borderColor = "#23ADAB")
    public String getMqttUser() {
        return getJsonData("user", "");
    }

    public void setMqttUser(String value) {
        setJsonData("user", value);
    }

    @UIField(order = 2)
    @UIFieldGroup("Security")
    public SecureString getMqttPassword() {
        return getJsonSecure("pwd");
    }

    public void setMqttPassword(String value) {
        setJsonData("pwd", value);
    }

    @UIField(order = 1)
    @UIFieldSlider(min = 100, max = 100000, step = 100)
    @UIFieldGroup(order = 10, value = "History")
    public int getHistorySize() {
        return getJsonData("hs", 1000);
    }

    public void setHistorySize(int value) {
        setJsonData("hs", value);
    }

    @UIField(order = 2, hideInEdit = true)
    @UIFieldProgress
    @UIFieldGroup("History")
    public UIFieldProgress.Progress getUsedHistorySize() {
        long storageCount = optService().map(service -> service.getStorage().count()).orElse(0L);
        return getEntityID() == null ? null : new UIFieldProgress.Progress((int) storageCount, getHistorySize());
    }

    @UIField(order = 3, inlineEdit = true)
    @UIFieldGroup("History")
    public boolean getIncludeSys() {
        return getJsonData("sys", false);
    }

    public void setIncludeSys(String value) {
        setJsonData("sys", value);
    }

    @UIField(order = 80)
    @UIFieldGroup(order = 5, value = "Connection", borderColor = "#479923")
    public boolean getMqttCleanSessionOnConnect() {
        return getJsonData("cs", true);
    }

    public void setMqttCleanSessionOnConnect(String value) {
        setJsonData("cs", value);
    }

    @UIField(order = 90)
    @UIFieldGroup("Connection")
    public String getMqttClientID() {
        return getJsonData("cid");
    }

    public void setMqttClientID(String value) {
        setJsonData("cid", value);
    }

    @UIField(order = 100)
    @UIFieldSlider(min = 10, max = 600)
    public int getConnectionTimeout() {
        return getJsonData("ct", 30);
    }

    public void setConnectionTimeout(String value) {
        setJsonData("ct", value);
    }

    @UIField(order = 110)
    @UIFieldSlider(min = 10, max = 600)
    @UIFieldGroup("Connection")
    public int getMqttKeepAlive() {
        return getJsonData("ka", 60);
    }

    public void setMqttKeepAlive(String value) {
        setJsonData("ka", value);
    }

    @SneakyThrows
    @UIContextMenuAction("TEST_CONNECTION")
    public ActionResponseModel testConnection() {
        getService().testServiceWithSetStatus();
        return ActionResponseModel.success();
    }

    @UIContextMenuAction("mqtt.clear_history")
    public ActionResponseModel clearHistory() {
        getService().clearHistory();
        return ActionResponseModel.success();
    }

    @Override
    public Class<MQTTService> getEntityServiceItemClass() {
        return MQTTService.class;
    }

    @Override
    @SneakyThrows
    public MQTTService createService(EntityContext entityContext) {
        return new MQTTService(this, entityContext);
    }

    @Override
    protected void beforePersist() {
        super.beforePersist();
        this.setMqttClientID(UUID.randomUUID().toString());
    }

    public void publish(String topic, String content, int qoS, boolean retain) {
        if (StringUtils.isNotEmpty(content)) {
            try {
                log.debug("[{}]: MQTT Name[{}]. Publish message: Topic[{}], Qos[{}], Retain[{}], Value[{}]", getEntityID(), getTitle(), topic, qoS,
                    retain, content);
                getService().getMqttClient().publish(topic, content.getBytes(StandardCharsets.UTF_8), qoS, retain);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public @NotNull List<Object[]> getTimeValueSeries(PeriodRequest request) {
        return getService().getStorage().getTimeSeries(request.getFromTime(), request.getToTime(), "topic",
            getTopicRequire(request.getParameters(), "queryTopic"));
    }

    @Override
    public DynamicParameterFields getDynamicParameterFields(RequestDynamicParameter request) {
        if (request.getDynamicRequestType() == DynamicRequestType.SetValue) {
            return new MQTTPublishQueryParameter().setPublishTopic("example/test");
        }
        return new MQTTTopicQueryParameter().setQueryTopic("example/test");
    }

    @Override
    public Object getAggregateValueFromSeries(@NotNull PeriodRequest request, @NotNull AggregationType aggregationType, boolean filterOnlyNumbers) {
        String topic = getTopicRequire(request.getParameters(), "queryTopic");
        return getService().getStorage().aggregate(request.getFromTime(), request.getToTime(), "topic", topic,
            aggregationType, filterOnlyNumbers);
    }

    @Override
    public Object getStatusValue(GetStatusValueRequest request) {
        String topic = getTopicRequire(request.getDynamicParameters(), "queryTopic");
        return getService().getRawValue(topic);
    }

    @Override
    public void setStatusValue(SetStatusValueRequest request) {
        publishFromDynamicTopic(String.valueOf(request.getValue()), request.getDynamicParameters());
    }

    @Override
    public void addUpdateValueListener(EntityContext entityContext, String key, JSONObject dynamicParameters,
        Consumer<Object> listener) {
        entityContext.event().addEventListener(getEntityID() + "_" +
            getTopicRequire(dynamicParameters, "queryTopic"), key, listener);
    }

    private String getTopicRequire(JSONObject dynamicParameters, String fieldName) {
        String topic = dynamicParameters.optString(fieldName);
        if (StringUtils.isEmpty(topic)) {
            throw new IllegalStateException("Unable to find topic from request");
        }
        return normalize(topic);
    }

    private void publishFromDynamicTopic(String value, JSONObject dynamicParameters) {
        String topic = getTopicRequire(dynamicParameters, "publishTopic");
        publish(topic, value, dynamicParameters.optInt("qos", 0),
            dynamicParameters.optBoolean("retain", false));
    }

    @Override
    public String getTimeValueSeriesDescription() {
        return "mqtt.time_series";
    }

    @Override
    public String getAggregateValueDescription() {
        return "mqtt.series_aggregate";
    }

    @Override
    public String getGetStatusDescription() {
        return "mqtt.get_value";
    }

    @Override
    public String getSetStatusDescription() {
        return "mqtt.set_topic";
    }

    public boolean deepEqual(@NotNull MQTTBaseEntity mqttEntity) {
        return Objects.equals(this.getEntityID(), mqttEntity.getEntityID()) &&
            Objects.equals(this.getMqttUser(), mqttEntity.getMqttUser()) &&
            Objects.equals(this.getMqttPassword().asString(), mqttEntity.getMqttPassword().asString()) &&
            Objects.equals(this.getMqttClientID(), mqttEntity.getMqttClientID()) &&
            this.getMqttPort() == mqttEntity.getMqttPort();
    }
}
