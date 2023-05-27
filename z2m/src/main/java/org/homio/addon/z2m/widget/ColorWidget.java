package org.homio.addon.z2m.widget;

import static org.homio.addon.z2m.service.properties.dynamic.Z2MGeneralProperty.SIGNAL;

import java.util.Map;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionDTO.WidgetDefinition;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextWidget.HorizontalAlign;
import org.homio.api.EntityContextWidget.VerticalAlign;
import org.homio.api.exception.ProhibitedExecution;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.service.Z2MProperty;

public class ColorWidget implements WidgetBuilder {

    @Override
    public void buildWidget(WidgetRequest widgetRequest) {
        EntityContext entityContext = widgetRequest.getEntityContext();
        Z2MDeviceEntity entity = widgetRequest.getEntity();
        WidgetDefinition wd = widgetRequest.getWidgetDefinition();

        String layoutID = "lt-clr_" + entity.getIeeeAddress();
        Map<String, Z2MProperty> properties = entity.getDeviceService().getProperties();
        Z2MProperty onOffProperty = properties.get("state");
        Z2MProperty brightnessProperty = properties.get("brightness");
        Z2MProperty colorProperty = properties.get("color");

        entityContext.widget().createLayoutWidget(layoutID, builder -> {
            builder.setBlockSize(2, 1)
                   .setZIndex(wd.getZIndex(20))
                   .setBackground(wd.getBackground())
                   .setLayoutDimension(2, 6);
        });

        if (brightnessProperty != null) {
            entityContext.widget().createSliderWidget("sl_" + entity.getIeeeAddress(), builder -> {
                builder.setBlockSize(wd.getBlockWidth(5), wd.getBlockHeight(1))
                       .setZIndex(wd.getZIndex(20));
                builder.attachToLayout(layoutID, 0, 0);
                builder.addSeries(entity.getModel(), seriesBuilder -> {
                    seriesBuilder.setIcon(entity.getIcon());
                    seriesBuilder.setIconColor(entity.getIconColor());
                    WidgetBuilder.setValueDataSource(seriesBuilder, entityContext, brightnessProperty);
                });
            });
        }

        entityContext.widget().createSimpleColorWidget("clr_" + entity.getIeeeAddress(), builder -> {
            builder
                .setBlockSize(5, 1)
                .setZIndex(wd.getZIndex(20));
            WidgetBuilder.setValueDataSource(builder, entityContext, colorProperty);
            builder.attachToLayout(layoutID, 1, 0);
        });

        if (onOffProperty != null) {
            entityContext.widget().createSimpleToggleWidget("tgl-" + entity.getIeeeAddress(), builder -> {
                WidgetBuilder.setValueDataSource(builder, entityContext, onOffProperty);
                builder.setAlign(HorizontalAlign.right, VerticalAlign.middle);
                builder.attachToLayout(layoutID, 0, 5);
            });
        }

        WidgetBuilder.addProperty(
            entityContext,
            HorizontalAlign.right,
            properties.get(SIGNAL),
            builder -> builder.attachToLayout(layoutID, 1, 5));
    }

    @Override
    public int getWidgetHeight(MainWidgetRequest request) {
        throw new ProhibitedExecution();
    }

    @Override
    public void buildMainWidget(MainWidgetRequest request) {
        throw new ProhibitedExecution();
    }
}
