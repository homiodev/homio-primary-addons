package org.touchhome.bundle.raspberry.gpio;

import java.util.Set;
import org.touchhome.bundle.api.model.HasEntityIdentifier;
import org.touchhome.bundle.api.model.HasEntityLog;
import org.touchhome.bundle.api.service.EntityService;

public interface GpioEntity<T extends HasEntityIdentifier>
    extends EntityService<GPIOService, T>, HasEntityLog, HasEntityIdentifier {

  Set<GpioPinEntity> getGpioPinEntities();

  GpioProviderIdModel getGpioProvider();

  int getOneWireInterval();

  String getName();
}
