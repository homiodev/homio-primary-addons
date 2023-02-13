package org.touchhome.bundle.z2m.widget.properties;

import org.touchhome.bundle.api.EntityContextWidget.DisplayWidgetSeriesBuilder;
import org.touchhome.bundle.api.EntityContextWidget.ThresholdBuilder.ValueCompare;
import org.touchhome.bundle.api.ui.UI.Color;

public class HumidityProperty implements PropertyBuilder {

    @Override
    public void build(DisplayWidgetSeriesBuilder seriesBuilder) {
        seriesBuilder.setIcon("fas fa-droplet", iconBuilder -> {
            iconBuilder.setThreshold("fas fa-cloud-rain", 55, ValueCompare.gt)
                       .setThreshold("fas fa-fire", 30, ValueCompare.lt);
            seriesBuilder.setIconColor(Color.GREEN, colorBuilder -> {
                colorBuilder.setThreshold(Color.WARNING, 30, ValueCompare.lt);
                colorBuilder.setThreshold(Color.RED, 55, ValueCompare.gt);
            });
        });
    }
}
