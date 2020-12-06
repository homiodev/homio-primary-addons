package org.touchhome.bundle.bluetooth;

import com.pi4j.system.SystemInfo;
import com.pivovarit.function.ThrowingRunnable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.ble.BleApplicationListener;
import org.ble.BluetoothApplication;
import org.dbus.InterfacesAddedSignal.InterfacesAdded;
import org.dbus.InterfacesRomovedSignal.InterfacesRemoved;
import org.freedesktop.dbus.Variant;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.touchhome.bundle.api.BundleEntryPoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.hardware.other.LinuxHardwareRepository;
import org.touchhome.bundle.api.hardware.wifi.Network;
import org.touchhome.bundle.api.hardware.wifi.WirelessHardwareRepository;
import org.touchhome.bundle.api.model.UserEntity;
import org.touchhome.bundle.api.setting.BundleSettingPluginStatus;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.bluetooth.setting.BluetoothStatusSetting;
import org.touchhome.bundle.cloud.netty.setting.CloudServerRestartSetting;
import org.touchhome.bundle.cloud.setting.ConsoleCloudProviderSetting;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.touchhome.bundle.api.model.UserEntity.ADMIN_USER;
import static org.touchhome.bundle.api.util.TouchHomeUtils.distinctByKey;

@Log4j2
@Controller
@RequiredArgsConstructor
public class BluetoothBundleEntryPoint implements BundleEntryPoint {

    public static final int MIN_WRITE_TIMEOUT = 10000; // 10 sec
    private static final String PREFIX = "13333333-3333-3333-3333-3333333330";
    private static final String SERVICE_UUID = PREFIX + "00";
    private static final String CPU_LOAD_UUID = PREFIX + "01";
    private static final String CPU_TEMP_UUID = PREFIX + "02";
    private static final String DEVICE_MODEL_UUID = PREFIX + "03";
    private static final String MEMORY_UUID = PREFIX + "04";
    private static final String UPTIME_UUID = PREFIX + "05";
    private static final String WIFI_NAME_UUID = PREFIX + "06";
    private static final String IP_ADDRESS_UUID = PREFIX + "07";
    private static final String PWD_SET_UUID = PREFIX + "08";
    private static final String KEYSTORE_SET_UUID = PREFIX + "09";
    private static final String WIFI_LIST_UUID = PREFIX + "10";
    private static final String SD_MEMORY_UUID = PREFIX + "11";
    private static final String WRITE_BAN_UUID = PREFIX + "12";
    private static final String SERVER_CONNECTED_UUID = PREFIX + "13";
    private static final String FEATURES_UUID = PREFIX + "14";
    private static final int TIME_REFRESH_PASSWORD = 5 * 60000; // 5 minute for session
    private static long timeSinceLastCheckPassword = -1;
    private final EntityContext entityContext;
    private final LinuxHardwareRepository linuxHardwareRepository;
    private final WirelessHardwareRepository wirelessHardwareRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, Long> wifiWriteProtect = new ConcurrentHashMap<>();
    private BluetoothApplication bluetoothApplication;
    private String loginUser;

    public String getDeviceCharacteristic(String uuid) {
        switch (uuid) {
            case CPU_LOAD_UUID:
                return readIfLinux(linuxHardwareRepository::getCpuLoad);
            case CPU_TEMP_UUID:
                return readIfLinux(this::getCpuTemp);
            case MEMORY_UUID:
                return readIfLinux(linuxHardwareRepository::getMemory);
            case SD_MEMORY_UUID:
                return readIfLinux(() -> linuxHardwareRepository.getSDCardMemory().toString());
            case UPTIME_UUID:
                return readIfLinux(linuxHardwareRepository::getUptime);
            case IP_ADDRESS_UUID:
                return getUserIPAddress();
            case WRITE_BAN_UUID:
                return gatherWriteBan();
            case DEVICE_MODEL_UUID:
                return readIfLinux(linuxHardwareRepository::getDeviceModel);
            case SERVER_CONNECTED_UUID:
                return readSafeValue(this::readServerConnected);
            case WIFI_LIST_UUID:
                return readIfLinux(this::readWifiList);
            case WIFI_NAME_UUID:
                return readIfLinux(this::getWifiName);
            case KEYSTORE_SET_UUID:
                return readSafeValue(this::getKeystore);
            case PWD_SET_UUID:
                return readPwdSet();
            case FEATURES_UUID:
                return readSafeValue(this::getFeatures);
        }
        return null;
    }

    private String gatherWriteBan() {
        List<String> status = new ArrayList<>();
        for (Map.Entry<String, Long> entry : wifiWriteProtect.entrySet()) {
            if (System.currentTimeMillis() - entry.getValue() < MIN_WRITE_TIMEOUT) {
                status.add(entry.getKey() + "%&%" + ((MIN_WRITE_TIMEOUT - (System.currentTimeMillis() - entry.getValue())) / 1000));
            }
        }
        return String.join("%#%", status);
    }

    public void setDeviceCharacteristic(String uuid, byte[] value) {
        if (value != null && (!wifiWriteProtect.containsKey(uuid) || System.currentTimeMillis() - wifiWriteProtect.get(uuid) > MIN_WRITE_TIMEOUT)) {
            wifiWriteProtect.put(uuid, System.currentTimeMillis());
            switch (uuid) {
                case DEVICE_MODEL_UUID:
                    rebootDevice(null);
                    return;
                case WIFI_NAME_UUID:
                    writeWifiSSID(value);
                    return;
                case PWD_SET_UUID:
                    writePwd(value);
                    return;
                case KEYSTORE_SET_UUID:
                    writeKeystore(value);
            }
        }
    }

    @Override
    public Class<? extends BundleSettingPluginStatus> getBundleStatusSetting() {
        return BluetoothStatusSetting.class;
    }

    public void init() {
        log.info("Starting bluetooth...");

        bluetoothApplication = new BluetoothApplication("touchHome", SERVICE_UUID, new BleApplicationListener() {
            @Override
            public void deviceConnected(Variant<String> address, InterfacesAdded signal) {
                log.info("Device connected. Address: <{}>. Path: <{}>", address.getValue(), signal.getObjectPath());
                timeSinceLastCheckPassword = -1;
            }

            @Override
            public void deviceDisconnected(InterfacesRemoved signal) {
                log.info("Device disconnected. Path: <{}>", signal.getObjectPath());
                timeSinceLastCheckPassword = -1;
            }
        });

        bluetoothApplication.newReadCharacteristic("cpu_load", CPU_LOAD_UUID, () -> readIfLinux(linuxHardwareRepository::getCpuLoad).getBytes());
        bluetoothApplication.newReadCharacteristic("cpu_temp", CPU_TEMP_UUID, () -> readIfLinux(this::getCpuTemp).getBytes());
        bluetoothApplication.newReadCharacteristic("memory", MEMORY_UUID, () -> readIfLinux(linuxHardwareRepository::getMemory).getBytes());
        bluetoothApplication.newReadCharacteristic("sd_memory", SD_MEMORY_UUID, () -> readIfLinux(() -> linuxHardwareRepository.getSDCardMemory().toString()).getBytes());
        bluetoothApplication.newReadCharacteristic("uptime", UPTIME_UUID, () -> readIfLinux(linuxHardwareRepository::getUptime).getBytes());
        bluetoothApplication.newReadCharacteristic("ip", IP_ADDRESS_UUID, () -> getUserIPAddress().getBytes());
        bluetoothApplication.newReadCharacteristic("write_ban", WRITE_BAN_UUID, () -> bluetoothApplication.gatherWriteBan().getBytes());
        bluetoothApplication.newReadWriteCharacteristic("device_model", DEVICE_MODEL_UUID, this::rebootDevice, () -> readIfLinux(linuxHardwareRepository::getDeviceModel).getBytes());
        bluetoothApplication.newReadCharacteristic("server_connected", SERVER_CONNECTED_UUID, () -> readSafeValue(this::readServerConnected).getBytes());
        bluetoothApplication.newReadCharacteristic("wifi_list", WIFI_LIST_UUID, () -> readIfLinux(this::readWifiList).getBytes());
        bluetoothApplication.newReadWriteCharacteristic("wifi_name", WIFI_NAME_UUID, this::writeWifiSSID, () -> readIfLinux(this::getWifiName).getBytes());
        bluetoothApplication.newReadWriteCharacteristic("pwd", PWD_SET_UUID, this::writePwd, () -> readPwdSet().getBytes());
        bluetoothApplication.newReadCharacteristic("features", FEATURES_UUID, () -> readSafeValue(this::getFeatures).getBytes());

        bluetoothApplication.newReadWriteCharacteristic("keystore", KEYSTORE_SET_UUID, this::writeKeystore,
                () -> readSafeValue(this::getKeystore).getBytes());

        // start ble
        try {
            bluetoothApplication.start();
            log.info("Bluetooth successfully started");
            entityContext.setFeatureState("Bluetooth", true);
            entityContext.setting().setValue(BluetoothStatusSetting.class, BundleSettingPluginStatus.ONLINE);
        } catch (Throwable ex) {
            entityContext.setting().setValue(BluetoothStatusSetting.class, BundleSettingPluginStatus.error(ex));
            entityContext.setFeatureState("Bluetooth", false);
            log.error("Unable to start bluetooth service", ex);
        }
    }

    private String readTimeToReleaseSession() {
        return Long.toString((TIME_REFRESH_PASSWORD - (System.currentTimeMillis() - timeSinceLastCheckPassword)) / 1000);
    }

    @SneakyThrows
    private void writeKeystore(byte[] bytes) {
        writeSafeValue(() -> {
            byte type = bytes[0];
            byte[] content = Arrays.copyOfRange(bytes, 1, bytes.length);
            switch (type) {
                case 3:
                    log.warn("Writing keystore");
                    entityContext.save(getUser().setKeystore(content));
                    entityContext.setting().setValue(CloudServerRestartSetting.class, null);
                    break;
                case 5:
                    log.warn("Writing private key");
                    Path privateKey = TouchHomeUtils.getSshPath().resolve("id_rsa_touchhome");
                    FileUtils.writeByteArrayToFile(privateKey.toFile(), content);
                    linuxHardwareRepository.setPermissions(privateKey, 600);
                    break;
                case 7:
                    log.warn("Writing public key");
                    Path publicKey = TouchHomeUtils.getSshPath().resolve("id_rsa_touchhome.pub");
                    FileUtils.writeByteArrayToFile(publicKey.toFile(), content);
                    linuxHardwareRepository.setPermissions(publicKey, 600);
                    break;
            }
        });
    }

    private String getUserIPAddress() {
        return readIfLinux(wirelessHardwareRepository::getIPAddress) + "%&%" + this.loginUser;
    }

    private UserEntity getUser() {
        return entityContext.getEntity(ADMIN_USER);
    }

    /**
     * We may set password only once. If user wants update password, he need pass old password hash
     */
    private void writePwd(byte[] bytes) {
        String[] split = new String(bytes).split("%&%");
        this.loginUser = split[0];
        String pwd = split[1];
        String prevPwd = split.length > 2 ? split[2] : "";
        UserEntity user = getUser();
        if (user.isPasswordNotSet(passwordEncoder)) {
            log.warn("Set primary admin password for user: <{}>", this.loginUser);
            entityContext.save(user.setUserId(this.loginUser).setPassword(pwd, passwordEncoder));
            this.entityContext.setting().setValue(CloudServerRestartSetting.class, null);
        } else if (Objects.equals(user.getUserId(), this.loginUser) &&
                user.matchPassword(prevPwd, passwordEncoder)) {
            log.warn("Reset primary admin password for user: <{}>", this.loginUser);
            entityContext.save(user.setPassword(pwd, passwordEncoder));
            this.entityContext.setting().setValue(CloudServerRestartSetting.class, null);
        }

        if (user.matchPassword(pwd, passwordEncoder)) {
            timeSinceLastCheckPassword = System.currentTimeMillis();
        }
    }

    private void writeWifiSSID(byte[] bytes) {
        writeSafeValue(() -> {
            String[] split = new String(bytes).split("%&%");
            if (split.length == 3 && split[1].length() >= 8) {
                log.info("Writing wifi credentials");
                wirelessHardwareRepository.setWifiCredentials(split[0], split[1], split[2]);
                wirelessHardwareRepository.restartNetworkInterface();
            }
        });
    }

    private void rebootDevice(byte[] ignore) {
        writeSafeValue(linuxHardwareRepository::reboot);
    }

    @Override
    public String getBundleId() {
        return "bluetooth";
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    private String readPwdSet() {
        if (getUser().isPasswordNotSet(passwordEncoder)) {
            return "none";
        } else if (System.currentTimeMillis() - timeSinceLastCheckPassword > TIME_REFRESH_PASSWORD) {
            return "required";
        }
        return "ok:" + readTimeToReleaseSession();
    }

    private String readWifiList() {
        return wirelessHardwareRepository.scan(wirelessHardwareRepository.getActiveNetworkInterface()).stream()
                .filter(distinctByKey(Network::getSsid))
                .map(n -> n.getSsid() + "%&%" + n.getStrength()).collect(Collectors.joining("%#%"));
    }

    private String readServerConnected() {
        return entityContext.setting().getValue(ConsoleCloudProviderSetting.class).getStatus();
    }

    @SneakyThrows
    private void writeSafeValue(ThrowingRunnable<Exception> runnable) {
        if (hasAccess()) {
            runnable.run();
        }
    }

    private String readSafeValue(Supplier<String> supplier) {
        if (hasAccess()) {
            return supplier.get();
        }
        return "";
    }

    private String readIfLinux(Supplier<String> supplier) {
        if (hasAccess() && EntityContext.isLinuxEnvironment()) {
            return supplier.get();
        }
        return "";
    }

    private boolean hasAccess() {
        return System.currentTimeMillis() - timeSinceLastCheckPassword < TIME_REFRESH_PASSWORD || SecurityContextHolder.getContext().getAuthentication() != null;
    }

    private String getFeatures() {
        return entityContext.getDeviceFeatures().entrySet().stream().map(es -> es.getKey() + "_" + es.getValue()).collect(Collectors.joining(";"));
    }

    private String getWifiName() {
        return linuxHardwareRepository.getWifiName();
    }

    @SneakyThrows
    private String getCpuTemp() {
        return String.valueOf(SystemInfo.getCpuTemperature());
    }

    private String getKeystore() {
        UserEntity user = getUser();
        return String.valueOf(user.getKeystoreDate() == null ? "" : user.getKeystoreDate().getTime());
    }
}
