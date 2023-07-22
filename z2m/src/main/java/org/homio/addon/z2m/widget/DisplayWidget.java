package org.homio.addon.z2m.widget;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Options;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Options.Chart;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Options.Source;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition.ItemDefinition;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextWidget.DisplayWidgetBuilder;
import org.homio.api.EntityContextWidget.DisplayWidgetSeriesBuilder;
import org.homio.api.EntityContextWidget.HasChartDataSource;
import org.homio.api.EntityContextWidget.HasLineChartBehaviour;
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
        Map<String, ZigBeeProperty> properties = entity.getDeviceService().getProperties().entrySet().stream()
                                                       .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        entityContext.widget().createLayoutWidget(layoutID, builder -> {
            WidgetBuilder.buildCommon(wd, widgetRequest, builder);
            builder.setBlockSize(wd.getBlockWidth(1), wd.getBlockHeight(1))
                   .setLayoutDimension(propertiesSize + 1, 3);
        });
        var request = new MainWidgetRequest(widgetRequest, wd, 3,
            propertiesSize + 1, builder -> builder.attachToLayout(layoutID, 0, 0));
        buildMainWidget(request);

        ComposeWidget.addBottomRow(entityContext, wd, layoutID, propertiesSize, properties);
    }

    @Override
    public void buildMainWidget(MainWidgetRequest request) {
        WidgetRequest widgetRequest = request.getWidgetRequest();
        EntityContext entityContext = widgetRequest.getEntityContext();
        Z2MDeviceEntity entity = widgetRequest.getEntity();

        List<ZigBeeProperty> includeProperties = request.getItemIncludeProperties();
        if (includeProperties.isEmpty()) {
            throw new IllegalArgumentException("Unable to find display properties for device: " + entity);
        }

        WidgetDefinition wd = request.getItem();
        entityContext.widget().createDisplayWidget("dw-" + entity.getIeeeAddress(), builder -> {
            WidgetBuilder.buildCommon(wd, widgetRequest, builder);
            builder.setPadding(0, 2, 0, 2);
            buildPushValue(request.getItem().getOptions(), builder, entity, entityContext);

            WidgetBuilder.buildBackground(wd.getBackground(), widgetRequest, builder);

            String layout = wd.getLayout();
            if (isNotEmpty(layout)) {
                builder.setLayout(layout);
            }
            builder.setBlockSize(
                wd.getBlockWidth(request.getLayoutColumnNum()),
                wd.getBlockHeight(request.getLayoutRowNum())); // includeProperties.size()

            request.getAttachToLayoutHandler().accept(builder);

            for (ZigBeeProperty property : includeProperties) {
                addProperty(widgetRequest, request.getItem(), builder, property, seriesBuilder ->
                    Optional.ofNullable(PROPERTIES.get(property.getKey())).ifPresent(ib -> ib.build(seriesBuilder)));
            }

            Chart chart = wd.getOptions().getChart();
            if (chart != null) {
                builder.setChartDataSource(WidgetBuilder.buildDataSource(entity, entityContext, chart.getSource()));
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

    private void buildPushValue(Options options, DisplayWidgetBuilder builder, Z2MDeviceEntity entity, EntityContext entityContext) {
        builder.setValueOnClick(options.getValueOnClick());
        builder.setValueOnDoubleClick(options.getValueOnDoubleClick());
        builder.setValueOnHoldClick(options.getValueOnHoldClick());
        builder.setValueOnHoldReleaseClick(options.getValueOnHoldReleaseClick());

        builder.setValueToPushConfirmMessage(options.getPushConfirmMessage());
        Source pushSource = options.getPushSource();
        if (pushSource != null) {
            builder.setValueToPushSource(WidgetBuilder.buildDataSource(entity, entityContext, pushSource));
        }
    }

    private void addProperty(WidgetRequest widgetRequest, WidgetDefinition wb, DisplayWidgetBuilder builder,
        ZigBeeProperty property, Consumer<DisplayWidgetSeriesBuilder> handler) {
        builder.addSeries(property.getName(true), seriesBuilder -> {
            seriesBuilder
                .setValueDataSource(WidgetBuilder.getSource(widgetRequest.getEntityContext(), property, false))
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
            }
            WidgetBuilder.buildIconAndColor(property, seriesBuilder, wbProperty, widgetRequest);
        });
    }
}
