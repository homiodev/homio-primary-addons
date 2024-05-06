package org.homio.addon.mqtt.entity;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.homio.api.util.Constants.PRIMARY_DEVICE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.homio.addon.mqtt.MQTTEntrypoint;
import org.homio.addon.mqtt.entity.parameters.MQTTPublishQueryParameter;
import org.homio.addon.mqtt.entity.parameters.MQTTTopicQueryParameter;
import org.homio.addon.mqtt.workspace.Scratch3MQTTBlocks;
import org.homio.api.Context;
import org.homio.api.ContextService;
import org.homio.api.ContextService.MQTTEntityService;
import org.homio.api.entity.device.DeviceEndpointsBehaviourContractStub;
import org.homio.api.entity.log.HasEntityLog;
import org.homio.api.entity.log.HasEntitySourceLog;
import org.homio.api.entity.storage.BaseFileSystemEntity;
import org.homio.api.entity.types.CommunicationEntity;
import org.homio.api.entity.validation.UIFieldValidationSize;
import org.homio.api.entity.version.HasFirmwareVersion;
import org.homio.api.entity.widget.PeriodRequest;
import org.homio.api.entity.widget.ability.HasGetStatusValue;
import org.homio.api.entity.widget.ability.HasSetStatusValue;
import org.homio.api.entity.widget.ability.HasTimeValueSeries;
import org.homio.api.exception.ServerException;
import org.homio.api.fs.TreeConfiguration;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.FileContentType;
import org.homio.api.model.FileModel;
import org.homio.api.model.Icon;
import org.homio.api.model.OptionModel;
import org.homio.api.model.Status;
import org.homio.api.model.endpoint.BaseDeviceEndpoint;
import org.homio.api.model.endpoint.DeviceEndpoint;
import org.homio.api.service.EntityService;
import org.homio.api.state.State;
import org.homio.api.state.StringType;
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
import org.homio.api.ui.field.selection.SelectionConfiguration;
import org.homio.api.ui.field.selection.dynamic.DynamicParameterFields;
import org.homio.api.ui.field.selection.dynamic.SelectionWithDynamicParameterFields;
import org.homio.api.util.CommonUtils;
import org.homio.api.util.DataSourceUtil;
import org.homio.api.util.SecureString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Log4j2
@Entity
@UISidebarChildren(icon = "fas fa-building", color = Scratch3MQTTBlocks.COLOR)
public class MQTTClientEntity extends CommunicationEntity implements
    EntityService<MQTTService>,
    BaseFileSystemEntity<MQTTFileSystem>,
    HasTimeValueSeries,
    SelectionWithDynamicParameterFields,
    HasGetStatusValue,
    HasSetStatusValue,
    ContextService.MQTTEntityService,
    SelectionConfiguration,
    HasEntityLog,
    HasEntitySourceLog,
    DeviceEndpointsBehaviourContractStub,
    HasFirmwareVersion {

    public static MQTTClientEntity ensureEntityExists(Context context) {
        MQTTClientEntity entity = context.db().getEntity(MQTTClientEntity.class, PRIMARY_DEVICE);
        if (entity == null) {
            log.info("Save default mqtt device");
            entity = new MQTTClientEntity();
            entity.setEntityID(PRIMARY_DEVICE);
            entity = context.db().save(entity);
        }
        return entity;
    }

    @Override
    public @NotNull Map<String, ? extends DeviceEndpoint> getDeviceEndpoints() {
        Map<String, MQTTEndpoint> streams = new TreeMap<>();
        MQTTService mqttService = optService().orElse(null);
        if (mqttService == null) {
            return Map.of();
        }
        Map<String, Set<String>> topics = new TreeMap<>();
        for (var entry : mqttService.getEventListeners().entrySet()) {
            topics.put(entry.getKey(), new TreeSet<>(entry.getValue().keySet()));
        }
        for (var entry : mqttService.getEventPatternListeners().entrySet()) {
            topics.putIfAbsent(entry.getKey(), new TreeSet<>());
            topics.get(entry.getKey()).addAll(entry.getValue().keySet());
        }
        for (Entry<String, Set<String>> entry : topics.entrySet()) {
            streams.put(entry.getKey(), new MQTTEndpoint(entry.getKey(), entry.getValue(), this));
        }
        return streams;
    }

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
    public @NotNull List<TreeConfiguration> buildFileSystemConfiguration(@NotNull Context context) {
        return getService().getValue();
    }

    @Override
    public @NotNull Icon getFileSystemIcon() {
        return getSelectionIcon();
    }

    @Override
    public boolean requireConfigure() {
        return isEmpty(getUser()) || getPassword().isEmpty();
    }

    @Override
    public @NotNull MQTTFileSystem buildFileSystem(@NotNull Context context, int alias) {
        return new MQTTFileSystem(this);
    }

    @Override
    public long getConnectionHashCode() {
        return getEntityServiceHashCode();
    }

    @UIField(order = 20)
    @UIFieldGroup(order = 1, value = "CONNECTION", borderColor = "#0EBC97")
    public @NotNull String getHostname() {
        return getJsonData("host", "127.0.0.1");
    }

    public void setHostname(String value) {
        setJsonData("host", value);
    }

    @UIField(order = 50, label = "mqttPort")
    @UIFieldPort
    @UIFieldGroup("CONNECTION")
    public int getPort() {
        return getJsonData("port", 1883);
    }

    public void setPort(String value) {
        setJsonData("port", value);
    }

    @Override
    public @Nullable String getLastValue(@NotNull String topic) {
        return getService().getLastValues().get(topic);
    }

    @UIField(order = 1, required = true, inlineEditWhenEmpty = true)
    @UIFieldValidationSize(min = 4, max = 20)
    @UIFieldGroup(order = 2, value = "SECURITY", borderColor = "#23ADAB")
    public @NotNull String getUser() {
        return getJsonData("user");
    }

    public void setUser(String value) {
        if (!Pattern.compile("^[a-zA-Z0-9]{4,20}$").matcher(value).matches()) {
            throw new ServerException("User must match pattern [a-zA-Z0-9]");
        }
        setJsonData("user", value);
    }

    @UIField(order = 2, required = true, inlineEditWhenEmpty = true)
    @UIFieldGroup("SECURITY")
    public @NotNull SecureString getPassword() {
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

    public void setIncludeSys(boolean value) {
        setJsonData("sys", value);
    }

    @UIField(order = 80, hideInView = true)
    @UIFieldGroup("CONNECTION")
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
    @UIFieldGroup("CONNECTION")
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

    @UIField(order = 120)
    @UIFieldGroup("CONNECTION")
    public boolean isPersistenceData() {
        return getJsonData("pd", false);
    }

    public void ssPersistenceData(boolean value) {
        setJsonData("pd", value);
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
    public MQTTService createService(@NotNull Context context) {
        return new MQTTService(context, this);
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
    public void addUpdateValueListener(Context context, String discriminator, JSONObject dynamicParameters,
        Consumer<State> listener) {
        String topic = getTopicRequire(dynamicParameters, "queryTopic");
        String fullTopicPath = MQTTEntityService.buildMqttListenEvent(getEntityID(), topic);
        context.event().addEventListener(fullTopicPath, discriminator, listener);
    }

    @Override
    public @Nullable String getSelectionDescription() {
        return "MQTT.TIME_SERIES";
    }

    @Override
    public @Nullable Set<String> getConfigurationErrors() {
        Set<String> errors = new HashSet<>();
        if (getUser().isEmpty()) {
            errors.add("ERROR.NO_USER");
        }
        if (getPassword().isEmpty()) {
            errors.add("ERROR.NO_PASSWORD");
        }
        return errors;
    }

    @Override
    public long getEntityServiceHashCode() {
        return getJsonDataHashCode("user", "pwd", "cid", "port", "cs", "ct", "ka", "host", "ct", "pd");
    }

    @Override
    public void beforePersist() {
        this.setMqttClientID(UUID.randomUUID().toString());
    }

    private String getTopicRequire(JSONObject dynamicParameters, String fieldName) {
        String topic = dynamicParameters.optString(fieldName);
        topic = DataSourceUtil.getSelection(topic).getValue();
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
    public void addListener(@NotNull String topic, @NotNull String discriminator, @NotNull BiConsumer<String, String> listener) {
        log.info("[{}]: Add mqtt listener for '{}' topic", getEntityID(), topic);
        getService().addEventBehaviourListener(topic, discriminator, listener);
    }

    @Override
    public boolean isDisableDelete() {
        return true;
    }

    @Override
    protected void assembleMissingMandatoryFields(@NotNull Set<String> fields) {
    }

    @Override
    public void removeListener(@Nullable String topic, @NotNull String discriminator) {
        log.info("[{}]: Remove mqtt listener from '{}' topic and discriminator: {}", getEntityID(), topic, discriminator);
        getService().removeEventListener(discriminator, topic);
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
    @UIFieldGroup("GENERAL")
    public boolean isStart() {
        return getJsonData("start", true);
    }

    public void setStart(boolean start) {
        setJsonData("start", start);
    }

    @Override
    public @NotNull Icon getSelectionIcon() {
        return new Icon("fas fa-mattress-pillow", "#1CA6E2");
    }

    @Override
    public @Nullable String getFirmwareVersion() {
        return optService().map(s -> s.getMosquittoInstaller().getVersion()).orElse(null);
    }

    @Override
    public void logBuilder(EntityLogBuilder logBuilder) {
        logBuilder.addTopicFilterByEntityID(MQTTEntrypoint.class);
    }

    @Override
    public void assembleActions(UIInputBuilder uiInputBuilder) {

    }

    @SneakyThrows
    @UIContextMenuAction(value = "MOSQUITTO_EDIT_CONFIG",
                         icon = "fas fa-keyboard",
                         iconColor = "#899343")
    public ActionResponseModel editConfig() {
        Path configFile = getService().getConfigFile();
        String content = Files.readString(configFile);
        return ActionResponseModel.showFile(new FileModel("mosquitto.conf", content, FileContentType.ini)
            .setSaveHandler(mc -> getService().updateConfiguration(mc)));
    }

    @Override
    public @NotNull List<OptionModel> getLogSources() {
        return List.of(OptionModel.of("log", "MQTT Log File"));
    }

    @Override
    public @Nullable InputStream getSourceLogInputStream(@NotNull String sourceID) throws Exception {
        return Files.newInputStream(getLogFilePath());
    }

    @JsonIgnore
    public Path getLogFilePath() {
        return CommonUtils.getLogsEntitiesPath().resolve(getType()).resolve(getEntityID());
    }

    @JsonIgnore
    public int getUserPasswordHash() {
        return getJsonData("uph", 0);
    }

    public void setUserPasswordHash(int newCode) {
        setJsonData("uph", newCode);
    }

    @Getter
    public static class MQTTEndpoint extends BaseDeviceEndpoint<MQTTClientEntity> {

        private final String description;

        public MQTTEndpoint(String discriminator, Set<String> topic, MQTTClientEntity entity) {
            super(new Icon("fas fa-mattress-pillow", "#4388C4"), "MQTT", entity.context(), entity, discriminator, false, EndpointType.string);
            this.description = topic.stream().sorted().collect(Collectors.joining("<br/>"));
            setInitialValue(new StringType(""));
        }

        @Override
        public @NotNull String getName(boolean shortFormat) {
            return "/" + getEndpointEntityID();
        }
    }
}
