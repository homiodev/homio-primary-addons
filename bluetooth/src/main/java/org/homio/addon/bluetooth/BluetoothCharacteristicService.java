package org.homio.addon.bluetooth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;
import org.ble.BleApplicationListener;
import org.ble.BluetoothApplication;
import org.dbus.InterfacesAddedSignal.InterfacesAdded;
import org.dbus.InterfacesRomovedSignal.InterfacesRemoved;
import org.freedesktop.dbus.Variant;
import org.homio.api.Context;
import org.homio.api.util.HardwareUtils;
import org.homio.hquery.hardware.network.Network;
import org.homio.hquery.hardware.network.NetworkHardwareRepository;
import org.homio.hquery.hardware.other.MachineHardwareRepository;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.homio.api.util.JsonUtils.OBJECT_MAPPER;

@Log4j2
@Service
@RequiredArgsConstructor
public class BluetoothCharacteristicService {

  private static final String PREFIX = "13333333-3333-3333-3333-3333333330";
  private static final String SERVICE_UUID = PREFIX + "00";
  private static final String DATA_UUID = PREFIX + "10";
  private static final String selectedWifiInterface = "wlan0";

  private final MachineHardwareRepository machineHardwareRepository;
  private final NetworkHardwareRepository networkHardwareRepository;
  private final Environment env;
  private final Context context;

  private static void updateHostapdConfigCountryCode(String country) {
    try {
      Path hostapdConf = Paths.get("/etc/hostapd/hostapd.conf");
      Properties properties = new Properties();
      try (InputStream inputStream = Files.newInputStream(hostapdConf)) {
        properties.load(inputStream);
      }
      properties.setProperty("country_code", country);
      try (OutputStream outputStream = Files.newOutputStream(hostapdConf)) {
        properties.store(outputStream, null);
      }
      log.info("Hostapd country_code updated successfully.");
    } catch (Exception ex) {
      log.error("Error while update hostapd country_code: {}", ex.getMessage());
    }
  }

  @PostConstruct
  public void postConstruct() {
    if (!SystemUtils.IS_OS_LINUX) {
      log.info("Bluetooth skipped for non linux env. Require unix sockets");
    } else {
      log.info("Starting bluetooth...");

      BluetoothApplication bluetoothApplication = new BluetoothApplication("homio", SERVICE_UUID, new BleApplicationListener() {
        @Override
        public void deviceConnected(Variant<String> address, InterfacesAdded signal) {
          log.info("Device connected. Address: '{}'. Path: '{}'", address.getValue(), signal.getObjectPath());
        }

        @Override
        public void deviceDisconnected(InterfacesRemoved signal) {
          log.info("Device disconnected. Path: '{}'", signal.getObjectPath());
        }
      });

      bluetoothApplication.newReadWriteCharacteristic("data", DATA_UUID, this::handleCommand, () -> getDeviceInfo().getBytes());

      // start ble
      try {
        bluetoothApplication.start();
        log.info("Bluetooth successfully started");
      } catch (Throwable ex) {
        log.error("Unable to start bluetooth service: '{}'", ex.getMessage());
      }
    }
  }

  @SneakyThrows
  public void handleCommand(byte[] bytes) {
    JsonNode command = OBJECT_MAPPER.readValue(bytes, JsonNode.class);
    log.info("Received command: {}", command);
    if (!isLinux()) {
      return;
    }
    if (context.user().isRequireAuth()) {
      context.user().assertUserCredentials(command.get("user").asText(""), command.get("pwd").asText(""));
    }
    switch (command.get("type").asText("")) {
      case "reboot":
        log.info("Reboot device");
        machineHardwareRepository.reboot();
        return;
      case "wifi":
        log.info("Update wifi configuration");
        String ssid = command.get("ssid").asText();
        String country = command.get("country").asText();
        String ssidPwd = command.get("ssidPwd").asText();
        if (ssid.isEmpty() || country.isEmpty() || ssidPwd.isEmpty()) {
          log.error("Unable to set wifi without data");
          return;
        }
        log.info("Writing wifi credentials: SSID: {}. PWD: {}. COUNTRY: {}", ssid, ssidPwd, country);
        networkHardwareRepository.setWifiCredentials(ssid, ssidPwd, country);
        updateHostapdConfigCountryCode(country);
        // this script should connect to router or run hotspot
        machineHardwareRepository.execute("/usr/bin/autohotspot", 60);
    }
    log.error("Unknown command: {}", command);
  }

  @SneakyThrows
  public String getDeviceInfo() {
    try {
      String value = new ObjectMapper().writeValueAsString(new MachineSummary());
      log.info("Getting device info: {}", value);
      return value;
    } catch (Exception ex) {
      log.error("Error during reading: {}", ex.getMessage());
      throw ex;
    }
  }

  protected <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  private boolean isLinux() {
    return SystemUtils.IS_OS_LINUX && !env.acceptsProfiles(Profiles.of("demo"));
  }

  private List<WifiInfo> readWifiList() {
    if (SystemUtils.IS_OS_LINUX) {
      if (this.isLinux()) {
        List<Network> networks = networkHardwareRepository.scan(selectedWifiInterface);
        if (networks == null) {
          return List.of();
        }
        return networks.stream()
          .filter(distinctByKey(Network::getSsid))
          .map(n -> new WifiInfo(n.getSsid(), n.getStrength()))
          .toList();
      }
      return List.of();
    }
    ArrayList<String> result = machineHardwareRepository
      .executeNoErrorThrowList("netsh wlan show profiles", 60, null);
    if (result == null) {
      return List.of();
    }
    return result.stream()
      .filter(s -> s.contains("All User Profile"))
      .map(s -> s.substring(s.indexOf(":") + 1).trim())
      .map(s -> new WifiInfo(s, -1)).toList();
  }

  @Getter
  @AllArgsConstructor
  public static class WifiInfo {
    private String n;
    private int s;
  }

  @Getter
  public class MachineSummary {

    private final boolean linux = SystemUtils.IS_OS_LINUX;
    private final String model = SystemUtils.OS_NAME;
    private final String mac;
    private final String wifi;
    private final String ip;
    private final String time;
    private final String memory;
    private final String disc;
    private final boolean net;
    private final String id;
    private final String version;
    private final boolean cred;
    private final List<WifiInfo> wl;

    public MachineSummary() {
      id = HardwareUtils.APP_ID;
      version = System.getProperty("server.version");
      mac = get("mac", networkHardwareRepository::getMacAddress);
      wifi = get("wifi", networkHardwareRepository::getWifiName);
      ip = get("ip", networkHardwareRepository::getIPAddress);
      time = get("time", machineHardwareRepository::getUptime);
      memory = get("memory", machineHardwareRepository::getRamMemory);
      disc = get("disk", machineHardwareRepository::getDiscCapacity);
      net = Boolean.TRUE.equals(get("net", () ->
        networkHardwareRepository.pingAddress("www.google.com", 80, 5000)));
      cred = context.user().isRequireAuth();
      wl = readWifiList();
    }

    private <T> T get(String name, Supplier<T> handler) {
      try {
        return handler.get();
      } catch (Exception ex) {
        log.error("Error while get: {}: {}", name, ex.getMessage());
        return null;
      }
    }
  }
}
