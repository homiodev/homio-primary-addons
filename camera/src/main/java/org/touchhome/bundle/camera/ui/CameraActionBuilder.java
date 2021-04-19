package org.touchhome.bundle.camera.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.json.JSONObject;
import org.touchhome.bundle.api.model.KeyValueEnum;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.state.JsonType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.impl.StatefulContextMenuAction;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.api.util.TouchHomeUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Log4j2
public class CameraActionBuilder {

    private List<StatefulContextMenuAction> actions = new ArrayList<>();
    private StatefulContextMenuAction lastAction;

    public static CameraActionBuilder builder() {
        return new CameraActionBuilder();
    }

    public static List<StatefulContextMenuAction> assemble(CameraActionsContext instance) {
        CameraActionBuilder builder = builder();
        Set<String> handledMethods = new HashSet<>();
        for (Method method : MethodUtils.getMethodsWithAnnotation(instance.getClass(), UICameraAction.class, true, false)) {
            UICameraActionConditional cameraActionConditional = method.getDeclaredAnnotation(UICameraActionConditional.class);
            if (handledMethods.add(method.getName()) && (cameraActionConditional == null ||
                    TouchHomeUtils.newInstance(cameraActionConditional.value()).test(instance))) {

                UICameraAction uiCameraAction = method.getDeclaredAnnotation(UICameraAction.class);
                Parameter actionParameter = method.getParameters()[0];
                Function<String, Object> actionParameterConverter = buildParameterActionConverter(actionParameter);
                JSONObject options = new JSONObject();
                if (uiCameraAction.min() != 0) {
                    options.put("min", uiCameraAction.min());
                }
                if (uiCameraAction.max() != 100) {
                    options.put("max", uiCameraAction.max());
                }
                if (StringUtils.isNotEmpty(uiCameraAction.selectReplacer())) {
                    options.put("selectReplacer", uiCameraAction.selectReplacer());
                }
                Map<String, Consumer<StatefulContextMenuAction>> updateHandlers = new HashMap<>();

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

                if (type == UIFieldType.SelectBox) {
                    if (actionParameter.getType().isEnum()) {
                        if (KeyValueEnum.class.isAssignableFrom(actionParameter.getType())) {
                            options.put("options", OptionModel.list((Class<? extends KeyValueEnum>) actionParameter.getType()));
                        } else {
                            options.put("options", OptionModel.enumList((Class<? extends Enum>) actionParameter.getType()));
                        }
                    } else if (method.isAnnotationPresent(UICameraSelectionAttributeValues.class)) {
                        updateHandlers.put("update-selection", statefulContextMenuAction -> {
                            UICameraSelectionAttributeValues attributeValues = method.getDeclaredAnnotation(UICameraSelectionAttributeValues.class);
                            State state = instance.getAttribute(attributeValues.value());
                            if (state instanceof JsonType) {
                                JsonNode jsonNode = ((JsonType) state).get(attributeValues.path());
                                if (jsonNode instanceof ArrayNode) {
                                    List<OptionModel> items = OptionModel.list(jsonNode);
                                    for (int i = 0; i < attributeValues.prependValues().length; i += 2) {
                                        items.add(i / 2, OptionModel.of(attributeValues.prependValues()[i], attributeValues.prependValues()[i + 1]));
                                    }
                                    options.put("options", items);
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
                        updateHandlers.put("update-selection", statefulContextMenuAction -> {
                            options.put("options", dynamicOptionLoader.loadOptions(
                                    instance.getCameraEntity(),
                                    instance.getEntityContext(),
                                    attributeValues.staticParameters()));
                        });
                    }
                }
                Method getter = findGetter(instance, uiCameraAction.name());

                // add update value handler
                if (getter != null) {
                    updateHandlers.put("update-value", statefulContextMenuAction -> {
                        try {
                            Object value = getter.invoke(instance);
                            if (value == null) {
                                return;
                            }
                            statefulContextMenuAction.setValue(type.getConvertToObject().apply(value));
                        } catch (Exception ex) {
                            log.error("Unable to fetch getter value for action: <{}>. Msg: <{}>",
                                    uiCameraAction.name(), TouchHomeUtils.getErrorMessage(ex));
                        }
                    });
                }
                builder.add(uiCameraAction.name(), uiCameraAction.group(), uiCameraAction.subGroup(),
                        uiCameraAction.collapseGroup(), uiCameraAction.collapseGroupIcon(), uiCameraAction.order(), uiCameraAction.icon(),
                        uiCameraAction.iconColor(), type,
                        options, param -> {
                            try {
                                method.invoke(instance, actionParameterConverter.apply(param));
                            } catch (Exception ex) {
                                log.error("Unable to invoke camera action: <{}>", TouchHomeUtils.getErrorMessage(ex));
                            }
                        }, updateHandlers);
                UICameraDimmerButton[] buttons = method.getDeclaredAnnotationsByType(UICameraDimmerButton.class);
                if (buttons.length > 0 && uiCameraAction.type() != UICameraAction.ActionType.Dimmer) {
                    throw new RuntimeException("Method " + method.getName() + " annotated with @UICameraDimmerButton, but @UICameraAction has no dimmer type");
                }
                for (UICameraDimmerButton button : buttons) {
                    builder.lastAction.addButton(button.name(), button.icon());
                }
            }
        }
        return builder.get();
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

    public CameraActionBuilder add(String name, String group, String subGroup, boolean collapseGroup, String collapseGroupIcon,
                                   int order, String icon, String iconColor, UIFieldType type,
                                   JSONObject params, Consumer<String> action,
                                   Map<String, Consumer<StatefulContextMenuAction>> updateHandlers) {
        this.lastAction = new StatefulContextMenuAction(name, group, subGroup, collapseGroup, collapseGroupIcon,
                order, icon, iconColor, type, action, params, updateHandlers);
        actions.add(this.lastAction);
        return this;
    }

    public List<StatefulContextMenuAction> get() {
        return actions;
    }

    public void addAll(List<StatefulContextMenuAction> statefulContextMenuActions) {
        this.actions.addAll(statefulContextMenuActions);
    }
}
