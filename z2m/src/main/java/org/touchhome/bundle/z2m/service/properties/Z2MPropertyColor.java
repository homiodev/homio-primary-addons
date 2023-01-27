package org.touchhome.bundle.z2m.service.properties;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.z2m.service.Z2MDeviceService;
import org.touchhome.bundle.z2m.service.Z2MProperty;
import org.touchhome.bundle.z2m.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options;

public class Z2MPropertyColor extends Z2MProperty {

    public Z2MPropertyColor() {
        super("#FF009B", "fas fa-fw fa-palette");
    }

    public static int[] cieToRgb(float x, float y) {
        float tmpY = 1.0f;
        float tmpX = (tmpY / y) * x;
        float tmpZ = (tmpY / y) * (1.0f - x - y);

        float r = tmpX * XY2RGB[0][0] + tmpY * XY2RGB[0][1] + tmpZ * XY2RGB[0][2];
        float g = tmpX * XY2RGB[1][0] + tmpY * XY2RGB[1][1] + tmpZ * XY2RGB[1][2];
        float b = tmpX * XY2RGB[2][0] + tmpY * XY2RGB[2][1] + tmpZ * XY2RGB[2][2];

        float max = Math.max(r, g);
        if (b > max) {
            max = b;
        }

        r = gammaCompress(r / max);
        g = gammaCompress(g / max);
        b = gammaCompress(b / max);

        return new int[]{(int) (r * 255.0f + 0.5f), (int) (g * 255.0f + 0.5f), (int) (b * 255.0f + 0.5f)};
    }

    // Gamma compression (sRGB) for a single component, in the 0.0 - 1.0 range
    private static float gammaCompress(float c) {
        if (c < 0.0f) {
            c = 0.0f;
        } else if (c > 1.0f) {
            c = 1.0f;
        }

        return c <= 0.0031308f ? 12.92f * c : (1.0f + 0.055f) * (float) Math.pow(c, 1.0f / 2.4f) - 0.055f;
    }

    private static float[] rgbToCie(double red, double green, double blue) {
        // Apply a gamma correction to the RGB values, which makes the color more vivid and more the
        // like the color displayed on the screen of your device
        red = gammaDecompress(red);
        green = gammaDecompress(green);
        blue = gammaDecompress(blue);

        // RGB values to XYZ using the Wide RGB D65 conversion formula
        var X = red * 0.664511 + green * 0.154324 + blue * 0.162028;
        var Y = red * 0.283881 + green * 0.668433 + blue * 0.047685;
        var Z = red * 0.000088 + green * 0.072310 + blue * 0.986039;

        // Calculate the xy values from the XYZ values
        float x = (float) (X / (X + Y + Z));
        float y = (float) (Y / (X + Y + Z));

        return new float[]{Double.isNaN(x) ? 0 : x, Double.isNaN(y) ? 0 : y};
    }

    // Gamma decompression (sRGB) for a single component, in the 0.0 - 1.0 range
    private static double gammaDecompress(double c) {
        if (c < 0.0f) {
            c = 0.0f;
        } else if (c > 1.0f) {
            c = 1.0f;
        }

        return c <= 0.04045f ? c / 12.92 : (float) Math.pow((c + 0.055) / (1.0f + 0.055), 2.4);
    }

    /*public float[] rgbToCie(int reg, int green, int blue) {
        float r = gammaDecompress(reg / 100.0f);
        float g = gammaDecompress(green / 100.0f);
        float b = gammaDecompress(blue / 100.0f);

        float tmpX = r * RGB2XY[0][0] + g * RGB2XY[0][1] + b * RGB2XY[0][2];
        float tmpY = r * RGB2XY[1][0] + g * RGB2XY[1][1] + b * RGB2XY[1][2];
        float tmpZ = r * RGB2XY[2][0] + g * RGB2XY[2][1] + b * RGB2XY[2][2];

        float x = tmpX / (tmpX + tmpY + tmpZ);
        float y = tmpY / (tmpX + tmpY + tmpZ);

        return new float[]{x * 100.0f, y * 100.0f};
        *//*return new DecimalType[]{new DecimalType(x * 100.0f),
            new DecimalType(y * 100.0f),
            new DecimalType(tmpY * getBrightness().floatValue())};*//*
    }*/

    @Override
    public void init(Z2MDeviceService deviceService, Options expose) {
        switch (expose.getName()) {
            case "color_xy":
                this.dataReader =
                    payload -> {
                        JSONObject color = payload.getJSONObject("color");
                        float x = color.getFloat("x");
                        float y = color.getFloat("y");
                        return new StringType(xyToHex(x, y));
                    };
                break;
            case "color_hs":
                this.dataReader =
                    payload -> {
                        JSONObject color = payload.getJSONObject("color");
                        float hue = color.getFloat("hue");
                        float saturation = color.getFloat("saturation");
                        return new StringType(hsToHex(hue, saturation, getBrightness(payload)));
                    };
                break;
        }
        super.init(deviceService, expose);
    }

    private int getBrightness(JSONObject payload) {
        return payload.has("brightness")
            ? payload.getInt("brightness")
            : getDeviceService().getProperties().get("brightness").getInteger(255);
    }

    @Override
    public boolean isWritable() {
        List<Options> features = getExpose().getFeatures();
        if (features != null && !features.isEmpty()) {
            Map<String, Options> name2feature = features.stream().collect(Collectors.toMap(Options::getProperty, f -> f));
            switch (getExpose().getName()) {
                case "color_xy":
                    return name2feature.containsKey("x")
                        && name2feature.get("x").isWritable()
                        && name2feature.containsKey("y")
                        && name2feature.get("y").isWritable();
                case "color_hs":
                    return name2feature.containsKey("hue")
                        && name2feature.get("hue").isWritable()
                        && name2feature.containsKey("saturation")
                        && name2feature.get("saturation").isWritable();
            }
        }
        return false;
    }

    @Override
    public String getPropertyDefinition() {
        return "color";
    }

    public String getStateColor() {
        return getValue().stringValue().startsWith("#") ? getValue().stringValue() : "#FFFFFF";
    }

    private String hsToHex(float hue, float saturation, int brightness) {
        Color rgb = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return String.format("#%02x%02x%02x", rgb.getRed(), rgb.getGreen(), rgb.getBlue());
    }

    private String xyToHex(float x, float y) {
        int[] rgbValues = cieToRgb(x, y);
        return String.format("#%02x%02x%02x", rgbValues[0], rgbValues[1], rgbValues[2]);
    }

    @Override
    public void fireAction(String value) {
        JSONObject color = new JSONObject();
        Color rgbColor = Color.decode(value);

        switch (getExpose().getName()) {
            case "color_xy":
                float[] cie = rgbToCie(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());
                color.put("x", cie[0]).put("y", cie[1]);
                break;
            case "color_hs":
                float[] hsb = Color.RGBtoHSB(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue(), null);
                color.put("h", hsb[0]).put("s", hsb[1]).put("b", hsb[2]);
                return;
        }
        getDeviceService().publish("set", new JSONObject().put(expose.getProperty(), color));
    }

    // 1931 CIE XYZ to sRGB (D65 reference white)
    private static final float[][] XY2RGB = {{3.2406f, -1.5372f, -0.4986f}, {-0.9689f, 1.8758f, 0.0415f},
        {0.0557f, -0.2040f, 1.0570f}};
}
