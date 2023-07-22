package org.homio.addon.z2m.widget;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.homio.addon.z2m.service.Z2MProperty.PROPERTY_BATTERY;
import static org.homio.addon.z2m.service.Z2MProperty.PROPERTY_LAST_SEEN;
import static org.homio.addon.z2m.service.properties.inline.Z2MPropertyGeneral.PROPERTY_SIGNAL;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.ColorPicker;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.IconPicker;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Options.Pulse;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Options.Source;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Options.Threshold;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Padding;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition.ItemDefinition;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetType;
import org.homio.addon.z2m.widget.properties.BatteryIconBuilder;
import org.homio.addon.z2m.widget.properties.HumidityIconBuilder;
import org.homio.addon.z2m.widget.properties.IconBuilder;
import org.homio.addon.z2m.widget.properties.LastSeenIconBuilder;
import org.homio.addon.z2m.widget.properties.SignalIconBuilder;
import org.homio.addon.z2m.widget.properties.TemperatureIconBuilder;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextVar.VariableType;
import org.homio.api.EntityContextWidget.HasIcon;
import org.homio.api.EntityContextWidget.HasName;
import org.homio.api.EntityContextWidget.HasPadding;
import org.homio.api.EntityContextWidget.HasSetSingleValueDataSource;
import org.homio.api.EntityContextWidget.HasSingleValueDataSource;
import org.homio.api.EntityContextWidget.HorizontalAlign;
import org.homio.api.EntityContextWidget.PulseBuilder;
import org.homio.api.EntityContextWidget.SimpleValueWidgetBuilder;
import org.homio.api.EntityContextWidget.ThresholdBuilder;
import org.homio.api.EntityContextWidget.VerticalAlign;
import org.homio.api.EntityContextWidget.WidgetBaseBuilder;
import org.homio.api.entity.zigbee.ZigBeeProperty;
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
        PROPERTY_BATTERY, new BatteryIconBuilder(),
        PROPERTY_SIGNAL, new SignalIconBuilder(),
        PROPERTY_LAST_SEEN, new LastSeenIconBuilder()
    );

    void buildWidget(WidgetRequest widgetRequest);

    static void addProperty(
        @NotNull EntityContext entityContext,
        @NotNull HorizontalAlign horizontalAlign,
        @Nullable ZigBeeProperty property,
        boolean addUnit,
        @NotNull Consumer<SimpleValueWidgetBuilder> attachHandler) {
        if (property != null) {
            if (PROPERTY_LAST_SEEN.equals(property.getKey())) {
                createSimpleProperty(entityContext, horizontalAlign, property, builder -> {
                    builder.setValueConverter("return Math.floor((new Date().getTime() - value) / 60000);");
                    builder.setValueConverterRefreshInterval(60);
                    buildValueSuffix(builder, "m");
                    attachHandler.accept(builder);
                }, false);
            } else {
                createSimpleProperty(entityContext, horizontalAlign, property, attachHandler, addUnit);
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
        @NotNull Consumer<SimpleValueWidgetBuilder> attachHandler,
        boolean addUnit) {
        entityContext.widget().createSimpleValueWidget(property.getEntityID(), builder -> {
            builder.setIcon(property.getIcon())
                   .setValueDataSource(getSource(entityContext, property, false))
                   .setAlign(horizontalAlign, VerticalAlign.bottom)
                   .setValueFontSize(0.8);
            if (addUnit) {
                buildValueSuffix(builder, property.getUnit());
            }
            Optional.ofNullable(PROPERTIES.get(property.getKey())).ifPresent(ib -> ib.build(builder));
            attachHandler.accept(builder);
        });
    }

    private static void buildValueSuffix(SimpleValueWidgetBuilder builder, @Nullable String value) {
        builder.setValueTemplate(null, value)
               .setValueSuffixFontSize(0.6)
               .setValueSuffixColor("#777777")
               .setValueSuffixVerticalAlign(VerticalAlign.bottom);
    }

    @Getter
    @AllArgsConstructor
    class WidgetRequest {

        private final @NotNull EntityContext entityContext;
        private final @NotNull Z2MDeviceEntity entity;
        private final @NotNull String tab;
        private final @NotNull Z2MDeviceDefinitionModel.WidgetDefinition widgetDefinition;
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

    static void buildCommon(WidgetDefinition wd, WidgetRequest widgetRequest, WidgetBaseBuilder builder) {
        buildCommon(wd, widgetRequest, builder, 20);
    }

    static void buildCommon(WidgetDefinition wd, WidgetRequest widgetRequest, WidgetBaseBuilder builder, Integer defaultZIndex) {
        buildBackground(wd.getBackground(), widgetRequest, builder);
        builder.setZIndex(wd.getZIndex(defaultZIndex));
        if (builder instanceof HasName<?> nameBuilder) {
            nameBuilder.setName(widgetRequest.getEntity().getDescription());
            nameBuilder.setShowName(false);
        }
        Padding padding = wd.getPadding();
        if (padding != null && builder instanceof HasPadding<?> paddingBuilder) {
            paddingBuilder.setPadding(padding.getTop(), padding.getRight(),
                padding.getBottom(), padding.getLeft());
        }
    }

    static void buildBackground(ColorPicker background, WidgetRequest widgetRequest, WidgetBaseBuilder builder) {
        if (background == null) {
            return;
        }
        builder.setBackground(background.getValue(),
            (Consumer<ThresholdBuilder>)
                thresholdBuilder -> buildThreshold(widgetRequest, background.getThresholds(), thresholdBuilder),
            (Consumer<PulseBuilder>) pulseBuilder ->
                buildPulseThreshold(widgetRequest, background.getPulses(), pulseBuilder));
    }

    static void buildIconAndColor(ZigBeeProperty property, HasIcon iconBuilder,
        ItemDefinition wbProperty, WidgetRequest widgetRequest) {
        iconBuilder.setIcon(property.getIcon());

        if (wbProperty == null) {
            return;
        }
        IconPicker icon = wbProperty.getIcon();
        if (icon != null) {
            iconBuilder.setIcon(defaultString(icon.getValue(), property.getIcon().getIcon()),
                (Consumer<ThresholdBuilder>) iconThresholdBuilder ->
                    WidgetBuilder.buildThreshold(widgetRequest, icon.getThresholds(), iconThresholdBuilder));
            ColorPicker color = wbProperty.getIconColor();
            if (color == null || (isEmpty(color.getValue()) && color.getThresholds() == null)) {
                return;
            }
            iconBuilder.setIconColor(defaultString(color.getValue(), property.getIcon().getColor()),
                (Consumer<ThresholdBuilder>) thresholdBuilder ->
                    WidgetBuilder.buildThreshold(widgetRequest, color.getThresholds(), thresholdBuilder));
        }
    }

    static void buildThreshold(WidgetRequest widgetRequest, List<Threshold> thresholds, ThresholdBuilder thresholdBuilder) {
        if (thresholds != null) {
            for (Threshold threshold : thresholds) {
                thresholdBuilder.setThreshold(
                    threshold.getTarget(),
                    threshold.getValue(),
                    threshold.getOp(),
                    buildDataSource(widgetRequest.getEntity(), widgetRequest.getEntityContext(), threshold.getSource()));
            }
        }
    }

    static void buildPulseThreshold(WidgetRequest widgetRequest, List<Pulse> pulses, PulseBuilder pulseThresholdBuilder) {
        if (pulses != null) {
            for (Pulse pulse : pulses) {
                pulseThresholdBuilder.setPulse(
                    pulse.getColor(),
                    pulse.getValue(),
                    pulse.getOp(),
                    buildDataSource(widgetRequest.getEntity(), widgetRequest.getEntityContext(), pulse.getSource()));
            }
        }
    }

    static String buildDataSource(Z2MDeviceEntity entity, EntityContext entityContext, Source source) {
        switch (source.getKind()) {
            case variable -> {
                String variable = entityContext.var().createVariable(entity.getEntityID(),
                    source.getValue(), source.getValue(), source.getVariableType(), null);
                return entityContext.var().buildDataSource(variable, true);
            }
            case broadcasts -> {
                String id = source.getValue() + "_" + entity.getIeeeAddress();
                String name = source.getValue() + " " + entity.getIeeeAddress();
                String variableID = entityContext.var().createVariable("broadcasts", id, name, VariableType.Any, null);
                return entityContext.var().buildDataSource(variableID, true);
            }
            case property -> {
                ZigBeeProperty property = entity.getProperty(source.getValue());
                if (property == null) {
                    throw new IllegalArgumentException("Unable to find z2m property: " + source.getValue() +
                        " for device: " + entity);
                }
                return WidgetBuilder.getSource(entityContext, property, true);
            }
            default -> throw new IllegalArgumentException("Unable to find handler for type: " + source.getKind());
        }
    }
}
