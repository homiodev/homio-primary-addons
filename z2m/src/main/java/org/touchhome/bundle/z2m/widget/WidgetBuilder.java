package org.touchhome.bundle.z2m.widget;

import java.util.Map;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.z2m.model.Z2MDeviceEntity;

public interface WidgetBuilder {

    Map<String, WidgetBuilder> WIDGETS = Map.of("color", new ColorWidget());

    void buildWidget(EntityContext entityContext, Z2MDeviceEntity entity, String tab);
}
