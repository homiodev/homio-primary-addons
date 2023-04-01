package org.homio.bundle.z2m.widget;

import java.util.List;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.entity.zigbee.ZigBeeProperty;
import org.homio.bundle.z2m.model.Z2MDeviceEntity;
import org.homio.bundle.z2m.util.Z2MDeviceDefinitionDTO.WidgetDefinition;

public class BarTimeWidget implements WidgetBuilder {

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
        entityContext.widget().createBarTimeChartWidget("bt-" + entity.getIeeeAddress(), builder -> {
            builder.setBlockSize(wd.getBlockWidth(3), wd.getBlockHeight(1))
                   .setZIndex(wd.getZIndex(20))
                   .setShowAxisX(wd.getOptions().isShowAxisX())
                   .setShowAxisY(wd.getOptions().isShowAxisY())
                   .setShowChartFullScreenButton(wd.getOptions().isShowChartFullScreenButton())
                   .setChartPointsPerHour(wd.getOptions().getPointsPerHour())
                   .setShowDynamicLine(wd.getOptions().isShowDynamicLine())
                   .setDynamicLineColor(wd.getOptions().getDynamicLineColor());
            request.getAttachToLayoutHandler().accept(builder);

            for (ZigBeeProperty series : barSeries) {
                builder.addSeries(series.getName(false), seriesBuilder -> {
                    seriesBuilder.setChartDataSource(WidgetBuilder.getSource(entityContext, series, false));
                });
            }
        });
    }

    @Override
    public int getWidgetHeight(MainWidgetRequest request) {
        return 1;
    }
}
