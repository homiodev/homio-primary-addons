package org.homio.addon.z2m.widget;

import static org.homio.addon.z2m.service.Z2MProperty.PROPERTY_BATTERY;
import static org.homio.addon.z2m.service.Z2MProperty.PROPERTY_LAST_SEEN;
import static org.homio.addon.z2m.service.properties.inline.Z2MPropertyGeneral.PROPERTY_SIGNAL;
import static org.homio.addon.z2m.service.properties.inline.Z2MPropertyLastUpdatedProperty.PROPERTY_LAST_UPDATED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.val;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.util.Z2MDeviceDefinitionModel.WidgetDefinition;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextWidget.HorizontalAlign;
import org.homio.api.entity.zigbee.ZigBeeProperty;
import org.homio.api.exception.ProhibitedExecution;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComposeWidget implements WidgetBuilder {

    public static final String[] LEFT_PROPERTIES = new String[]{PROPERTY_BATTERY, "power", "consumption", "energy", "voltage"};
    public static final String[] CENTER_PROPERTIES = new String[]{PROPERTY_LAST_SEEN, PROPERTY_LAST_UPDATED};
    public static final String[] RIGHT_PROPERTIES = new String[]{PROPERTY_SIGNAL};

    @Override
    public void buildWidget(WidgetRequest widgetRequest) {
        EntityContext entityContext = widgetRequest.getEntityContext();
        Z2MDeviceEntity entity = widgetRequest.getEntity();
        WidgetDefinition wd = widgetRequest.getWidgetDefinition();
        List<WidgetDefinition> composeContainer = wd.getCompose();
        if (composeContainer == null || composeContainer.isEmpty()) {
            throw new IllegalArgumentException("Unable to create compose widget without compose properties");
        }

        // set 3 as min layout height to look better
        List<Integer> rowHeights = calcLayoutRows(widgetRequest, composeContainer);
        int layoutRowsCount = calcLayoutRowsCount(rowHeights);

        int composeBlockHeight = adjustBlockHeightToInnerContentHeight(wd, layoutRowsCount);

        String layoutID = "lt-cmp-" + entity.getIeeeAddress();
        int columns = 3;

        entityContext.widget().createLayoutWidget(layoutID, builder ->
            builder
                .setBlockSize(wd.getBlockWidth(1), composeBlockHeight)
                .setZIndex(wd.getZIndex(15))
                .setBackground(wd.getBackground())
                .setLayoutDimension(layoutRowsCount + 1, columns));

        AtomicInteger currentLayoutRow = new AtomicInteger(0);
        for (int i = 0; i < composeContainer.size(); i++) {
            int wdMinRowHeight = rowHeights.get(i);
            int rowHeight = calcAdjustRowHeight(wdMinRowHeight, layoutRowsCount);

            WidgetDefinition item = composeContainer.get(i);
            WidgetBuilder innerWidgetBuilder = WidgetBuilder.WIDGETS.get(item.getType());
            val request = new MainWidgetRequest(widgetRequest, item, columns, rowHeight, builder ->
                builder.attachToLayout(layoutID, currentLayoutRow.get(), 0));
            innerWidgetBuilder.buildMainWidget(request);
            currentLayoutRow.addAndGet(rowHeight);
        }

        Map<String, ZigBeeProperty> properties = widgetRequest.getEntity().getProperties();

        addBottomRow(entityContext, wd, layoutID, currentLayoutRow.get(), properties);
    }

    public static void addBottomRow(EntityContext entityContext, WidgetDefinition wd, String layoutID, int row,
        Map<String, ZigBeeProperty> properties) {
        ZigBeeProperty leftProperty = findCellProperty(wd.getLeftProperty(), properties, LEFT_PROPERTIES);
        WidgetBuilder.addProperty(
            entityContext,
            HorizontalAlign.left,
            leftProperty,
            true,
            builder -> builder.attachToLayout(layoutID, row, 0));

        WidgetBuilder.addProperty(
            entityContext,
            HorizontalAlign.center,
            findCellProperty(wd.getCenterProperty(), properties, CENTER_PROPERTIES),
            false,
            builder -> builder.attachToLayout(layoutID, row, 1));

        WidgetBuilder.addProperty(
            entityContext,
            HorizontalAlign.right,
            findCellProperty(wd.getRightProperty(), properties, RIGHT_PROPERTIES),
            false,
            builder -> builder.attachToLayout(layoutID, row, 2));
    }

    private int calcAdjustRowHeight(int wdMinRowHeight, int layoutRowsCount) {
        return (int) (layoutRowsCount / (float) wdMinRowHeight);
    }

    private int calcLayoutRowsCount(List<Integer> rowHeights) {
        int minHeight = rowHeights.stream().reduce(0, Integer::sum);
        return minHeight == 1 ? 3 : minHeight;
    }

    private int adjustBlockHeightToInnerContentHeight(WidgetDefinition wd, int layoutRows) {
        int composeBlockHeight = wd.getBlockHeight(1);
        if (layoutRows > 6 && composeBlockHeight == 1) {
            composeBlockHeight = 2;
        }
        return composeBlockHeight;
    }

    @Override
    public int getWidgetHeight(MainWidgetRequest request) {
        throw new ProhibitedExecution();
    }

    @Override
    public void buildMainWidget(MainWidgetRequest request) {
        throw new ProhibitedExecution();
    }

    private static @Nullable ZigBeeProperty findCellProperty(
        @Nullable String property,
        @NotNull Map<String, ZigBeeProperty> properties,
        @NotNull String[] availableProperties) {
        if ("none".equals(property)) {
            return null;
        }
        return Arrays.stream(availableProperties).filter(properties::containsKey).findFirst().map(properties::get).orElse(null);
    }

    private List<Integer> calcLayoutRows(WidgetRequest widgetRequest, List<WidgetDefinition> compose) {
        List<Integer> rowHeights = new ArrayList<>(compose.size());
        for (WidgetDefinition item : compose) {
            val request = new MainWidgetRequest(widgetRequest, item, 0, 0, null);
            rowHeights.add(WidgetBuilder.WIDGETS.get(item.getType()).getWidgetHeight(request));
        }
        return rowHeights;
    }
}
