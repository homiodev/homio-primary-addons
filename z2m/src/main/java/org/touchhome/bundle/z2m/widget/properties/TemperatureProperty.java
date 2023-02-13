package org.touchhome.bundle.z2m.widget.properties;

import org.touchhome.bundle.api.EntityContextWidget.DisplayWidgetSeriesBuilder;
import org.touchhome.bundle.api.EntityContextWidget.ThresholdBuilder.ValueCompare;
import org.touchhome.bundle.api.ui.UI.Color;

public class TemperatureProperty implements PropertyBuilder {

    @Override
    public void build(DisplayWidgetSeriesBuilder seriesBuilder) {
        seriesBuilder.setIcon("fas fa-temperature-full", iconBuilder -> {
            iconBuilder.setThreshold("fas fa-temperature-three-quarters", 20, ValueCompare.lt)
                       .setThreshold("fas fa-temperature-half", 15, ValueCompare.lt)
                       .setThreshold("fas fa-temperature-quarter", 10, ValueCompare.lt)
                       .setThreshold("fas fa-temperature-empty", 5, ValueCompare.lt);
            seriesBuilder.setIconColor(Color.BLUE, colorBuilder -> {
                colorBuilder.setThreshold(Color.WARNING, 15, ValueCompare.lt);
                colorBuilder.setThreshold(Color.RED, 10, ValueCompare.lt);
            });
        });
    }
}
