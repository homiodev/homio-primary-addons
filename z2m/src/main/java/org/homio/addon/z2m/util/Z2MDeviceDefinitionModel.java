package org.homio.addon.z2m.util;

import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Options.Chart;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Options.Pulse;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Options.Threshold;
import org.homio.addon.z2m.widget.WidgetBuilder.MainWidgetRequest;
import org.homio.api.EntityContextVar.VariableType;
import org.homio.api.EntityContextWidget.Fill;
import org.homio.api.EntityContextWidget.PulseColor;
import org.homio.api.EntityContextWidget.Stepped;
import org.homio.api.EntityContextWidget.ToggleType;
import org.homio.api.EntityContextWidget.ValueCompare;
import org.homio.api.entity.widget.AggregationType;
import org.homio.api.model.endpoint.DeviceEndpoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class Z2MDeviceDefinitionModel {

    // for description inside json file only
    private @Nullable String name;
    private @Nullable String icon;
    private @Nullable String iconColor;
    private @Nullable Set<String> models;
    private @Nullable Set<String> exposes;
    private @Nullable List<WidgetDefinition> widgets;
    private @Nullable JsonNode options;

    public enum WidgetType {
        color, toggle, display, compose, line, barTime
    }

    @Setter
    public static class WidgetDefinition {

        // z-index
        private Integer index;
        // block height/width
        private Integer blockWidth;
        private Integer blockHeight;
        // widget height inside layout
        private Integer widgetHeight;
        @Getter
        private @NotNull WidgetType type;
        @Getter
        private @Nullable ColorPicker background;
        @Getter
        private boolean autoDiscovery;
        @Getter
        private @Nullable String leftProperty; // "none" special key to ignore any left property
        @Getter
        private @Nullable String centerProperty;
        @Getter
        private @Nullable String rightProperty;
        private @Nullable List<ItemDefinition> props;
        @Getter
        private @Nullable List<WidgetDefinition> compose;
        private @Nullable String icon;
        // specify ui label(useful in case of 'compose' widget type)
        private @Nullable String name;
        @Getter
        private @Nullable String layout;
        @Getter
        private Options options = new Options();
        @Getter
        private @Nullable List<Requests> requests;
        @Getter
        private Padding padding;

        public @NotNull List<DeviceEndpoint> getProperties(Z2MDeviceEntity z2MDeviceEntity) {
            if (this.isAutoDiscovery()) {
                if (type == WidgetType.toggle) {
                    return z2MDeviceEntity.getDeviceService().getProperties().values().stream()
                                          .filter(p -> p.getExpose().getName().startsWith("state"))
                                          .collect(Collectors.toList());
                }
            }
            Stream<DeviceEndpoint> stream = Stream.empty();
            if (props != null) {
                stream = props.stream().map(p -> z2MDeviceEntity.getDeviceService().getProperties().get(p.getName()));
            }
            if (type == WidgetType.compose) {
                if (getCompose() == null) {
                    throw new IllegalArgumentException("compose type has to have compose array");
                }
                stream = getCompose().stream().flatMap(s -> s.getProperties(z2MDeviceEntity).stream());
            }
            return stream.filter(Objects::nonNull).collect(Collectors.toList());
        }

        public String getName() {
            return StringUtils.defaultIfEmpty(name, type.name());
        }

        public String getIcon() {
            if (icon != null) {return icon;}
            return switch (type) {
                case color -> "fas fa-palette";
                case toggle -> "fas fa-toggle-on";
                case display -> "fas fa-display";
                default -> null;
            };
        }

        public List<DeviceEndpoint> getIncludeProperties(MainWidgetRequest request) {
            Set<String> topIncludeProperties = request.getWidgetRequest().getIncludeProperties().stream()
                                                      .map(DeviceEndpoint::getKey).collect(Collectors.toSet());
            List<DeviceEndpoint> allPossibleProperties = request.getItem().getProperties(request.getWidgetRequest().getEntity());
            return allPossibleProperties.stream()
                                        .filter(zigBeeProperty -> topIncludeProperties.contains(zigBeeProperty.getKey()))
                                        .collect(Collectors.toList());
        }

        public @Nullable Z2MDeviceDefinitionModel.WidgetDefinition.ItemDefinition getProperty(String key) {
            return props == null ? null : props.stream().filter(f -> f.name.equals(key)).findAny().orElse(null);
        }

        public int getBlockWidth(int defaultValue) {
            return Math.max(1, blockWidth == null ? defaultValue : blockWidth);
        }

        public int getBlockHeight(int defaultValue) {
            return Math.max(1, blockHeight == null ? defaultValue : blockHeight);
        }

        public int getWidgetHeight(int defaultValue) {
            return Math.max(1, widgetHeight == null ? defaultValue : widgetHeight);
        }

        public int getZIndex(int defaultValue) {
            return index == null ? defaultValue : index;
        }

        @SneakyThrows
        public static void replaceField(String path, Object value, WidgetDefinition widgetDefinition) {
            String[] split = path.split("\\.");
            if (split.length == 1) {
                FieldUtils.writeDeclaredField(widgetDefinition, split[0], value);
            }
            Object parentObject = widgetDefinition;
            Field cursor = FieldUtils.getDeclaredField(WidgetDefinition.class, split[0], true);
            for (int i = 1; i < split.length; i++) {
                parentObject = FieldUtils.readField(cursor, parentObject, true);
                cursor = FieldUtils.getDeclaredField(parentObject.getClass(), split[i], true);
            }
            FieldUtils.writeField(cursor, parentObject, value, true);
        }

        @Getter
        @Setter
        public static class ItemDefinition {

            private String name;
            private IconPicker icon;
            private ColorPicker iconColor;
            private String valueConverter;
            private String valueColor;
            private boolean valueSourceClickHistory = true;
            private int valueConverterRefreshInterval = 0;
            private Chart chart;
        }
    }

    @Getter
    @Setter
    public static class IconPicker {

        private String value;
        private List<Threshold> thresholds;
    }

    @Getter
    @Setter
    public static class ColorPicker {

        private String value;
        private List<Threshold> thresholds;
        private List<Pulse> pulses;
    }

    @Getter
    @Setter
    public static class Requests {

        private String name;
        private String value;
        private RequestType type;
        private String title;
        private String target;
        private float min = 0;
        private float max = 255;

        public enum RequestType {
            number
        }
    }

    @Getter
    @Setter
    public static class Padding {

        private int top;
        private int right;
        private int bottom;
        private int left;
    }

    @Getter
    @Setter
    public static class Options {

        // For chart
        private int pointsPerHour = 60;
        private double pointRadius = 0D;
        private boolean showDynamicLine = false;
        private String dynamicLineColor;
        private String pointBorderColor;
        private ToggleType toggleType = ToggleType.Regular;

        private boolean showAxisX = true;
        private boolean showAxisY = true;
        private boolean showChartFullScreenButton = true;

        // for display fire push value
        private Source pushSource;
        private String valueOnClick;
        private String valueOnDoubleClick;
        private String valueOnHoldClick;
        private String valueOnHoldReleaseClick;
        private String pushConfirmMessage;
        private Chart chart;

        public Boolean showAllButton;

        @Getter
        @Setter
        public static class Pulse {

            private ValueCompare op;
            private Object value;
            private PulseColor color;
            private Source source;
        }

        @Getter
        @Setter
        public static class Threshold {

            private ValueCompare op;
            private Object value;
            private String target;
            private Source source;
        }

        @Getter
        @Setter
        public static class Chart {

            private Source source;
            private String color;
            private Stepped stepped = Stepped.False;
            private Fill fill = Fill.Origin;
            private int lineBorderWidth = 2;
            private AggregationType aggregateFunc = AggregationType.AverageNoZero;
            private int opacity = 50;
            private int height = 30;
            private boolean smoothing = true;
            private Integer min;
            private Integer max;
            private boolean fillEmptyValues;
        }

        @Getter
        @Setter
        public static class Source {

            private SourceType kind;
            private String value;
            private VariableType variableType;

            public enum SourceType {
                broadcasts, property, variable
            }
        }
    }

    @Getter
    @Setter
    public static class ModelGroups {
        private String name;
        private Set<String> models;
    }
}
