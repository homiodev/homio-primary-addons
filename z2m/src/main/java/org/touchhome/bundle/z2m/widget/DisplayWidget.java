package org.touchhome.bundle.z2m.widget;

import java.util.Map;
import java.util.function.Consumer;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextWidget.DisplayWidgetBuilder;
import org.touchhome.bundle.api.EntityContextWidget.DisplayWidgetSeriesBuilder;
import org.touchhome.bundle.api.EntityContextWidget.VerticalAlign;
import org.touchhome.bundle.api.entity.zigbee.ZigBeeProperty;
import org.touchhome.bundle.z2m.model.Z2MDeviceEntity;
import org.touchhome.bundle.z2m.service.Z2MProperty;
import org.touchhome.bundle.z2m.service.properties.Z2MPropertyLastUpdate;
import org.touchhome.bundle.z2m.widget.properties.HumidityProperty;
import org.touchhome.bundle.z2m.widget.properties.PropertyBuilder;
import org.touchhome.bundle.z2m.widget.properties.TemperatureProperty;

public class DisplayWidget implements WidgetBuilder {

    Map<String, PropertyBuilder> PROPERTIES = Map.of(
        "temperature", new TemperatureProperty(),
        "humidity", new HumidityProperty()
    );

    @Override
    public void buildWidget(WidgetRequest widgetRequest) {
        EntityContext entityContext = widgetRequest.getEntityContext();
        Z2MDeviceEntity entity = widgetRequest.getEntity();

        int propertiesSize = widgetRequest.getIncludeProperties().size();
        if (propertiesSize == 0) {
            throw new IllegalArgumentException("Unable to find properties: " + widgetRequest.getIncludeProperties() + " from device: " + entity);
        }

        String layoutID = "lt-dsp-" + entity.getIeeeAddress();
        Map<String, Z2MProperty> properties = entity.getDeviceService().getProperties();

        entityContext.widget().createLayoutWidget(layoutID, builder -> {
            builder.setBlockSize(1, 1);
            builder.setLayoutDimension(propertiesSize + 1, 3);
        });
        entityContext.widget().createDisplayWidget("dw-" + entity.getIeeeAddress(), builder -> {
            builder.setPadding(0, 2, 0, 2);
            builder.setBlockSize(3, propertiesSize);
            builder.attachToLayout(layoutID, 0, 0);

            for (ZigBeeProperty property : widgetRequest.getIncludeProperties()) {
                //Z2MProperty z2MProperty = properties.get(property.getName());
                addProperty(entityContext, builder, property, seriesBuilder -> {
                    PropertyBuilder propertyBuilder = PROPERTIES.get(property.getName());
                    if (propertyBuilder != null) {
                        propertyBuilder.build(seriesBuilder);
                    }
                });
            }
        });

        WidgetBuilder.addBatteryProperty(entityContext, layoutID, properties.get("battery"), propertiesSize, 0);
        WidgetBuilder.addLastUpdateProperty(entityContext, layoutID, properties.get(Z2MPropertyLastUpdate.KEY), propertiesSize, 1);
        WidgetBuilder.addLqiProperty(entityContext, layoutID, properties.get("linkquality"), propertiesSize, 2);
    }

    private void addProperty(EntityContext entityContext, DisplayWidgetBuilder builder, ZigBeeProperty property,
        Consumer<DisplayWidgetSeriesBuilder> handler) {
        builder.addSeries(property.getName(), seriesBuilder -> {
            seriesBuilder
                .setName(property.getName())
                .setIcon(property.getIcon())
                .setIconColor(property.getIconColor())
                .setValueDataSource(WidgetBuilder.getSource(entityContext, property, false))
                .setValueTemplate(null, property.getUnit())
                .setValueTemplateSuffixFontSize(0.8)
                .setValueTemplateSuffixVerticalAlign(VerticalAlign.bottom);
            handler.accept(seriesBuilder);
        });
    }
}
