package org.touchhome.bundle.zigbee.service.z2m.properties;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.zigbee.service.z2m.Z2MDeviceService;
import org.touchhome.bundle.zigbee.service.z2m.Z2MProperty;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options;

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

    /* private static float[] rgbToCie(double red, double green, double blue) {
        // Apply a gamma correction to the RGB values, which makes the color more vivid and more the
        // like the color displayed on the screen of your device
        red = (red > 0.04045) ? Math.pow((red + 0.055) / (1.0 + 0.055), 2.4) : (red / 12.92);
        green = (green > 0.04045) ? Math.pow((green + 0.055) / (1.0 + 0.055), 2.4) : (green / 12.92);
        blue = (blue > 0.04045) ? Math.pow((blue + 0.055) / (1.0 + 0.055), 2.4) : (blue / 12.92);

        // RGB values to XYZ using the Wide RGB D65 conversion formula
        var X = red * 0.664511 + green * 0.154324 + blue * 0.162028;
        var Y = red * 0.283881 + green * 0.668433 + blue * 0.047685;
        var Z = red * 0.000088 + green * 0.072310 + blue * 0.986039;

        // Calculate the xy values from the XYZ values
        float x = (float) (X / (X + Y + Z));
        float y = (float) (Y / (X + Y + Z));

        if (isNaN(x)) {
            x = 0;
        }
        if (isNaN(y)) {
            y = 0;
        }
        return new float[]{x, y};
    }*/

    /*private static double[] cieToRgb(double x, double y, Integer brightness) {
        // Set to maximum brightness if no custom value was given (Not the slick ECMAScript 6 way
        // for compatibility reasons)
        if (brightness == null) {
            brightness = 254;
        }

        double z = 1.0 - x - y;
        double Y = (brightness / 254D) *//*.toFixed(2)*//*;
        double X = (Y / y) * x;
        double Z = (Y / y) * z;

        // Convert to RGB using Wide RGB D65 conversion
        double red = X * 1.656492 - Y * 0.354851 - Z * 0.255038;
        double green = -X * 0.707196 + Y * 1.655397 + Z * 0.036152;
        double blue = X * 0.051713 - Y * 0.121364 + Z * 1.011530;

        // If red, green or blue is larger than 1.0 set it back to the maximum of 1.0
        if (red > blue && red > green && red > 1.0) {
            green = green / red;
            blue = blue / red;
            red = 1.0f;
        } else if (green > blue && green > red && green > 1.0) {
            red = red / green;
            blue = blue / green;
            green = 1.0f;
        } else if (blue > red && blue > green && blue > 1.0) {
            red = red / blue;
            green = green / blue;
            blue = 1.0f;
        }

        // Reverse gamma correction
        red = red <= 0.0031308 ? 12.92 * red : (1.0 + 0.055) * Math.pow(red, (1.0 / 2.4)) - 0.055;
        green = green <= 0.0031308 ? 12.92 * green : (1.0 + 0.055) * Math.pow(green, (1.0 / 2.4)) - 0.055;
        blue = blue <= 0.0031308 ? 12.92 * blue : (1.0 + 0.055) * Math.pow(blue, (1.0 / 2.4)) - 0.055;

        // Convert normalized decimal to decimal
        red = Math.round(red * 255);
        green = Math.round(green * 255);
        blue = Math.round(blue * 255);

        return new double[]{red, green, blue};
    }*/

    // Gamma decompression (sRGB) for a single component, in the 0.0 - 1.0 range
    private static float gammaDecompress(float c) {
        if (c < 0.0f) {
            c = 0.0f;
        } else if (c > 1.0f) {
            c = 1.0f;
        }

        return c <= 0.04045f ? c / 12.92f : (float) Math.pow((c + 0.055f) / (1.0f + 0.055f), 2.4f);
    }

    public float[] rgbToCie(int reg, int green, int blue) {
        float r = gammaDecompress(reg / 100.0f);
        float g = gammaDecompress(green / 100.0f);
        float b = gammaDecompress(blue / 100.0f);

        float tmpX = r * RGB2XY[0][0] + g * RGB2XY[0][1] + b * RGB2XY[0][2];
        float tmpY = r * RGB2XY[1][0] + g * RGB2XY[1][1] + b * RGB2XY[1][2];
        float tmpZ = r * RGB2XY[2][0] + g * RGB2XY[2][1] + b * RGB2XY[2][2];

        float x = tmpX / (tmpX + tmpY + tmpZ);
        float y = tmpY / (tmpX + tmpY + tmpZ);

        return new float[]{x * 100.0f, y * 100.0f};
        /*return new DecimalType[]{new DecimalType(x * 100.0f),
            new DecimalType(y * 100.0f),
            new DecimalType(tmpY * getBrightness().floatValue())};*/
    }

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
    // sRGB to 1931 CIE XYZ (D65 reference white)
    private static final float[][] RGB2XY = {{0.4124f, 0.3576f, 0.1805f}, {0.2126f, 0.7152f, 0.0722f},
        {0.0193f, 0.1192f, 0.9505f}};
}
