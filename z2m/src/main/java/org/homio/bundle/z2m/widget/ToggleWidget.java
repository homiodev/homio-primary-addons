package org.homio.bundle.z2m.widget;

import static java.lang.String.format;
import static org.homio.bundle.api.ui.field.UIFieldLayout.HorizontalAlign.left;
import static org.homio.bundle.api.ui.field.UIFieldLayout.HorizontalAlign.right;
import static org.homio.bundle.z2m.service.properties.Z2MPropertyLastUpdate.UPDATED;
import static org.homio.bundle.z2m.service.properties.dynamic.Z2MGeneralProperty.SIGNAL;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.EntityContextWidget.HorizontalAlign;
import org.homio.bundle.api.EntityContextWidget.VerticalAlign;
import org.homio.bundle.api.entity.zigbee.ZigBeeProperty;
import org.homio.bundle.api.ui.UI;
import org.homio.bundle.api.ui.field.UIFieldLayout;
import org.homio.bundle.z2m.model.Z2MDeviceEntity;
import org.homio.bundle.z2m.service.Z2MProperty;
import org.homio.bundle.z2m.util.Z2MDeviceDefinitionDTO.WidgetDefinition;
import org.jetbrains.annotations.Nullable;

public class ToggleWidget implements WidgetBuilder {

    @Override
    public void buildWidget(WidgetRequest widgetRequest) {
        EntityContext entityContext = widgetRequest.getEntityContext();
        Z2MDeviceEntity entity = widgetRequest.getEntity();
        WidgetDefinition wd = widgetRequest.getWidgetDefinition();

        assertPropertiesExists(widgetRequest, entity);

        String layoutID = "lt-btn-" + entity.getIeeeAddress();
        entityContext.widget().createLayoutWidget(layoutID, builder -> {
            builder.setBlockSize(wd.getBlockWidth(1), wd.getBlockHeight(1))
                   .setZIndex(wd.getZIndex(20))
                   .setBackground(wd.getBackground())
                   .setLayoutDimension(4, 2);
        });
        Map<String, Z2MProperty> properties = entity.getDeviceService().getProperties();

        var request = new MainWidgetRequest(widgetRequest, wd, 2, 4, builder ->
            builder.attachToLayout(layoutID, 0, 0));
        buildMainWidget(request);
        addLastRowProperties(entityContext, layoutID, properties);
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

        entityContext.widget().createToggleWidget("tgl_" + entity.getEntityID(), builder -> {
            builder.setName(entity.getDescription()).setShowName(false);
            builder.setDisplayType(request.getItem().getOptions().getToggleType())
                   .setLayout(UIFieldLayout.LayoutBuilder.builder(50, 50).addRow(
                       rowBuilder -> rowBuilder.addCol("name", left).addCol("button", right)
                   ).build());
            builder.setBlockSize(request.getLayoutColumnNum(), getWidgetHeight(includeProperties))
                   .setZIndex(request.getItem().getZIndex(20));
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

    private void assertPropertiesExists(WidgetRequest widgetRequest, Z2MDeviceEntity entity) {
        int propertiesSize = widgetRequest.getIncludeProperties().size();
        if (propertiesSize == 0) {
            throw new IllegalArgumentException("Unable to find properties: " + widgetRequest.getIncludeProperties() + " from device: " + entity);
        }
    }

    private void addLastRowProperties(EntityContext entityContext, String layoutID, Map<String, Z2MProperty> properties) {
        Z2MProperty leftProperty = getLeftProperty(properties);
        if (leftProperty != null) {
            entityContext.widget().createSimpleValueWidget(leftProperty.getEntityID(), builder -> {
                builder.setIcon(leftProperty.getIcon())
                       .setValueDataSource(WidgetBuilder.getSource(entityContext, leftProperty, false))
                       .setValueFontSize(0.8)
                       .setValueTemplate("", leftProperty.getExpose().getUnit())
                       .setValueSuffixFontSize(0.6)
                       .setValueSuffixVerticalAlign(VerticalAlign.bottom)
                       .attachToLayout(layoutID, 3, 0)
                       .setIconColor(leftProperty.getIconColor());
                Optional.ofNullable(PROPERTIES.get(leftProperty.getKey())).ifPresent(ib -> ib.build(builder));
            });
        }

        WidgetBuilder.addProperty(
            entityContext,
            HorizontalAlign.right,
            properties.get(SIGNAL),
            builder -> builder.attachToLayout(layoutID, 3, 1));
    }

    private @Nullable Z2MProperty getLeftProperty(Map<String, Z2MProperty> properties) {
        for (String property : new String[]{"consumption", "power", "energy", "current", UPDATED}) {
            if (properties.containsKey(property)) {
                return properties.get(property);
            }
        }
        return null;
    }

    private String getName(Z2MDeviceEntity entity, Z2MProperty state) {
        if (StringUtils.isNotEmpty(entity.getPlace())) {
            return entity.getPlace() + format("[%s]", state.getName(true));
        }
        return state.getName(true);
    }
}
