package org.touchhome.bundle.firmata;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.firmata4j.Pin;
import org.springframework.web.bind.annotation.*;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.json.Option;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.firmata.model.FirmataBaseEntity;
import org.touchhome.bundle.firmata.provider.util.OneWireDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.touchhome.bundle.firmata.workspace.Scratch3FirmataBaseBlock.PIN;
import static org.touchhome.bundle.firmata.workspace.Scratch3FirmataBlocks.FIRMATA_ID_MENU;

@Log4j2
@RestController
@RequestMapping("/rest/firmata")
@RequiredArgsConstructor
public class FirmataController {

    private final EntityContext entityContext;

    @GetMapping("onewire/address")
    public List<Option> getOneWireAddress(
            @RequestParam(name = "family", required = false) Byte family,
            @RequestParam(name = FIRMATA_ID_MENU, required = false) String firmataIdMenu,
            @RequestParam(name = PIN, required = false) String pin) {
        if (firmataIdMenu != null && pin != null) {
            byte pinNum;
            try {
                pinNum = Byte.parseByte(pin);
            } catch (Exception ex) {
                return Collections.emptyList();
            }
            FirmataBaseEntity entity = entityContext.getEntity(firmataIdMenu);
            if (entity != null && entity.getJoined() == Status.ONLINE) {
                entity.getDevice().getIoOneWire().sendOneWireConfig(pinNum, true);

                List<OneWireDevice> devices = entity.getDevice().getIoOneWire().sendOneWireSearch(pinNum);
                if (devices != null) {
                    List<OneWireDevice> ds18B20Devices = devices;
                    if (family != null) {
                        ds18B20Devices = devices.stream().filter(d -> d.isFamily(0x28)).collect(Collectors.toList());
                    }
                    return ds18B20Devices.stream().map(d -> Option.of(String.valueOf(d.getAddress()), d.toString())).collect(Collectors.toList());
                }
            }
        }
        return Collections.emptyList();
    }

    @GetMapping("pin")
    public List<Option> getAllPins(@RequestParam(name = FIRMATA_ID_MENU, required = false) String firmataIdMenu) {
        return this.getPins(null, firmataIdMenu);
    }

    @GetMapping("pin/{mode}")
    public List<Option> getPins(@PathVariable("mode") String mode, @RequestParam(name = FIRMATA_ID_MENU, required = false) String firmataIdMenu) {
        if (firmataIdMenu != null) {
            FirmataBaseEntity entity = entityContext.getEntity(firmataIdMenu);
            if (entity != null && entity.getJoined() == Status.ONLINE) {
                Pin.Mode supportMode = mode == null ? null : Pin.Mode.valueOf(mode);
                List<Option> pins = new ArrayList<>();
                ArrayList<Pin> sortedPins = new ArrayList<>(entity.getDevice().getIoDevice().getPins());
                sortedPins.sort(Comparator.comparingInt(Pin::getIndex));
                for (Pin pin : sortedPins) {
                    if (!pin.getSupportedModes().isEmpty() && (supportMode == null || pin.getSupportedModes().contains(supportMode))) {
                        String name = String.format("%2d / %s", pin.getIndex(), pin.getMode());
                        pins.add(Option.of(String.valueOf(pin.getIndex()), name));
                    }
                }
                return pins;
            }
        }
        return Collections.emptyList();
    }
}
