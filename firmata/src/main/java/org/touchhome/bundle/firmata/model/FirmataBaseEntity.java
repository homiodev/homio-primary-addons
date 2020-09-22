package org.touchhome.bundle.firmata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchhome.bundle.api.DynamicOptionLoader;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.json.NotificationEntityJSON;
import org.touchhome.bundle.api.json.Option;
import org.touchhome.bundle.api.model.BaseEntity;
import org.touchhome.bundle.api.model.MicroControllerBaseEntity;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldName;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.api.ui.method.UIMethodAction;
import org.touchhome.bundle.firmata.provider.FirmataDeviceCommunicator;
import org.touchhome.bundle.firmata.provider.IODeviceWrapper;
import org.touchhome.bundle.firmata.provider.command.FirmataRegisterCommand;
import org.touchhome.bundle.firmata.provider.command.PendingRegistrationContext;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Accessors(chain = true)
public abstract class FirmataBaseEntity<T extends FirmataBaseEntity<T>> extends MicroControllerBaseEntity<T> {

    public static final String PREFIX = "fm_";

    @Setter
    @Getter
    @Transient
    @JsonIgnore
    private FirmataDeviceCommunicator firmataDeviceCommunicator;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @UIField(order = 23, readOnly = true)
    public String getBoardType() {
        return getJsonData("boardType");
    }

    public FirmataBaseEntity<T> setBoardType(String boardType) {
        return setJsonData("boardType", boardType);
    }

    @Override
    @UIField(order = 100, inlineEdit = true)
    @UIFieldSelection(SelectTargetFirmataDeviceLoader.class)
    @UIFieldSelectValueOnEmpty(label = "selection.target", color = "#A7D21E")
    public String getIeeeAddress() {
        return super.getIeeeAddress();
    }

    @Override
    @UIFieldName("communicatorStatus")
    public Status getStatus() {
        return super.getStatus();
    }

    @UIMethodAction("ACTION.COMMUNICATOR.RESTART")
    public NotificationEntityJSON restartCommunicator() {
        if (firmataDeviceCommunicator != null) {
            if (firmataDeviceCommunicator.restart()) {
                return NotificationEntityJSON.success(getTitle()).setDescription("ACTION.COMMUNICATOR.SUCCESS");
            }
            return NotificationEntityJSON.danger(getTitle()).setDescription("ACTION.COMMUNICATOR.FAILED");
        }
        return NotificationEntityJSON.warn(getTitle()).setDescription("ACTION.COMMUNICATOR_NOT_SET");
    }

    @JsonIgnore
    public short getTarget() {
        return getIeeeAddress() == null ? -1 : Short.parseShort(getIeeeAddress());
    }

    public void setTarget(short target) {
        setIeeeAddress(Short.toString(target));
    }

    @Override
    public String getShortTitle() {
        return "Firmata";
    }

    @Override
    public int getOrder() {
        return 20;
    }

    public abstract FirmataDeviceCommunicator createFirmataDeviceType(EntityContext entityContext);

    @JsonIgnore
    public final IODeviceWrapper getDevice() {
        return firmataDeviceCommunicator.getDevice();
    }

    public long getUniqueID() {
        return getJsonData("uniqueID", 0L);
    }

    public FirmataBaseEntity<T> setUniqueID(Long uniqueID) {
        setJsonData("uniqueID", uniqueID);
        return this;
    }

    protected abstract boolean allowRegistrationType(PendingRegistrationContext pendingRegistrationContext);

    public static class SelectTargetFirmataDeviceLoader implements DynamicOptionLoader {

        @Override
        public List<Option> loadOptions(Object parameter, BaseEntity baseEntity, EntityContext entityContext) {
            return entityContext.getBean(FirmataRegisterCommand.class).getPendingRegistrations().entrySet().stream()
                    .filter(entry -> ((FirmataBaseEntity) baseEntity).allowRegistrationType(entry.getValue()))
                    .map(entry -> Option.of(Short.toString(entry.getKey()), entry.getKey() + "/" + entry.getValue()))
                    .collect(Collectors.toList());
        }
    }
}
