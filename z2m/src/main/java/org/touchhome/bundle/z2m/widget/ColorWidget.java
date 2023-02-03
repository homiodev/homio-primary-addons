package org.touchhome.bundle.z2m.widget;

import java.util.Map;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.z2m.model.Z2MDeviceEntity;
import org.touchhome.bundle.z2m.service.Z2MProperty;

public class ColorWidget implements WidgetBuilder {

    @Override
    public void buildWidget(EntityContext entityContext, Z2MDeviceEntity entity, String tab) {
        entityContext.widget().createColorWidget("clr_" + entity.getIeeeAddress(),
            entity.getName(), builder -> {
                builder.setBlockSize(2, 1);
                builder.setName(entity.getModel());
                builder.setIcon(entity.getIcon());
                builder.setIconColor(entity.getIconColor());
                Map<String, Z2MProperty> properties = entity.getDeviceService().getProperties();

                Z2MProperty colorTempProperty = properties.get("color_temp");
                if (colorTempProperty != null) {
                    builder.setColorTemperatureValueDataSource(getSource(entityContext, colorTempProperty));
                    if (colorTempProperty.getExpose().getValueMin() != null) {
                        builder.setColorTemperatureMinValue(colorTempProperty.getExpose().getValueMin());
                    }
                    if (colorTempProperty.getExpose().getValueMax() != null) {
                        builder.setColorTemperatureMaxValue(colorTempProperty.getExpose().getValueMax());
                    }
                }

                Z2MProperty brightnessProperty = properties.get("brightness");
                if (brightnessProperty != null) {
                    builder.setBrightnessValueDataSource(getSource(entityContext, brightnessProperty));
                    if (brightnessProperty.getExpose().getValueMin() != null) {
                        builder.setBrightnessMinValue(brightnessProperty.getExpose().getValueMin());
                    }
                    if (brightnessProperty.getExpose().getValueMax() != null) {
                        builder.setBrightnessMaxValue(brightnessProperty.getExpose().getValueMax());
                    }
                }

                Z2MProperty onOffProperty = properties.get("state");
                if (onOffProperty != null) {
                    builder.setOnOffValueDataSource(getSource(entityContext, onOffProperty));
                }

                Z2MProperty colorProperty = properties.get("color");
                if (colorProperty != null) {
                    builder.setColorValueDataSource(getSource(entityContext, colorProperty));
                }
            });
    }

    private String getSource(EntityContext entityContext, Z2MProperty property) {
        return entityContext.var().buildDataSource(property.getVariableId());
    }
}
