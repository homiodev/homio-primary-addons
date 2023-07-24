package org.homio.addon.z2m.widget;

import java.util.List;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition;
import org.homio.api.model.DeviceProperty;

public class BarTimeWidget implements WidgetBuilder {

    @Override
    public void buildWidget(WidgetRequest widgetRequest) {
        WidgetDefinition wd = widgetRequest.getWidgetDefinition();

        var request = new MainWidgetRequest(widgetRequest, wd, 0,
            0, builder -> WidgetBuilder.buildCommon(wd, widgetRequest, builder));
        buildMainWidget(request);
    }

    @Override
    public void buildMainWidget(MainWidgetRequest request) {
        WidgetRequest widgetRequest = request.getWidgetRequest();
        Z2MDeviceEntity entity = widgetRequest.getEntity();
        WidgetDefinition wd = request.getItem();

        List<DeviceProperty> barSeries = wd.getIncludeProperties(request);
        widgetRequest.getEntityContext().widget().createBarTimeChartWidget("bt-" + entity.getIeeeAddress(), builder -> {
            WidgetBuilder.buildCommon(wd, widgetRequest, builder);
            builder.setBlockSize(wd.getBlockWidth(3), wd.getBlockHeight(1))
                   .setShowAxisX(wd.getOptions().isShowAxisX())
                   .setShowAxisY(wd.getOptions().isShowAxisY())
                   .setShowChartFullScreenButton(wd.getOptions().isShowChartFullScreenButton())
                   .setChartPointsPerHour(wd.getOptions().getPointsPerHour())
                   .setShowDynamicLine(wd.getOptions().isShowDynamicLine())
                   .setDynamicLineColor(wd.getOptions().getDynamicLineColor());
            request.getAttachToLayoutHandler().accept(builder);

            for (DeviceProperty series : barSeries) {
                builder.addSeries(series.getName(false), seriesBuilder ->
                    seriesBuilder.setChartDataSource(
                        WidgetBuilder.getSource(widgetRequest.getEntityContext(), series, false)));
            }
        });
    }

    @Override
    public int getWidgetHeight(MainWidgetRequest request) {
        return 1;
    }
}
