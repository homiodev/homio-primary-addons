package org.touchhome.bundle.firmata.model;

import org.apache.commons.lang3.StringUtils;
import org.firmata4j.IODevice;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.transport.NetworkTransport;
import org.touchhome.bundle.api.DynamicOptionLoader;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.json.Option;
import org.touchhome.bundle.api.model.BaseEntity;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.firmata.FirmataBundleEntrypoint;
import org.touchhome.bundle.firmata.provider.FirmataDeviceCommunicator;
import org.touchhome.bundle.firmata.provider.command.PendingRegistrationContext;

import javax.persistence.Entity;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class FirmataNetworkEntity extends FirmataBaseEntity<FirmataNetworkEntity> {

    @UIField(order = 22, type = UIFieldType.TextSelectBoxDynamic)
    @Pattern(regexp = "(\\d{1,3}\\.){3}\\d{1,3}", message = "validation.host_port")
    @UIFieldSelection(SelectFirmataIpDeviceLoader.class)
    @UIFieldSelectValueOnEmpty(label = "selection.selectIp", color = "#A7D21E")
    public String getIp() {
        return getJsonData("ip");
    }

    public FirmataNetworkEntity setIp(String ip) {
        setJsonData("ip", ip);

        FirmataBundleEntrypoint.UdpPayload udpPayload = FirmataBundleEntrypoint.getUdpFoundDevices().get(ip);
        if (udpPayload != null) {
            setTarget(udpPayload.getDeviceID());
            setBoardType(udpPayload.getBoard());
        }
        return this;
    }

    @Override
    @UIField(order = 100, readOnly = true)
    public String getIeeeAddress() {
        return super.getIeeeAddress();
    }

    @Override
    public FirmataDeviceCommunicator createFirmataDeviceType(EntityContext entityContext) {
        return new FirmataNetworkFirmataDeviceCommunicator(entityContext, this);
    }

    @Override
    protected boolean allowRegistrationType(PendingRegistrationContext pendingRegistrationContext) {
        return pendingRegistrationContext.getEntity() instanceof FirmataNetworkEntity;
    }

    private static class FirmataNetworkFirmataDeviceCommunicator extends FirmataDeviceCommunicator<FirmataNetworkEntity> {

        public FirmataNetworkFirmataDeviceCommunicator(EntityContext entityContext, FirmataNetworkEntity entity) {
            super(entityContext, entity);
        }

        @Override
        protected IODevice createIODevice(FirmataNetworkEntity entity) {
            String ip = entity.getIp();
            return StringUtils.isEmpty(ip) ? null : new FirmataDevice(new NetworkTransport(ip + ":3030"));
        }

        @Override
        public long generateUniqueIDOnRegistrationSuccess() {
            return 1;
        }
    }

    public static class SelectFirmataIpDeviceLoader implements DynamicOptionLoader {

        @Override
        public List<Option> loadOptions(Object parameter, BaseEntity baseEntity, EntityContext entityContext) {
            Map<String, FirmataBundleEntrypoint.UdpPayload> udpFoundDevices = FirmataBundleEntrypoint.getUdpFoundDevices();
            return udpFoundDevices.entrySet().stream().map(e -> Option.of(e.getKey(), e.getValue().toString())).collect(Collectors.toList());
        }
    }
}
