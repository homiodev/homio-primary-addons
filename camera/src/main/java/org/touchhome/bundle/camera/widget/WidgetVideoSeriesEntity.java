package org.touchhome.bundle.camera.widget;

import org.springframework.data.util.Pair;
import org.touchhome.bundle.api.entity.types.BaseVideoStreamEntity;
import org.touchhome.bundle.api.entity.widget.WidgetSeriesEntity;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.model.StylePosition;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class WidgetVideoSeriesEntity extends WidgetSeriesEntity<WidgetVideoEntity> {

    public static final String PREFIX = "wtvidser_";

    @UIField(order = 14, required = true, label = "widget.video_dataSource")
    @UIFieldSelection(VideoSeriesDataSourceDynamicOptionLoader.class)
    public String getDataSource() {
        return getJsonData("ds");
    }

    public StylePosition getActionPosition() {
        return getJsonDataEnum("actionPosition", StylePosition.TopRight);
    }

    public WidgetVideoSeriesEntity setActionPosition(StylePosition value) {
        setJsonData("actionPosition", value);
        return this;
    }

    @Override
    public String getEntityPrefix() {
        return PREFIX;
    }

    public static class VideoSeriesDataSourceDynamicOptionLoader implements DynamicOptionLoader {

        @Override
        public List<OptionModel> loadOptions(DynamicOptionLoaderParameters parameters) {
            List<OptionModel> list = new ArrayList<>();
            for (BaseVideoStreamEntity entity : parameters.getEntityContext().findAll(BaseVideoStreamEntity.class)) {
                Collection<Pair<String, String>> sources = entity.getVideoSources();
                for (Pair<String, String> source : sources) {
                    list.add(OptionModel.of(entity.getEntityID() + "~~~" + source.getFirst(), entity.getTitle() + "/" + source.getSecond()));
                }
            }
            return list;
        }
    }
}
