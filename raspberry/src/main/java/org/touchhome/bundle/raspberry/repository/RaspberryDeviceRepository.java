package org.touchhome.bundle.raspberry.repository;

import static org.touchhome.bundle.raspberry.entity.RaspberryDeviceEntity.DEFAULT_DEVICE_ENTITY_ID;

import com.pi4j.io.gpio.PinMode;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.repository.AbstractRepository;
import org.touchhome.bundle.api.util.RaspberryGpioPin;
import org.touchhome.bundle.api.workspace.HasWorkspaceVariableLinkAbility;
import org.touchhome.bundle.raspberry.RaspberryGPIOService;
import org.touchhome.bundle.raspberry.entity.RaspberryDeviceEntity;
import org.touchhome.bundle.raspberry.workspace.Scratch3RaspberryBlocks;

@Log4j2
@Repository
public class RaspberryDeviceRepository extends AbstractRepository<RaspberryDeviceEntity>
    implements HasWorkspaceVariableLinkAbility {

  private final Scratch3RaspberryBlocks scratch3RaspberryBlocks;
  private final RaspberryGPIOService raspberryGPIOService;
  private final EntityContext entityContext;

  public RaspberryDeviceRepository(Scratch3RaspberryBlocks scratch3RaspberryBlocks, RaspberryGPIOService raspberryGPIOService,
      EntityContext entityContext) {
    super(RaspberryDeviceEntity.class);
    this.scratch3RaspberryBlocks = scratch3RaspberryBlocks;
    this.raspberryGPIOService = raspberryGPIOService;
    this.entityContext = entityContext;
  }

  @Override
  @SneakyThrows
  public void createVariable(String entityID, String varGroup, String varName, String key) {
    RaspberryGpioPin raspberryGpioPin =
        RaspberryGpioPin.values(PinMode.DIGITAL_INPUT, null).stream().filter(p -> p.name().equals(key)).findAny()
            .orElse(null);
    if (raspberryGpioPin != null) {
      JSONObject parameter = new JSONObject().put("pin", raspberryGpioPin).put("entityID", entityID);
      scratch3RaspberryBlocks.getIsGpioInState().getLinkGenerator().handle(varGroup, varName, parameter);
    }
  }

  @SneakyThrows
  @Transactional
  public void ensureDeviceExists() {
    if (getByEntityID(DEFAULT_DEVICE_ENTITY_ID) == null) {
      log.info("Save default pi device");
      save(new RaspberryDeviceEntity().computeEntityID(() -> DEFAULT_DEVICE_ENTITY_ID));
    }
  }
}
