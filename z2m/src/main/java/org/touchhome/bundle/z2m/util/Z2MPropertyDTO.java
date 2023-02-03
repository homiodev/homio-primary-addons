package org.touchhome.bundle.z2m.util;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import java.util.List;
import javax.measure.MetricPrefix;
import javax.measure.Prefix;
import javax.measure.Unit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Z2MPropertyDTO {

    private String name;
    private String icon;
    private String iconColor;
    private int order;

    @JsonDeserialize(converter = StringToUnitConverter.class)
    private Unit unit;

    private List<String> alias;

    public static class StringToUnitConverter extends StdConverter<String, Unit<?>> {

        @Override
        public Unit<?> convert(String value) {
            String[] split = value.split(":");
            String unitStr = split[0];
            Unit<?> unit = org.touchhome.bundle.api.util.Units.findUnit(unitStr);
            if (unit == null) {
                throw new IllegalArgumentException("Unable to find unit with name: " + unitStr);
            }
            if (split.length > 1) {
                unit = unit.prefix(getUnitPrefix(split[1]));
            }
            return unit;
        }

        private Prefix getUnitPrefix(String unitPrefix) {
            for (MetricPrefix metricPrefix : MetricPrefix.values()) {
                if (metricPrefix.getName().equals(unitPrefix)) {
                    return metricPrefix;
                }
            }
            throw new IllegalArgumentException("Unable to find unit prefix: " + unitPrefix);
        }
    }
}
