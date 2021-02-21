package org.touchhome.bundle.camera.ui;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.json.JSONObject;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.impl.StatefulContextMenuAction;
import org.touchhome.bundle.api.util.TouchHomeUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Log4j2
public class CameraActionBuilder {

    private List<StatefulContextMenuAction> actions = new ArrayList<>();
    private StatefulContextMenuAction lastAction;

    public static CameraActionBuilder builder() {
        return new CameraActionBuilder();
    }

    public static List<StatefulContextMenuAction> assemble(Object instance, Object conditionalInstance) {
        CameraActionBuilder builder = builder();
        Set<String> handledMethods = new HashSet<>();
        for (Method method : MethodUtils.getMethodsWithAnnotation(instance.getClass(), UICameraAction.class, true, false)) {
            UICameraActionConditional cameraActionConditional = method.getDeclaredAnnotation(UICameraActionConditional.class);
            if (handledMethods.add(method.getName()) && (cameraActionConditional == null ||
                    TouchHomeUtils.newInstance(cameraActionConditional.value()).test(conditionalInstance))) {

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

                UIFieldType type = uiCameraAction.type() == UICameraAction.ActionType.AutoDiscover ?
                        getFieldTypeFromMethod(actionParameter) : uiCameraAction.type() == UICameraAction.ActionType.Dimmer
                        ? UIFieldType.Slider : uiCameraAction.type() == UICameraAction.ActionType.Switch
                        ? UIFieldType.Boolean : UIFieldType.String;
                Method getter = findGetter(instance, uiCameraAction.name());
                builder.add(uiCameraAction.name(), uiCameraAction.order(), uiCameraAction.icon(),
                        uiCameraAction.iconColor(), type,
                        options, param -> {
                            try {
                                method.invoke(instance, actionParameterConverter.apply(param));
                            } catch (Exception ex) {
                                log.error("Unable to invoke camera action: <{}>", TouchHomeUtils.getErrorMessage(ex));
                            }
                        }, () -> {
                            try {
                                if (getter != null) {
                                    Object value = getter.invoke(instance);
                                    return value == null ? null : value.toString();
                                }
                                return null;
                            } catch (Exception ex) {
                                log.error("Unable to fetch getter value for action: <{}>. Msg: <{}>",
                                        uiCameraAction.name(), TouchHomeUtils.getErrorMessage(ex));
                            }
                            return null;
                        });
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
        return command -> command;
    }

    private static UIFieldType getFieldTypeFromMethod(Parameter parameter) {
        switch (parameter.getType().getSimpleName()) {
            case "boolean":
                return UIFieldType.Boolean;
            case "int":
                return UIFieldType.Slider;
        }
        return UIFieldType.String;
    }

    public CameraActionBuilder add(String name, int order, String icon, String iconColor, UIFieldType type,
                                   JSONObject params, Consumer<String> action,
                                   Supplier<String> getter) {
        this.lastAction = new StatefulContextMenuAction(name, order, icon, iconColor, type, action, params, getter);
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
