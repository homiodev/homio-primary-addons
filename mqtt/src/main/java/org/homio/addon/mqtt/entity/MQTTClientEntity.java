package org.homio.addon.mqtt.entity;

import static java.nio.charset.StandardCharsets.UTF_8;

import jakarta.persistence.Entity;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.homio.addon.mqtt.MQTTEntrypoint;
import org.homio.addon.mqtt.entity.parameters.MQTTPublishQueryParameter;
import org.homio.addon.mqtt.entity.parameters.MQTTTopicQueryParameter;
import org.homio.addon.mqtt.workspace.Scratch3MQTTBlocks;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextService;
import org.homio.api.entity.HasFirmwareVersion;
import org.homio.api.entity.log.HasEntityLog;
import org.homio.api.entity.storage.BaseFileSystemEntity;
import org.homio.api.entity.types.StorageEntity;
import org.homio.api.entity.widget.PeriodRequest;
import org.homio.api.entity.widget.ability.HasGetStatusValue;
import org.homio.api.entity.widget.ability.HasSetStatusValue;
import org.homio.api.entity.widget.ability.HasTimeValueSeries;
import org.homio.api.fs.TreeConfiguration;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.service.EntityService;
import org.homio.api.storage.DataStorageService;
import org.homio.api.storage.SourceHistory;
import org.homio.api.storage.SourceHistoryItem;
import org.homio.api.ui.UISidebarChildren;
import org.homio.api.ui.field.UIField;
import org.homio.api.ui.field.UIFieldGroup;
import org.homio.api.ui.field.UIFieldPort;
import org.homio.api.ui.field.UIFieldProgress;
import org.homio.api.ui.field.UIFieldSlider;
import org.homio.api.ui.field.action.UIContextMenuAction;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.homio.api.ui.field.selection.dynamic.DynamicParameterFields;
import org.homio.api.ui.field.selection.dynamic.SelectionWithDynamicParameterFields;
import org.homio.api.ui.field.selection.dynamic.UIFieldDynamicSelection;
import org.homio.api.util.SecureString;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

@Log4j2
@Entity
@UISidebarChildren(icon = "fas fa-building", color = Scratch3MQTTBlocks.COLOR)
public class MQTTClientEntity extends StorageEntity implements
    EntityService<MQTTService, MQTTClientEntity>,
    BaseFileSystemEntity<MQTTClientEntity, MQTTFileSystem>,
    HasTimeValueSeries,
    SelectionWithDynamicParameterFields,
    HasGetStatusValue,
    HasSetStatusValue,
    EntityContextService.MQTTEntityService,
    UIFieldDynamicSelection.SelectionConfiguration,
    HasEntityLog,
    HasFirmwareVersion {

    static String normalize(String topic) {
        if (topic.startsWith("/")) {
            topic = topic.substring(1);
        }
        return topic;
    }

    @Override
    public @NotNull String getFileSystemAlias() {
        return "MQTT";
    }

    @Override
    public boolean isShowInFileManager() {
        return false;
    }

    @Override
    public @NotNull TreeConfiguration buildFileSystemConfiguration(@NotNull EntityContext entityContext) {
        return getService().getValue().iterator().next();
    }

    @Override
    public @NotNull Icon getFileSystemIcon() {
        return selectionIcon();
    }

    @Override
    public boolean requireConfigure() {
        return false;
    }

    @Override
    public @NotNull MQTTFileSystem buildFileSystem(@NotNull EntityContext entityContext) {
        return new MQTTFileSystem(this);
    }

    @Override
    public long getConnectionHashCode() {
        return 0;
    }

    @UIField(order = 1)
    @UIFieldGroup(order = 1, value = "SERVER", borderColor = "#0EBC97")
    public String getHostname() {
        return getJsonData("host", "127.0.0.1");
    }

    public void setHostname(String value) {
        setJsonData("host", value);
    }

    @UIField(order = 2, label = "mqttPort")
    @UIFieldPort
    @UIFieldGroup("SERVER")
    public int getPort() {
        return getJsonData("port", 1883);
    }

    public void setPort(String value) {
        setJsonData("port", value);
    }

    @UIField(order = 1, hideOnEmpty = true)
    @UIFieldGroup(order = 2, value = "SECURITY", borderColor = "#23ADAB")
    public String getUser() {
        return getJsonData("user", "");
    }

    public void setUser(String value) {
        setJsonData("user", value);
    }

    @UIField(order = 2, hideOnEmpty = true)
    @UIFieldGroup("SECURITY")
    public SecureString getPassword() {
        return getJsonSecure("pwd");
    }

    public void setPassword(String value) {
        setJsonDataSecure("pwd", value);
    }

    @UIField(order = 1)
    @UIFieldSlider(min = 100, max = 100000, step = 100)
    @UIFieldGroup(order = 10, value = "HISTORY")
    public int getHistorySize() {
        return getJsonData("hs", 1000);
    }

    public void setHistorySize(int value) {
        setJsonData("hs", value);
    }

    @UIField(order = 2, hideInEdit = true)
    @UIFieldProgress
    @UIFieldGroup("HISTORY")
    public UIFieldProgress.Progress getUsedHistorySize() {
        DataStorageService<MQTTMessage> storageService = optService().map(MQTTService::getStorage).orElse(null);
        long storageCount = storageService == null ? 0 : storageService.count();
        return UIFieldProgress.Progress.of((int) storageCount, getHistorySize());
    }

    @UIField(order = 3, inlineEdit = true)
    @UIFieldGroup("HISTORY")
    public boolean getIncludeSys() {
        return getJsonData("sys", false);
    }

    public void setIncludeSys(String value) {
        setJsonData("sys", value);
    }

    @UIField(order = 80, hideInView = true)
    @UIFieldGroup(order = 5, value = "CONNECTION", borderColor = "#479923")
    public boolean getMqttCleanSessionOnConnect() {
        return getJsonData("cs", true);
    }

    public void setMqttCleanSessionOnConnect(String value) {
        setJsonData("cs", value);
    }

    @UIField(order = 90, hideInView = true)
    @UIFieldGroup("CONNECTION")
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

    @UIField(order = 110, hideInView = true)
    @UIFieldSlider(min = 10, max = 600)
    @UIFieldGroup("CONNECTION")
    public int getMqttKeepAlive() {
        return getJsonData("ka", 60);
    }

    public void setMqttKeepAlive(String value) {
        setJsonData("ka", value);
    }

    @SneakyThrows
    @UIContextMenuAction("TEST_CONNECTION")
    public ActionResponseModel testConnection() {
        try {
            getService().testService();
            setStatus(Status.ONLINE);
            return ActionResponseModel.success();
        } catch (Exception ex) {
            setStatusError(ex);
            return ActionResponseModel.showError(ex);
        }
    }

    @UIContextMenuAction("MQTT.CLEAR_HISTORY")
    public ActionResponseModel clearHistory() {
        getService().clearHistory();
        return ActionResponseModel.success();
    }

    @Override
    public @NotNull Class<MQTTService> getEntityServiceItemClass() {
        return MQTTService.class;
    }

    @Override
    @SneakyThrows
    public MQTTService createService(@NotNull EntityContext entityContext) {
        return new MQTTService(entityContext, this);
    }

    public void publish(@NotNull String topic, byte[] content, int qoS, boolean retain) {
        try {
            log.debug("[{}]: MQTT Name[{}]. Publish message: Topic[{}], Qos[{}], Retain[{}], Value[{}]", getEntityID(), getTitle(), topic, qoS,
                retain, content);
            getService().getMqttClient().publish(topic, content, qoS, retain);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull List<Object[]> getTimeValueSeries(PeriodRequest request) {
        return getService().getStorage().getTimeSeries(request.getFromTime(), request.getToTime(), "topic",
            getTopicRequire(request.getParameters(), "queryTopic"));
    }

    @Override
    public DynamicParameterFields getDynamicParameterFields(RequestDynamicParameter request) {
        if (request.getMetadata().has("set")) {
            return new MQTTPublishQueryParameter().setPublishTopic("example/test");
        } else if (request.getMetadata().has("get")) {
            return new MQTTTopicQueryParameter().setQueryTopic("example/test");
        }
        return null;
    }

    /*@Override
    public Object getAggregateValueFromSeries(@NotNull PeriodRequest request, @NotNull AggregationType aggregationType, boolean filterOnlyNumbers) {
        String topic = getTopicRequire(request.getParameters(), "queryTopic");
        return getService().getStorage().aggregate(request.getFromTime(), request.getToTime(), "topic", topic,
            aggregationType, filterOnlyNumbers);
    }*/

    @Override
    public Object getStatusValue(GetStatusValueRequest request) {
        JSONObject parameters = request.getDynamicParameters();
        if (parameters == null) {
            return null;
        }
        String topic = getTopicRequire(parameters, "queryTopic");
        return getService().getRawValue(topic);
    }

    @Override
    public SourceHistory getSourceHistory(GetStatusValueRequest request) {
        JSONObject parameters = request.getDynamicParameters();
        if (parameters == null) {
            return null;
        }
        String topic = getTopicRequire(parameters, "queryTopic");
        return getService().getSourceHistory(topic);
    }

    @Override
    public List<SourceHistoryItem> getSourceHistoryItems(GetStatusValueRequest request, int from, int count) {
        JSONObject parameters = request.getDynamicParameters();
        if (parameters == null) {
            return null;
        }
        String topic = getTopicRequire(parameters, "queryTopic");
        return getService().getSourceHistoryItems(topic, from, count);
    }

    @Override
    public void setStatusValue(SetStatusValueRequest request) {
        publishFromDynamicTopic(String.valueOf(request.getValue()), request.getDynamicParameters());
    }

    @Override
    public void addUpdateValueListener(EntityContext entityContext, String key, JSONObject dynamicParameters,
        Consumer<Object> listener) {
        String topic = getTopicRequire(dynamicParameters, "queryTopic");
        entityContext.event().addEventListener(getEntityID() + "~~~" + topic, key, listener);
    }

    @Override
    public String getTimeValueSeriesDescription() {
        return "MQTT.TIME_SERIES";
    }

    @Override
    public String getGetStatusDescription() {
        return "MQTT.GET_VALUE";
    }

    @Override
    public String getSetStatusDescription() {
        return "MQTT.SET_TOPIC";
    }

    @Override
    public long getEntityServiceHashCode() {
        return getJsonDataHashCode("user", "pwd", "cid", "port", "cs", "ct", "ka", "host");
    }

    @Override
    public void beforePersist() {
        this.setMqttClientID(UUID.randomUUID().toString());
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
        publish(topic, value == null ? new byte[0] : value.getBytes(UTF_8), dynamicParameters.optInt("qos", 0),
            dynamicParameters.optBoolean("retain", false));
    }

    @Override
    public boolean isShowHiddenFiles() {
        return true;
    }

    @Override
    public @NotNull String getFileSystemRoot() {
        return "";
    }

    @Override
    public void addListener(String topic, String discriminator, Consumer<Object> listener) {
        log.info("[{}]: Add mqtt listener for '{}' topic", getEntityID(), topic);
        getEntityContext().event().addEventBehaviourListener(getEntityID() + "~~~" + topic, discriminator, listener);
    }

    @Override
    public void removeListener(String topic, String discriminator) {
        log.info("[{}]: Remove mqtt listener from '{}' topic", getEntityID(), topic);
        getEntityContext().event().removeEventListener(discriminator, getEntityID() + "~~~" + topic);
    }

    @Override
    protected @NotNull String getDevicePrefix() {
        return "mqtt";
    }

    @Override
    public String getDefaultName() {
        return "MQTT Mosquito";
    }

    @UIField(order = 1, inlineEdit = true)
    @UIFieldGroup(value = "GENERAL", order = 10)
    public boolean isStart() {
        return getJsonData("start", true);
    }

    public void setStart(boolean start) {
        setJsonData("start", start);
    }

    @Override
    public @NotNull Icon selectionIcon() {
        return new Icon("fas fa-mattress-pillow", "#1CA6E2");
    }

    @Override
    @UIField(order = 1, hideInEdit = true)
    public String getFirmwareVersion() {
        return optService().map(s -> s.getMosquittoInstaller().getVersion()).orElse("-");
    }

    @Override
    public void logBuilder(EntityLogBuilder logBuilder) {
        logBuilder.addTopicFilterByEntityID(MQTTEntrypoint.class);
    }

    @Override
    public void assembleActions(UIInputBuilder uiInputBuilder) {

    }
}
