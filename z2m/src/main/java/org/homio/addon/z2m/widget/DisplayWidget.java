package org.homio.addon.z2m.widget;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.homio.addon.z2m.service.properties.Z2MPropertyLastUpdate.UPDATED;
import static org.homio.addon.z2m.service.properties.dynamic.Z2MGeneralProperty.SIGNAL;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Options.Chart;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Options.Source;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition.ItemDefinition;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextVar.VariableType;
import org.homio.api.EntityContextWidget.AnimateColor;
import org.homio.api.EntityContextWidget.DisplayWidgetBuilder;
import org.homio.api.EntityContextWidget.DisplayWidgetSeriesBuilder;
import org.homio.api.EntityContextWidget.HasChartDataSource;
import org.homio.api.EntityContextWidget.HasLineChartBehaviour;
import org.homio.api.EntityContextWidget.HorizontalAlign;
import org.homio.api.EntityContextWidget.ThresholdBuilder;
import org.homio.api.EntityContextWidget.ValueCompare;
import org.homio.api.EntityContextWidget.VerticalAlign;
import org.homio.api.entity.zigbee.ZigBeeProperty;
import org.homio.api.ui.UI;

@SuppressWarnings("rawtypes")
public class DisplayWidget implements WidgetBuilder {

    @Override
    public void buildWidget(WidgetRequest widgetRequest) {
        EntityContext entityContext = widgetRequest.getEntityContext();
        Z2MDeviceEntity entity = widgetRequest.getEntity();
        WidgetDefinition wd = widgetRequest.getWidgetDefinition();

        int propertiesSize = widgetRequest.getIncludeProperties().size();
        if (propertiesSize == 0) {
            throw new IllegalArgumentException("Unable to find display properties for device: " + entity);
        }

        String layoutID = "lt-dsp-" + entity.getIeeeAddress();
        Map<String, Z2MProperty> properties = entity.getDeviceService().getProperties();

        entityContext.widget().createLayoutWidget(layoutID, builder ->
            builder.setBlockSize(wd.getBlockWidth(1), wd.getBlockHeight(1))
                   .setZIndex(wd.getZIndex(20))
                   .setBackground(wd.getBackground())
                   .setLayoutDimension(propertiesSize + 1, 3));
        var request = new MainWidgetRequest(widgetRequest, wd, 3,
            propertiesSize + 1, builder -> builder.attachToLayout(layoutID, 0, 0));
        buildMainWidget(request);

        WidgetBuilder.addProperty(
            entityContext,
            HorizontalAlign.left,
            properties.get("battery"),
            builder -> builder.attachToLayout(layoutID, propertiesSize, 0));

        WidgetBuilder.addProperty(
            entityContext,
            HorizontalAlign.center,
            properties.get(UPDATED),
            builder -> builder.attachToLayout(layoutID, propertiesSize, 1));

        WidgetBuilder.addProperty(
            entityContext,
            HorizontalAlign.right,
            properties.get(SIGNAL),
            builder -> builder.attachToLayout(layoutID, propertiesSize, 2));
    }

    @Override
    public void buildMainWidget(MainWidgetRequest request) {
        EntityContext entityContext = request.getWidgetRequest().getEntityContext();
        Z2MDeviceEntity entity = request.getWidgetRequest().getEntity();

        List<ZigBeeProperty> includeProperties = request.getItemIncludeProperties();
        if (includeProperties.isEmpty()) {
            throw new IllegalArgumentException("Unable to find display properties for device: " + request.getWidgetRequest().getEntity());
        }

        WidgetDefinition wd = request.getItem();
        entityContext.widget().createDisplayWidget("dw-" + entity.getIeeeAddress(), builder -> {
            builder.setPadding(0, 2, 0, 2);
            buildPushValue(request, builder, entityContext);

            buildBackground(request, builder);

            String layout = wd.getLayout();
            if (isNotEmpty(layout)) {
                builder.setLayout(layout);
            }
            builder.setBlockSize(wd.getBlockWidth(3), wd.getBlockHeight(includeProperties.size()))
                   .setZIndex(wd.getZIndex(20));
            request.getAttachToLayoutHandler().accept(builder);

            for (ZigBeeProperty property : includeProperties) {
                addProperty(entityContext, request.getItem(), builder, property, seriesBuilder ->
                    Optional.ofNullable(PROPERTIES.get(property.getKey())).ifPresent(ib -> ib.build(seriesBuilder)));
            }

            Chart chart = wd.getOptions().getChart();
            if (chart != null) {
                builder.setChartDataSource(buildDataSource(request, entityContext, chart.getSource()));
                builder.setChartHeight(chart.getHeight());
                fillHasChartDataSource(builder, chart);
                fillHasLineChartBehaviour(builder, chart);
            }
        });
    }

    @Override
    public int getWidgetHeight(MainWidgetRequest request) {
        return request.getItem().getWidgetHeight(request.getItemIncludeProperties().size());
    }

    public static void fillHasLineChartBehaviour(HasLineChartBehaviour builder, Chart chart) {
        builder.setStepped(chart.getStepped());
        builder.setLineFill(chart.getFill());
        builder.setLineBorderWidth(chart.getLineBorderWidth());
        builder.setMin(chart.getMin());
        builder.setMax(chart.getMax());
    }

    public static void fillHasChartDataSource(HasChartDataSource builder, Chart chart) {
        builder.setChartColorOpacity(chart.getOpacity());
        builder.setChartAggregationType(chart.getAggregateFunc());
        builder.setSmoothing(chart.isSmoothing());
        builder.setChartColor(StringUtils.defaultIfEmpty(chart.getColor(), UI.Color.random()));
        builder.setFillEmptyValues(chart.isFillEmptyValues());
    }

    private void buildBackground(MainWidgetRequest request, DisplayWidgetBuilder builder) {
        builder.setBackground(request.getItem().getBackground(), thresholdBuilder -> {

        }, animateBuilder -> {
            JsonNode animation = request.getItem().getOptions().getAnimation();
            if (animation != null) {
                JsonNode value = animation.get("value");
                Object rawValue = value.isNumber() ? value.numberValue() : value.isBoolean() ? value.asBoolean() : value.asText();
                animateBuilder.setAnimate(
                    AnimateColor.valueOf(animation.get("color").asText()),
                    rawValue,
                    ValueCompare.valueOf(animation.get("op").asText())
                );
            }
        });
    }

    private void buildPushValue(MainWidgetRequest request, DisplayWidgetBuilder builder, EntityContext entityContext) {
        builder.setValueOnClick(request.getItem().getOptions().getValueOnClick());
        builder.setValueOnDoubleClick(request.getItem().getOptions().getValueOnDoubleClick());
        builder.setValueOnHoldClick(request.getItem().getOptions().getValueOnHoldClick());
        builder.setValueOnHoldReleaseClick(request.getItem().getOptions().getValueOnHoldReleaseClick());

        builder.setValueToPushConfirmMessage(request.getItem().getOptions().getPushConfirmMessage());
        Source pushSource = request.getItem().getOptions().getPushSource();
        if (pushSource != null) {
            builder.setValueToPushSource(buildDataSource(request, entityContext, pushSource));
        }
    }

    private String buildDataSource(MainWidgetRequest request, EntityContext entityContext, Source source) {
        switch (source.getKind()) {
            case variable -> {
                String variable = entityContext.var().createVariable(request.getWidgetRequest().getEntity().getEntityID(),
                    source.getValue(), source.getValue(), source.getVariableType(), null);
                return entityContext.var().buildDataSource(variable, true);
            }
            case broadcasts -> {
                String id = source.getValue() + "_" + request.getWidgetRequest().getEntity().getIeeeAddress();
                String name = source.getValue() + " " + request.getWidgetRequest().getEntity().getIeeeAddress();
                String variableID = entityContext.var().createVariable("broadcasts", id, name, VariableType.Any, null);
                return entityContext.var().buildDataSource(variableID, true);
            }
            case property -> {
                ZigBeeProperty property = request.getWidgetRequest().getEntity().getProperty(source.getValue());
                if (property == null) {
                    throw new IllegalArgumentException("Unable to find z2m property: " + source.getValue() +
                        " for device: " + request.getWidgetRequest().getEntity());
                }
                return WidgetBuilder.getSource(entityContext, property, true);
            }
            default -> throw new IllegalArgumentException("Unable to find handler for type: " + source.getKind());
        }
    }

    private void addProperty(EntityContext entityContext, WidgetDefinition wb, DisplayWidgetBuilder builder, ZigBeeProperty property,
        Consumer<DisplayWidgetSeriesBuilder> handler) {
        builder.addSeries(property.getName(true), seriesBuilder -> {
            seriesBuilder
                .setIcon(property.getIcon())
                .setValueDataSource(WidgetBuilder.getSource(entityContext, property, false))
                .setValueTemplate(null, property.getUnit())
                .setValueSuffixFontSize(0.6)
                .setValueSuffixColor("#777777")
                .setValueSuffixVerticalAlign(VerticalAlign.bottom);
            handler.accept(seriesBuilder);
            ItemDefinition wbProperty = wb.getProperty(property.getKey());
            if (wbProperty != null) {
                seriesBuilder.setValueConverter(wbProperty.getValueConverter());
                seriesBuilder.setValueConverterRefreshInterval(wbProperty.getValueConverterRefreshInterval());
                seriesBuilder.setValueColor(wbProperty.getValueColor());
                seriesBuilder.setValueSourceClickHistory(wbProperty.isValueSourceClickHistory());

                if (isNotEmpty(wbProperty.getIcon()) || wbProperty.getIconThreshold() != null) {
                    seriesBuilder.setIcon(defaultString(wbProperty.getIcon(), property.getIcon().getColor()), thresholdBuilder ->
                        buildThreshold(wbProperty.getIconThreshold(), thresholdBuilder));
                    if (isNotEmpty(wbProperty.getIconColor()) || wbProperty.getIconColorThreshold() != null) {
                        seriesBuilder.setIconColor(defaultString(wbProperty.getIconColor(), property.getIcon().getColor()), thresholdBuilder ->
                            buildThreshold(wbProperty.getIconColorThreshold(), thresholdBuilder));
                    }
                }
            }
        });
    }

    private void buildThreshold(JsonNode thresholdConfiguration, ThresholdBuilder thresholdBuilder) {
        if (thresholdConfiguration != null) {
            for (JsonNode threshold : thresholdConfiguration) {
                thresholdBuilder.setThreshold(
                    threshold.get("target").asText(),
                    threshold.get("value").asText(),
                    ValueCompare.valueOf(threshold.get("op").asText()));

            }
        }
    }
}
