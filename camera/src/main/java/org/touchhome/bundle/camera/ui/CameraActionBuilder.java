package org.touchhome.bundle.camera.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.touchhome.bundle.api.model.KeyValueEnum;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.state.JsonType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.api.ui.action.UIActionHandler;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.v1.UIEntityItemBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.item.UIMultiButtonItemBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.item.UISelectBoxItemBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.item.UISliderItemBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.layout.UIFlexLayoutBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.layout.UILayoutBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.layout.dialog.UIStickyDialogItemBuilder;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.common.util.CommonUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Log4j2
public class CameraActionBuilder {

    public static void assembleActions(CameraActionsContext instance, UIInputBuilder uiInputBuilder) {
        Set<String> handledMethods = new HashSet<>();
        for (Method method : MethodUtils.getMethodsWithAnnotation(instance.getClass(), UICameraAction.class, true, false)) {
            UICameraActionConditional cameraActionConditional = method.getDeclaredAnnotation(UICameraActionConditional.class);
            if (handledMethods.add(method.getName()) && (cameraActionConditional == null ||
                    CommonUtils.newInstance(cameraActionConditional.value()).test(instance))) {

                UICameraAction uiCameraAction = method.getDeclaredAnnotation(UICameraAction.class);
                Parameter actionParameter = method.getParameters()[0];
                Function<String, Object> actionParameterConverter = buildParameterActionConverter(actionParameter);

                UIFieldType type;
                if (uiCameraAction.type() == UICameraAction.ActionType.AutoDiscover) {
                    if (method.isAnnotationPresent(UICameraSelectionAttributeValues.class) || method.isAnnotationPresent(UIFieldSelection.class)) {
                        type = UIFieldType.SelectBox;
                    } else {
                        type = getFieldTypeFromMethod(actionParameter);
                    }
                } else {
                    type = uiCameraAction.type() == UICameraAction.ActionType.Dimmer
                            ? UIFieldType.Slider : uiCameraAction.type() == UICameraAction.ActionType.Switch
                            ? UIFieldType.Boolean : UIFieldType.String;
                }

                UIActionHandler actionHandler = (entityConetxt, params) -> {
                    try {
                        method.invoke(instance, actionParameterConverter.apply(params.optString("value")));
                    } catch (Exception ex) {
                        log.error("Unable to invoke camera action: <{}>", CommonUtils.getErrorMessage(ex));
                    }
                    return null;
                };

                UIEntityItemBuilder uiEntityItemBuilder;
                UILayoutBuilder layoutBuilder = uiInputBuilder;
                if (StringUtils.isNotEmpty(uiCameraAction.group())) {

                    if (StringUtils.isEmpty(uiCameraAction.subGroup())) {
                        layoutBuilder = uiInputBuilder.addFlex(uiCameraAction.group(), uiCameraAction.order())
                                .columnFlexDirection()
                                .setBorderArea(uiCameraAction.group());
                    } else {
                        UIStickyDialogItemBuilder stickyLayoutBuilder = layoutBuilder.addStickyDialogButton(
                                uiCameraAction.group() + "_sb", uiCameraAction.subGroupIcon(), null, uiCameraAction.order())
                                .editButton(buttonItemBuilder -> buttonItemBuilder.setText(uiCameraAction.group()));

                        layoutBuilder = stickyLayoutBuilder.addFlex(uiCameraAction.subGroup(), uiCameraAction.order())
                                .setBorderArea(uiCameraAction.subGroup())
                                .columnFlexDirection();
                    }
                }
                UICameraDimmerButton[] buttons = method.getDeclaredAnnotationsByType(UICameraDimmerButton.class);
                if (buttons.length > 0) {
                    if (uiCameraAction.type() != UICameraAction.ActionType.Dimmer) {
                        throw new RuntimeException("Method " + method.getName() + " annotated with @UICameraDimmerButton, but @UICameraAction has no dimmer type");
                    }
                    UIFlexLayoutBuilder flex = layoutBuilder.addFlex("dimmer", uiCameraAction.order());
                    UIMultiButtonItemBuilder multiButtonItemBuilder = flex.addMultiButton("dimm_btns", actionHandler, uiCameraAction.order());
                    for (UICameraDimmerButton button : buttons) {
                        multiButtonItemBuilder.addButton(button.name(), button.icon(), null);
                    }
                    layoutBuilder = flex;
                }

                uiEntityItemBuilder = (UIEntityItemBuilder) createUIEntity(uiCameraAction, type, layoutBuilder, actionHandler)
                        .setIcon(uiCameraAction.icon(), uiCameraAction.iconColor());

                if (type == UIFieldType.SelectBox) {
                    if (actionParameter.getType().isEnum()) {
                        if (KeyValueEnum.class.isAssignableFrom(actionParameter.getType())) {
                            ((UISelectBoxItemBuilder) uiEntityItemBuilder).setOptions(OptionModel.list((Class<? extends KeyValueEnum>) actionParameter.getType()));
                        } else {
                            ((UISelectBoxItemBuilder) uiEntityItemBuilder).setOptions(OptionModel.enumList((Class<? extends Enum>) actionParameter.getType()));
                        }
                    } else if (method.isAnnotationPresent(UICameraSelectionAttributeValues.class)) {
                        uiEntityItemBuilder.addFetchValueHandler("update-selection", () -> {
                            UICameraSelectionAttributeValues attributeValues = method.getDeclaredAnnotation(UICameraSelectionAttributeValues.class);
                            State state = instance.getAttribute(attributeValues.value());
                            if (state instanceof JsonType) {
                                JsonNode jsonNode = ((JsonType) state).get(attributeValues.path());
                                if (jsonNode instanceof ArrayNode) {
                                    List<OptionModel> items = OptionModel.list(jsonNode);
                                    for (int i = 0; i < attributeValues.prependValues().length; i += 2) {
                                        items.add(i / 2, OptionModel.of(attributeValues.prependValues()[i], attributeValues.prependValues()[i + 1]));
                                    }
                                    ((UISelectBoxItemBuilder) uiEntityItemBuilder).setOptions(items);
                                }
                            }
                        });
                    } else if (method.isAnnotationPresent(UIFieldSelection.class)) {
                        DynamicOptionLoader dynamicOptionLoader;
                        UIFieldSelection attributeValues = method.getDeclaredAnnotation(UIFieldSelection.class);
                        try {
                            Constructor<? extends DynamicOptionLoader> constructor = attributeValues.value().getDeclaredConstructor(instance.getClass());
                            constructor.setAccessible(true);
                            dynamicOptionLoader = constructor.newInstance(instance);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        uiEntityItemBuilder.addFetchValueHandler("update-selection", () -> {
                            ((UISelectBoxItemBuilder) uiEntityItemBuilder)
                                    .setOptions(dynamicOptionLoader.loadOptions(
                                            instance.getCameraEntity(),
                                            instance.getEntityContext(),
                                            attributeValues.staticParameters()));
                        });
                    }
                }
                Method getter = findGetter(instance, uiCameraAction.name());

                // add update value handler
                if (getter != null) {
                    uiEntityItemBuilder.addFetchValueHandler("update-value", () -> {
                        try {
                            Object value = getter.invoke(instance);
                            if (value == null) {
                                return;
                            }
                            uiEntityItemBuilder.setValue(type.getConvertToObject().apply(value));
                        } catch (Exception ex) {
                            log.error("Unable to fetch getter value for action: <{}>. Msg: <{}>",
                                    uiCameraAction.name(), CommonUtils.getErrorMessage(ex));
                        }
                    });
                }
            }
        }
    }

    private static UIEntityItemBuilder<?, ?> createUIEntity(UICameraAction uiCameraAction, UIFieldType type,
                                                            UILayoutBuilder layoutBuilder,
                                                            UIActionHandler handler) {
        switch (type) {
            case SelectBox:
                return layoutBuilder.addSelectBox(uiCameraAction.name(), handler, uiCameraAction.order()).setSelectReplacer(uiCameraAction.min(),
                        uiCameraAction.max(), uiCameraAction.selectReplacer());
            case Slider:
                return layoutBuilder.addSlider(uiCameraAction.name(), 0, uiCameraAction.min(),
                        uiCameraAction.max(), handler, UISliderItemBuilder.SliderType.Regular, uiCameraAction.order());
            case Boolean:
                return layoutBuilder.addCheckbox(uiCameraAction.name(), false, handler, uiCameraAction.order());
            case String:
                return layoutBuilder.addInfo(uiCameraAction.name(), uiCameraAction.order());
            default:
                throw new RuntimeException("Unknown type: " + type);
        }
    }

    private static Method findGetter(Object instance, String name) {
        for (Method method : MethodUtils.getMethodsWithAnnotation(instance.getClass(), UICameraActionGetter.class, true, true)) {
            if (method.getDeclaredAnnotation(UICameraActionGetter.class).value().equals(name)) {
                return method;
            }
        }
        return null;
    }

    private static Function<String, Object> buildParameterActionConverter(Parameter parameter) {
        switch (parameter.getType().getSimpleName()) {
            case "boolean":
                return Boolean::parseBoolean;
            case "int":
                return Integer::parseInt;
        }
        if (parameter.getType().isEnum()) {
            return value -> Enum.valueOf((Class<? extends Enum>) parameter.getType(), value);
        }
        return command -> command;
    }

    private static UIFieldType getFieldTypeFromMethod(Parameter parameter) {
        switch (parameter.getType().getSimpleName()) {
            case "boolean":
                return UIFieldType.Boolean;
            case "int":
                return UIFieldType.Slider;
        }
        if (parameter.getType().isEnum()) {
            return UIFieldType.SelectBox;
        }

        return UIFieldType.String;
    }
}
