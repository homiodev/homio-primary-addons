package org.touchhome.bundle.camera.widget;

import org.touchhome.bundle.api.entity.widget.WidgetBaseEntityAndSeries;

import javax.persistence.Entity;
import javax.validation.constraints.Max;
import java.util.Set;

@Entity
public class WidgetVideoEntity extends WidgetBaseEntityAndSeries<WidgetVideoEntity, WidgetVideoSeriesEntity> {

    public static final String PREFIX = "wtvid_";

    @Max(4) // allow max 4 cameras
    public Set<WidgetVideoSeriesEntity> getSeries() {
        return super.getSeries();
    }

    @Override
    public String getImage() {
        return "fas fa-video";
    }

    @Override
    protected void beforePersist() {
        super.beforePersist();
        setBh(3);
        setBw(3);
    }

    @Override
    public String getEntityPrefix() {
        return PREFIX;
    }
}
