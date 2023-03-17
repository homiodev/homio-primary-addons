package org.touchhome.bundle.z2m.widget.properties;

import org.touchhome.bundle.api.EntityContextWidget.HasIcon;
import org.touchhome.bundle.api.EntityContextWidget.ValueCompare;
import org.touchhome.bundle.api.ui.UI.Color;

public class SignalIconBuilder implements IconBuilder {

    @Override
    public void build(HasIcon<?> iconWidgetBuilder) {
        iconWidgetBuilder.setIconColor(Color.GREEN, colorBuilder ->
            colorBuilder.setThreshold(Color.WARNING, 50, ValueCompare.lt)
                        .setThreshold(Color.RED, 0, ValueCompare.eq));
    }
}