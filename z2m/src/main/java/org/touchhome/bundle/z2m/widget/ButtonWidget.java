package org.touchhome.bundle.z2m.widget;

import static java.lang.String.format;
import static org.touchhome.bundle.api.ui.field.UIFieldLayout.HorizontalAlign.center;
import static org.touchhome.bundle.api.ui.field.UIFieldLayout.HorizontalAlign.left;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextWidget.ToggleType;
import org.touchhome.bundle.api.EntityContextWidget.VerticalAlign;
import org.touchhome.bundle.api.entity.zigbee.ZigBeeProperty;
import org.touchhome.bundle.api.ui.UI;
import org.touchhome.bundle.api.ui.field.UIFieldLayout;
import org.touchhome.bundle.z2m.model.Z2MDeviceEntity;
import org.touchhome.bundle.z2m.service.Z2MProperty;

public class ButtonWidget implements WidgetBuilder {

    @Override
    public void buildWidget(WidgetRequest widgetRequest) {
        EntityContext entityContext = widgetRequest.getEntityContext();
        Z2MDeviceEntity entity = widgetRequest.getEntity();

        int propertiesSize = widgetRequest.getIncludeProperties().size();
        if (propertiesSize == 0) {
            throw new IllegalArgumentException("Unable to find properties: " + widgetRequest.getIncludeProperties() + " from device: " + entity);
        }

        String layoutID = "lt-btn-" + entity.getIeeeAddress();
        entityContext.widget().createLayoutWidget(layoutID, builder -> {
            builder.setBlockSize(1, 1);
            builder.setLayoutDimension(4, 2);
        });
        Map<String, Z2MProperty> properties = entity.getDeviceService().getProperties();

        addLastRowProperties(entityContext, layoutID, properties);

        entityContext.widget().createToggleWidget("tgl_" + entity.getEntityID(), builder -> {
            builder.setDisplayType(ToggleType.Regular)
                   .setLayout(UIFieldLayout.LayoutBuilder.builder(50, 50).addRow(
                       rowBuilder -> rowBuilder.addCol("name", left).addCol("button", center)
                   ).build());
            builder.setBlockSize(2, 3);
            builder.attachToLayout(layoutID, 0, 0);

            for (ZigBeeProperty property : widgetRequest.getIncludeProperties()) {
                Z2MProperty z2MProperty = properties.get(property.getEntityID());
                builder.addSeries(getName(entity, z2MProperty), seriesBuilder ->
                    WidgetBuilder.setValueDataSource(seriesBuilder, entityContext, z2MProperty)
                                 .setColor(UI.Color.random()));
            }
        });
    }

    private void addLastRowProperties(EntityContext entityContext, String layoutID, Map<String, Z2MProperty> properties) {
        Z2MProperty leftProperty = getLeftProperty(properties);
        if (leftProperty != null) {
            entityContext.widget().createSimpleValueWidget(leftProperty.getEntityID(), builder ->
                builder.setIcon(leftProperty.getIcon())
                       .setValueDataSource(WidgetBuilder.getSource(entityContext, leftProperty, false))
                       .setValueTemplateFontSize(0.8)
                       .setValueTemplate("", leftProperty.getExpose().getUnit())
                       .setValueTemplateSuffixFontSize(0.6)
                       .setValueTemplateSuffixVerticalAlign(VerticalAlign.bottom)
                       .attachToLayout(layoutID, 3, 0)
                       .setIconColor(leftProperty.getIconColor()));
        }

        WidgetBuilder.addLqiProperty(entityContext, layoutID, properties.get("linkquality"), 3, 1);
    }

    private @Nullable Z2MProperty getLeftProperty(Map<String, Z2MProperty> properties) {
        for (String property : new String[]{"energy", "consumption", "power"}) {
            if (properties.containsKey(property)) {
                return properties.get(property);
            }
        }
        return null;
    }

    private String getName(Z2MDeviceEntity entity, Z2MProperty state) {
        if (StringUtils.isNotEmpty(entity.getPlace())) {
            return entity.getPlace() + format("[%s]", state.getShortName());
        }
        return state.getShortName();
    }
}
