package org.touchhome.bundle.z2m.widget.properties;

import org.touchhome.bundle.api.EntityContextWidget.HasIcon;
import org.touchhome.bundle.api.EntityContextWidget.ValueCompare;
import org.touchhome.bundle.api.ui.UI.Color;

public class HumidityIconBuilder implements IconBuilder {

    @Override
    public void build(HasIcon<?> iconWidgetBuilder) {
        iconWidgetBuilder.setIcon("fas fa-droplet", iconBuilder -> {
            iconBuilder.setThreshold("fas fa-cloud-rain", 55, ValueCompare.gt)
                       .setThreshold("fas fa-fire", 30, ValueCompare.lt);
            iconWidgetBuilder.setIconColor(Color.GREEN, colorBuilder -> {
                colorBuilder.setThreshold(Color.WARNING, 30, ValueCompare.lt);
                colorBuilder.setThreshold(Color.RED, 55, ValueCompare.gt);
            });
        });
    }
}