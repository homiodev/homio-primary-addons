package org.touchhome.bundle.z2m.widget;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextWidget.HasSingleValueDataSource;
import org.touchhome.bundle.api.EntityContextWidget.HorizontalAlign;
import org.touchhome.bundle.api.EntityContextWidget.ThresholdBuilder.ValueCompare;
import org.touchhome.bundle.api.EntityContextWidget.VerticalAlign;
import org.touchhome.bundle.api.entity.zigbee.ZigBeeProperty;
import org.touchhome.bundle.api.ui.UI.Color;
import org.touchhome.bundle.z2m.model.Z2MDeviceEntity;
import org.touchhome.bundle.z2m.service.Z2MProperty;
import org.touchhome.bundle.z2m.util.Z2MDeviceDefinitionDTO.WidgetDefinition;

public interface WidgetBuilder {

    Map<String, WidgetBuilder> WIDGETS = Map.of(
        "color", new ColorWidget(),
        "button", new ButtonWidget(),
        "display", new DisplayWidget()
    );

    void buildWidget(WidgetRequest widgetRequest);

    static void addBatteryProperty(EntityContext entityContext, String layoutID, Z2MProperty property, int rowNum, int colNum) {
        entityContext.widget().createSimpleValueWidget(property.getEntityID(), builder ->
            builder.setIcon("fas fa-battery-full", thresholdBuilder ->
                       thresholdBuilder.setThreshold("fas fa-battery-three-quarters", 20, ValueCompare.lt)
                                       .setThreshold("fas fa-battery-half", 15, ValueCompare.lt)
                                       .setThreshold("fas fa-battery-quarter", 10, ValueCompare.lt)
                                       .setThreshold("fas fa-battery-empty", 5, ValueCompare.lt))
                   .setValueDataSource(getSource(entityContext, property, false))
                   .setAlign(HorizontalAlign.left, VerticalAlign.bottom)
                   .setValueTemplateFontSize(0.8)
                   .attachToLayout(layoutID, rowNum, colNum)
                   .setIconColor(Color.BLUE, colorBuilder ->
                       colorBuilder.setThreshold(Color.WARNING, 15, ValueCompare.lt)
                                   .setThreshold(Color.RED, 10, ValueCompare.lt)));
    }

    static void addLqiProperty(EntityContext entityContext, String layoutID, Z2MProperty property, int rowNum, int colNum) {
        entityContext.widget().createSimpleValueWidget(property.getEntityID(), builder ->
            builder.setIcon(property.getIcon())
                   .setValueDataSource(getSource(entityContext, property, false))
                   .setAlign(HorizontalAlign.right, VerticalAlign.bottom)
                   .setValueTemplateFontSize(0.8)
                   .attachToLayout(layoutID, rowNum, colNum)
                   .setIconColor(Color.GREEN, colorBuilder ->
                       colorBuilder.setThreshold(Color.WARNING, 50, ValueCompare.lt)
                                   .setThreshold(Color.RED, 0, ValueCompare.eq)));
    }

    static String getSource(EntityContext entityContext, ZigBeeProperty property, boolean forSet) {
        return entityContext.var().buildDataSource(property.getVariableID(), forSet);
    }

    static void addLastUpdateProperty(EntityContext entityContext, String layoutID, Z2MProperty property, int rowNum, int colNum) {
        entityContext.widget().createSimpleValueWidget(property.getEntityID(), builder ->
            builder.setIcon(property.getIcon())
                   .setValueDataSource(getSource(entityContext, property, false))
                   .setAlign(HorizontalAlign.center, VerticalAlign.bottom)
                   .setValueTemplateFontSize(0.8)
                   .setValueConverter("return Math.floor((new Date().getTime() - value) / 60000) + 'm';")
                   .setValueConverterRefreshInterval(60)
                   .attachToLayout(layoutID, rowNum, colNum));
    }

    static <T extends HasSingleValueDataSource<?>> T setValueDataSource(T builder, EntityContext entityContext, Z2MProperty property) {
        builder.setValueDataSource(WidgetBuilder.getSource(entityContext, property, false));
        builder.setSetValueDataSource(WidgetBuilder.getSource(entityContext, property, true));
        return builder;
    }

    @Getter
    @AllArgsConstructor
    class WidgetRequest {

        private final EntityContext entityContext;
        private final Z2MDeviceEntity entity;
        private final String tab;
        private final WidgetDefinition widgetDefinition;
        private final List<ZigBeeProperty> includeProperties;
    }
}
