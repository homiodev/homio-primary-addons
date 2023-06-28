package org.homio.addon.z2m.widget;

import static org.homio.addon.z2m.widget.DisplayWidget.fillHasLineChartBehaviour;

import java.util.List;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.Options.Chart;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition.ItemDefinition;
import org.homio.api.EntityContext;
import org.homio.api.entity.zigbee.ZigBeeProperty;

public class LineWidget implements WidgetBuilder {

    @Override
    public void buildWidget(WidgetRequest widgetRequest) {
        WidgetDefinition widgetDefinition = widgetRequest.getWidgetDefinition();

        var request = new MainWidgetRequest(widgetRequest, widgetDefinition, 0,
            0, builder -> {});
        buildMainWidget(request);
    }

    @Override
    public void buildMainWidget(MainWidgetRequest request) {
        EntityContext entityContext = request.getWidgetRequest().getEntityContext();
        Z2MDeviceEntity entity = request.getWidgetRequest().getEntity();

        WidgetDefinition wd = request.getItem();
        List<ZigBeeProperty> barSeries = wd.getIncludeProperties(request);

        entityContext.widget().createLineChartWidget("ln-" + entity.getIeeeAddress(), builder -> {
            builder.setBlockSize(wd.getBlockWidth(3), wd.getBlockHeight(1))
                   .setZIndex(wd.getZIndex(20))
                   .setShowAxisX(wd.getOptions().isShowAxisX())
                   .setShowAxisY(wd.getOptions().isShowAxisY())
                   .setShowChartFullScreenButton(wd.getOptions().isShowChartFullScreenButton())
                   .setChartPointsPerHour(wd.getOptions().getPointsPerHour())
                   .setPointRadius(wd.getOptions().getPointRadius())
                   .setShowDynamicLine(wd.getOptions().isShowDynamicLine())
                   .setDynamicLineColor(wd.getOptions().getDynamicLineColor())
                   .setPointBorderColor(wd.getOptions().getPointBorderColor());
            request.getAttachToLayoutHandler().accept(builder);

            for (ZigBeeProperty series : barSeries) {
                builder.addSeries(series.getName(false), seriesBuilder -> {
                    seriesBuilder.setChartDataSource(WidgetBuilder.getSource(entityContext, series, false));
                    ItemDefinition property = wd.getProperty(series.getKey());
                    if (property != null) {
                        Chart chart = property.getChart();
                        String color = series.getIcon().getColor();
                        if (chart != null) {
                            fillHasLineChartBehaviour(builder, chart);
                            color = chart.getColor();
                        }
                        seriesBuilder.setChartColor(color);
                    }
                });
            }
        });
    }

    @Override
    public int getWidgetHeight(MainWidgetRequest request) {
        return request.getItem().getWidgetHeight(1);
    }
}
