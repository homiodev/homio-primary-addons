package org.touchhome.bundle.camera.widget;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.widget.WidgetBaseEntityAndSeries;

import javax.persistence.Entity;
import javax.validation.constraints.Max;
import java.util.Set;

@Entity
public class WidgetCameraEntity extends WidgetBaseEntityAndSeries<WidgetCameraEntity, WidgetCameraSeriesEntity> {

    public static final String PREFIX = "wtcam_";

    @Max(4) // allow max 4 cameras
    public Set<WidgetCameraSeriesEntity> getSeries() {
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

    @Override
    public boolean updateRelations(EntityContext entityContext) {
        return false;
    }
}
