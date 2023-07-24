package org.homio.addon.z2m.widget;

import static org.homio.api.ui.field.UIFieldLayout.HorizontalAlign.left;
import static org.homio.api.ui.field.UIFieldLayout.HorizontalAlign.right;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition.ItemDefinition;
import org.homio.api.exception.ProhibitedExecution;
import org.homio.api.model.DeviceProperty;
import org.homio.api.ui.UI;
import org.homio.api.ui.field.UIFieldLayout;

public class ToggleWidget implements WidgetBuilder {

    @Override
    public void buildWidget(WidgetRequest widgetRequest) {
        // Use Compose Widget instead
        throw new ProhibitedExecution();
    }

    @Override
    public int getWidgetHeight(MainWidgetRequest request) {
        int defaultWidgetHeight = getWidgetHeight(request.getItemIncludeProperties());
        return request.getItem().getWidgetHeight(defaultWidgetHeight);
    }

    @Override
    public void buildMainWidget(MainWidgetRequest request) {
        WidgetRequest widgetRequest = request.getWidgetRequest();
        Z2MDeviceEntity entity = widgetRequest.getEntity();

        Map<String, Z2MProperty> properties = entity.getDeviceService().getProperties();
        List<DeviceProperty> includeProperties = request.getItemIncludeProperties();
        if (includeProperties.isEmpty()) {
            throw new IllegalArgumentException("Unable to find properties from device: " + entity);
        }
        WidgetDefinition wd = request.getItem();

        widgetRequest.getEntityContext().widget().createToggleWidget("tgl_" + entity.getEntityID(), builder -> {
            WidgetBuilder.buildCommon(wd, widgetRequest, builder);
            builder.setDisplayType(wd.getOptions().getToggleType())
                   .setLayout(UIFieldLayout.LayoutBuilder.builder(50, 50).addRow(
                       rowBuilder -> rowBuilder.addCol("name", left).addCol("button", right)
                   ).build());
            builder.setBlockSize(
                wd.getBlockWidth(request.getLayoutColumnNum()),
                wd.getBlockHeight(request.getLayoutRowNum()));
            builder.setShowAllButton(wd.getOptions().getShowAllButton());
            request.getAttachToLayoutHandler().accept(builder);

            for (DeviceProperty property : includeProperties) {
                ItemDefinition wbProperty = wd.getProperty(property.getKey());
                Z2MProperty z2MProperty = properties.get(property.getKey());
                builder.addSeries(getName(entity, z2MProperty), seriesBuilder -> {
                    WidgetBuilder.buildIconAndColor(property, seriesBuilder, wbProperty, widgetRequest);
                    WidgetBuilder.setValueDataSource(seriesBuilder, widgetRequest.getEntityContext(), z2MProperty)
                                 .setColor(UI.Color.random());
                });
            }
        });
    }

    private int getWidgetHeight(List<DeviceProperty> properties) {
        return Math.round(properties.size() * 2F / 3);
    }

    private String getName(Z2MDeviceEntity entity, Z2MProperty state) {
        if (StringUtils.isNotEmpty(entity.getPlace())) {
            return "%s[%s]".formatted(entity.getPlace(), state.getName(true));
        }
        return state.getName(true);
    }
}
