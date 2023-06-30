package org.homio.addon.z2m.widget;

import static java.lang.String.format;
import static org.homio.api.ui.field.UIFieldLayout.HorizontalAlign.left;
import static org.homio.api.ui.field.UIFieldLayout.HorizontalAlign.right;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition;
import org.homio.api.EntityContext;
import org.homio.api.entity.zigbee.ZigBeeProperty;
import org.homio.api.exception.ProhibitedExecution;
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
        Z2MDeviceEntity entity = request.getWidgetRequest().getEntity();
        EntityContext entityContext = request.getWidgetRequest().getEntityContext();
        Map<String, Z2MProperty> properties = entity.getDeviceService().getProperties();
        List<ZigBeeProperty> includeProperties = request.getItemIncludeProperties();
        if (includeProperties.isEmpty()) {
            throw new IllegalArgumentException("Unable to find properties from device: " + entity);
        }
        WidgetDefinition wd = request.getItem();

        entityContext.widget().createToggleWidget("tgl_" + entity.getEntityID(), builder -> {
            builder.setName(entity.getDescription()).setShowName(false);
            builder.setDisplayType(wd.getOptions().getToggleType())
                   .setLayout(UIFieldLayout.LayoutBuilder.builder(50, 50).addRow(
                       rowBuilder -> rowBuilder.addCol("name", left).addCol("button", right)
                   ).build());
            builder.setBlockSize(
                       wd.getBlockWidth(request.getLayoutColumnNum()),
                       wd.getBlockHeight(request.getLayoutRowNum()))
                   .setZIndex(wd.getZIndex(20));
            request.getAttachToLayoutHandler().accept(builder);

            for (ZigBeeProperty property : includeProperties) {
                Z2MProperty z2MProperty = properties.get(property.getKey());
                builder.addSeries(getName(entity, z2MProperty), seriesBuilder ->
                    WidgetBuilder.setValueDataSource(seriesBuilder, entityContext, z2MProperty)
                                 .setColor(UI.Color.random()));
            }
        });
    }

    private int getWidgetHeight(List<ZigBeeProperty> properties) {
        return Math.round(properties.size() * 2F / 3);
    }

    private String getName(Z2MDeviceEntity entity, Z2MProperty state) {
        if (StringUtils.isNotEmpty(entity.getPlace())) {
            return entity.getPlace() + format("[%s]", state.getName(true));
        }
        return state.getName(true);
    }
}
