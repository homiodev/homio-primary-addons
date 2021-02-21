package org.touchhome.bundle.camera.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UICameraAction {
    String name();

    int order();

    String icon();

    String iconColor() default "inherit";

    ActionType type() default ActionType.AutoDiscover;

    // for dimmer typ
    int min() default 0;

    // for dimmer typ
    int max() default 100;

    // uses if want replace dimmer with select box from min:
    String selectReplacer() default "";

    enum ActionType {
        Dimmer,
        String,
        Switch,
        AutoDiscover // discover on type of first parameter type in method
    }
}