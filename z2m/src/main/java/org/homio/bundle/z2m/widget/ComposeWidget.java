package org.homio.bundle.z2m.widget;

import static org.homio.bundle.z2m.service.properties.Z2MPropertyLastUpdate.UPDATED;
import static org.homio.bundle.z2m.service.properties.dynamic.Z2MGeneralProperty.SIGNAL;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.val;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.EntityContextWidget.HorizontalAlign;
import org.homio.bundle.api.entity.zigbee.ZigBeeProperty;
import org.homio.bundle.api.exception.ProhibitedExecution;
import org.homio.bundle.z2m.model.Z2MDeviceEntity;
import org.homio.bundle.z2m.util.Z2MDeviceDefinitionDTO.WidgetDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComposeWidget implements WidgetBuilder {

    public static final String[] LEFT_PROPERTIES = new String[]{"battery", "voltage"};
    public static final String[] CENTER_PROPERTIES = new String[]{UPDATED};
    public static final String[] RIGHT_PROPERTIES = new String[]{SIGNAL};

    @Override
    public void buildWidget(WidgetRequest widgetRequest) {
        EntityContext entityContext = widgetRequest.getEntityContext();
        Z2MDeviceEntity entity = widgetRequest.getEntity();
        WidgetDefinition wd = widgetRequest.getWidgetDefinition();
        List<WidgetDefinition> compose = wd.getCompose();
        if (compose == null || compose.isEmpty()) {
            throw new IllegalArgumentException("Unable to create compose widget without compose properties");
        }

        int layoutRows = calcLayoutRows(widgetRequest, compose);
        String layoutID = "lt-cmp-" + entity.getIeeeAddress();
        entityContext.widget().createLayoutWidget(layoutID, builder -> {
            builder
                .setBlockSize(wd.getBlockWidth(1), wd.getBlockHeight(1))
                .setZIndex(wd.getZIndex(15))
                .setBackground(wd.getBackground())
                .setLayoutDimension(layoutRows + 1, 3);
        });

        AtomicInteger currentLayoutRow = new AtomicInteger(0);
        for (WidgetDefinition item : compose) {
            WidgetBuilder innerWidgetBuilder = WidgetBuilder.WIDGETS.get(item.getType());
            val request = new MainWidgetRequest(widgetRequest, item, 3, layoutRows, builder ->
                builder.attachToLayout(layoutID, currentLayoutRow.get(), 0));
            innerWidgetBuilder.buildMainWidget(request);
            currentLayoutRow.addAndGet(innerWidgetBuilder.getWidgetHeight(request));
        }

        Map<String, ZigBeeProperty> properties = widgetRequest.getEntity().getProperties();

        WidgetBuilder.addProperty(
            entityContext,
            HorizontalAlign.left,
            findCellProperty(wd.getLeftProperty(), properties, LEFT_PROPERTIES),
            builder -> builder.attachToLayout(layoutID, currentLayoutRow.get(), 0));

        WidgetBuilder.addProperty(
            entityContext,
            HorizontalAlign.center,
            findCellProperty(wd.getCenterProperty(), properties, CENTER_PROPERTIES),
            builder -> builder.attachToLayout(layoutID, currentLayoutRow.get(), 1));

        WidgetBuilder.addProperty(
            entityContext,
            HorizontalAlign.right,
            findCellProperty(wd.getRightProperty(), properties, RIGHT_PROPERTIES),
            builder -> builder.attachToLayout(layoutID, currentLayoutRow.get(), 2));
    }

    @Override
    public int getWidgetHeight(MainWidgetRequest request) {
        throw new ProhibitedExecution();
    }

    @Override
    public void buildMainWidget(MainWidgetRequest request) {
        throw new ProhibitedExecution();
    }

    private @Nullable ZigBeeProperty findCellProperty(
        @Nullable String property,
        @NotNull Map<String, ZigBeeProperty> properties,
        @NotNull String[] availableProperties) {
        if ("none".equals(property)) {
            return null;
        }
        return Arrays.stream(availableProperties).filter(properties::containsKey).findFirst().map(properties::get).orElse(null);
    }

    private int calcLayoutRows(WidgetRequest widgetRequest, List<WidgetDefinition> compose) {
        int layoutRows = 0;
        for (WidgetDefinition item : compose) {
            val request = new MainWidgetRequest(widgetRequest, item, 0, 0, null);
            layoutRows += WidgetBuilder.WIDGETS.get(item.getType()).getWidgetHeight(request);
        }
        return layoutRows;
    }
}
