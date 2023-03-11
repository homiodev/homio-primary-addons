package org.touchhome.bundle.z2m.model;

import java.util.List;
import java.util.stream.Collectors;
import lombok.val;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.zigbee.ZigBeeProperty;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.ui.UI;
import org.touchhome.bundle.api.ui.UI.Color;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.layout.UIFlexLayoutBuilder;
import org.touchhome.bundle.z2m.util.Z2MDeviceDefinitionDTO.Requests;
import org.touchhome.bundle.z2m.util.Z2MDeviceDefinitionDTO.WidgetDefinition;
import org.touchhome.bundle.z2m.util.Z2MDeviceDefinitionDTO.WidgetType;
import org.touchhome.bundle.z2m.util.ZigBeeUtil;
import org.touchhome.bundle.z2m.widget.WidgetBuilder;
import org.touchhome.bundle.z2m.widget.WidgetBuilder.WidgetRequest;

public class Z2MActionsBuilder {

    public static void createWidgetActions(UIInputBuilder uiInputBuilder, EntityContext context, Z2MDeviceEntity entity) {
        for (WidgetDefinition widgetDefinition : ZigBeeUtil.getDeviceWidgets(entity.getModel())) {
            WidgetType type = widgetDefinition.getType();
            WidgetBuilder widgetBuilder = WidgetBuilder.WIDGETS.get(type);
            if (widgetBuilder == null) {
                throw new IllegalStateException("Widget creation not implemented for type: " + type);
            }
            String icon = widgetDefinition.getIcon();
            String iconColor = UI.Color.random();
            String title = "widget.create_" + widgetDefinition.getName();
            uiInputBuilder
                .addOpenDialogSelectableButton(title, icon, iconColor, null,
                    (entityContext, params) -> createWidget(widgetBuilder, widgetDefinition, entityContext, params, entity))
                .editDialog(dialogBuilder -> {
                    dialogBuilder.setTitle(title, icon, iconColor);
                    dialogBuilder.addFlex("main", flex -> {
                        flex.addSelectBox("selection.dashboard_tab", null)
                            .setSelected(context.widget().getDashboardDefaultID())
                            .addOptions(context.widget().getDashboardTabs());
                        addPropertyDefinitions(widgetDefinition, flex, entity);
                        addRequests(widgetDefinition, flex, entity);
                    });
                });
        }
    }

    private static ActionResponseModel createWidget(WidgetBuilder widgetBuilder, WidgetDefinition widgetDefinition,
        EntityContext entityContext, JSONObject params, Z2MDeviceEntity entity) {
        String tab = params.getString("selection.dashboard_tab");

        val includeProperties = widgetDefinition.getProps(entity).stream()
                                                .filter(pd -> params.getBoolean(pd.getKey()))
                                                .collect(Collectors.toList());

        List<Requests> requests = widgetDefinition.getRequests();
        if (requests != null) {
            for (Requests request : requests) {
                Object value = params.get(request.getName());
                WidgetDefinition.replaceField(request.getTarget(), value, widgetDefinition);
            }
        }

        widgetBuilder.buildWidget(new WidgetRequest(entityContext, entity, tab, widgetDefinition, includeProperties));
        return ActionResponseModel.success();
    }

    private static void addPropertyDefinitions(WidgetDefinition widgetDefinition, UIFlexLayoutBuilder flex, Z2MDeviceEntity entity) {
        val existedProperties = widgetDefinition.getProps(entity);
        if (existedProperties.isEmpty()) {
            return;
        }

        flex.addFlex("properties", propertyBuilder -> {
            propertyBuilder.setBorderArea("Endpoints").setBorderColor(Color.BLUE);
            for (ZigBeeProperty propertyDefinition : existedProperties) {
                propertyBuilder.addCheckbox(propertyDefinition.getKey(), true, null)
                               .setTitle(propertyDefinition.getName(false));
            }
        });
    }

    private static void addRequests(WidgetDefinition widgetDefinition, UIFlexLayoutBuilder flex, Z2MDeviceEntity entity) {
        List<Requests> requests = widgetDefinition.getRequests();
        if (requests != null) {
            flex.addFlex("inputs", builder -> {
                builder.setBorderArea("Inputs").setBorderColor(Color.GREEN);
                for (Requests request : requests) {
                    switch (request.getType()) {
                        case number:
                            builder.addNumberInput(request.getName(), Float.parseFloat(request.getValue()),
                                request.getMin(), request.getMax(), null).setTitle(request.getTitle());
                            break;
                    }
                }
            });
        }
        val existedProperties = widgetDefinition.getProps(entity);
        if (existedProperties.isEmpty()) {
            return;
        }

        flex.addFlex("properties", propertyBuilder -> {
            propertyBuilder.setBorderArea("Endpoints").setBorderColor(Color.BLUE);
            for (ZigBeeProperty propertyDefinition : existedProperties) {
                propertyBuilder.addCheckbox(propertyDefinition.getKey(), true, null)
                               .setTitle(propertyDefinition.getName(false));
            }
        });
    }
}
