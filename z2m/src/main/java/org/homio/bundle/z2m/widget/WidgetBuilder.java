package org.homio.bundle.z2m.widget;

import static org.homio.bundle.z2m.service.properties.Z2MPropertyLastUpdate.UPDATED;
import static org.homio.bundle.z2m.service.properties.dynamic.Z2MGeneralProperty.SIGNAL;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.EntityContextWidget.HasSetSingleValueDataSource;
import org.homio.bundle.api.EntityContextWidget.HasSingleValueDataSource;
import org.homio.bundle.api.EntityContextWidget.HorizontalAlign;
import org.homio.bundle.api.EntityContextWidget.SimpleValueWidgetBuilder;
import org.homio.bundle.api.EntityContextWidget.VerticalAlign;
import org.homio.bundle.api.EntityContextWidget.WidgetBaseBuilder;
import org.homio.bundle.api.entity.zigbee.ZigBeeProperty;
import org.homio.bundle.z2m.model.Z2MDeviceEntity;
import org.homio.bundle.z2m.service.Z2MProperty;
import org.homio.bundle.z2m.util.Z2MDeviceDefinitionDTO;
import org.homio.bundle.z2m.util.Z2MDeviceDefinitionDTO.WidgetDefinition;
import org.homio.bundle.z2m.util.Z2MDeviceDefinitionDTO.WidgetType;
import org.homio.bundle.z2m.widget.properties.BatteryIconBuilder;
import org.homio.bundle.z2m.widget.properties.HumidityIconBuilder;
import org.homio.bundle.z2m.widget.properties.IconBuilder;
import org.homio.bundle.z2m.widget.properties.SignalIconBuilder;
import org.homio.bundle.z2m.widget.properties.TemperatureIconBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WidgetBuilder {

    Map<WidgetType, WidgetBuilder> WIDGETS = Map.of(
        WidgetType.color, new ColorWidget(),
        WidgetType.toggle, new ToggleWidget(),
        WidgetType.display, new DisplayWidget(),
        WidgetType.compose, new ComposeWidget(),
        WidgetType.barTime, new BarTimeWidget(),
        WidgetType.line, new LineWidget()
    );

    Map<String, IconBuilder> PROPERTIES = Map.of(
        "temperature", new TemperatureIconBuilder(),
        "humidity", new HumidityIconBuilder(),
        "battery", new BatteryIconBuilder(),
        SIGNAL, new SignalIconBuilder()
    );

    void buildWidget(WidgetRequest widgetRequest);

    static void addProperty(
        @NotNull EntityContext entityContext,
        @NotNull HorizontalAlign horizontalAlign,
        @Nullable ZigBeeProperty property,
        @NotNull Consumer<SimpleValueWidgetBuilder> attachHandler) {
        if (property != null) {
            switch (property.getKey()) {
                case UPDATED:
                    createSimpleProperty(entityContext, horizontalAlign, property, builder -> {
                        builder.setValueConverter("return Math.floor((new Date().getTime() - value) / 60000) + 'm';");
                        builder.setValueConverterRefreshInterval(60);
                        attachHandler.accept(builder);
                    });
                    break;
                default:
                    createSimpleProperty(entityContext, horizontalAlign, property, attachHandler);
            }
        }
    }

    static String getSource(EntityContext entityContext, ZigBeeProperty property, boolean forSet) {
        return entityContext.var().buildDataSource(property.getVariableID(), forSet);
    }

    static <T extends HasSingleValueDataSource<?> & HasSetSingleValueDataSource<?>> T
    setValueDataSource(T builder, EntityContext entityContext, Z2MProperty property) {
        builder.setValueDataSource(WidgetBuilder.getSource(entityContext, property, false));
        builder.setSetValueDataSource(WidgetBuilder.getSource(entityContext, property, true));
        return builder;
    }

    /**
     * Get total number of units for widget height
     */
    int getWidgetHeight(MainWidgetRequest request);

    void buildMainWidget(MainWidgetRequest request);

    private static void createSimpleProperty(
        @NotNull EntityContext entityContext,
        @NotNull HorizontalAlign horizontalAlign,
        @NotNull ZigBeeProperty property,
        @NotNull Consumer<SimpleValueWidgetBuilder> attachHandler) {
        entityContext.widget().createSimpleValueWidget(property.getEntityID(), builder -> {
            builder.setIcon(property.getIcon())
                   .setValueDataSource(getSource(entityContext, property, false))
                   .setAlign(horizontalAlign, VerticalAlign.bottom)
                   .setValueFontSize(0.8)
                   .setIconColor(property.getIconColor());
            Optional.ofNullable(PROPERTIES.get(property.getKey())).ifPresent(ib -> ib.build(builder));
            attachHandler.accept(builder);
        });
    }

    @Getter
    @AllArgsConstructor
    class WidgetRequest {

        private final @NotNull EntityContext entityContext;
        private final @NotNull Z2MDeviceEntity entity;
        private final @NotNull String tab;
        private final @NotNull Z2MDeviceDefinitionDTO.WidgetDefinition widgetDefinition;
        private final @NotNull List<ZigBeeProperty> includeProperties;
    }

    @Getter
    @AllArgsConstructor
    class MainWidgetRequest {

        private final WidgetRequest widgetRequest;
        private final WidgetDefinition item;
        // total number of columns in layout
        private final int layoutColumnNum;
        private final int layoutRowNum;
        private Consumer<WidgetBaseBuilder> attachToLayoutHandler;

        public List<ZigBeeProperty> getItemIncludeProperties() {
            return item.getIncludeProperties(this);
        }
    }
}
