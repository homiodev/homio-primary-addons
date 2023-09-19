package org.homio.addon.z2m.service.endpoints;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.homio.addon.z2m.service.Z2MDeviceEndpoint;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.api.EntityContext;
import org.homio.api.model.Icon;
import org.homio.api.state.StringType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class Z2MDeviceEndpointColor extends Z2MDeviceEndpoint {

    // 1931 CIE XYZ to sRGB (D65 reference white)
    private static final float[][] XY2RGB = {
            {3.2406f, -1.5372f, -0.4986f},
            {-0.9689f, 1.8758f, 0.0415f},
            {0.0557f, -0.2040f, 1.0570f}};

    public Z2MDeviceEndpointColor(@NotNull EntityContext entityContext) {
        super(new Icon("fas fa-fw fa-palette", "#FF009B"), entityContext);
    }

    public static int[] cieToRgb(double x, double y) {
        double tmpY = 1.0f;
        double tmpX = (tmpY / y) * x;
        double tmpZ = (tmpY / y) * (1.0f - x - y);

        double r = tmpX * 1.656492 + tmpY * -0.354851 + tmpZ * -0.255038;
        double g = tmpX * -0.707196 + tmpY * 1.655397 + tmpZ * 0.036152;
        double b = tmpX * 0.051713 + tmpY * -0.121364 + tmpZ * 1.011530;

        double max = Math.max(r, g);
        if (b > max) {
            max = b;
        }

        r = gammaCompress(r / max);
        g = gammaCompress(g / max);
        b = gammaCompress(b / max);

        return new int[]{(int) (r * 255.0 + 0.5), (int) (g * 255.0 + 0.5), (int) (b * 255.0 + 0.5)};
    }

    private static String xyToHex(float x, float y) {
        int[] rgbValues = cieToRgb(x, y);
        return String.format("#%02x%02x%02x", rgbValues[0], rgbValues[1], rgbValues[2]);
    }

    // Gamma compression (sRGB) for a single component, in the 0.0 - 1.0 range
    private static double gammaCompress(double c) {
        if (c < 0.0f) {
            c = 0.0f;
        } else if (c > 1.0f) {
            c = 1.0f;
        }

        return (c > 0.04045) ? Math.pow((c + 0.055) / (1.0 + 0.055), 2.4) : (c / 12.92);
    }

    private static float[] rgbToCie(double red, double green, double blue) {
        // Apply a gamma correction to the RGB values, which makes the color more vivid and more the
        // like the color displayed on the screen of your device
        red = gammaDecompress(red / 255D);
        green = gammaDecompress(green / 255D);
        blue = gammaDecompress(blue / 255D);

        // RGB values to XYZ using the Wide RGB D65 conversion formula
        var X = red * 0.649926 + green * 0.103455 + blue * 0.197109;
        var Y = red * 0.234327 + green * 0.743075 + blue * 0.022598;
        var Z = red * 0.0000000 + green * 0.053077 + blue * 1.035763;

        // Calculate the xy values from the XYZ values
        float x = (float) (X / (X + Y + Z));
        float y = (float) (Y / (X + Y + Z));

        if (!xyIsInGamutRange(x, y, getGamutRanges()[3])) {
            // xy = getClosestColor(xy,colorGamut);
        }

        return new float[]{Double.isNaN(x) ? 0 : x, Double.isNaN(y) ? 0 : y};
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

    private static boolean xyIsInGamutRange(float x, float y, float[][] gamutRange) {
        float[] v0 = new float[]{gamutRange[2][0] - gamutRange[0][0], gamutRange[2][1] - gamutRange[0][1]};
        float[] v1 = new float[]{gamutRange[1][0] - gamutRange[0][0], gamutRange[1][1] - gamutRange[0][1]};
        float[] v2 = new float[]{x - gamutRange[0][0], y - gamutRange[0][1]};

        float dot00 = (v0[0] * v0[0]) + (v0[1] * v0[1]);
        float dot01 = (v0[0] * v1[0]) + (v0[1] * v1[1]);
        float dot02 = (v0[0] * v2[0]) + (v0[1] * v2[1]);
        float dot11 = (v1[0] * v1[0]) + (v1[1] * v1[1]);
        float dot12 = (v1[0] * v2[0]) + (v1[1] * v2[1]);

        float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);

        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        return ((u >= 0) && (v >= 0) && (u + v < 1));
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

    private static float[][][] getGamutRanges() {
        float[][][] gamutRanges = new float[4][3][2];
        // gamutA
        gamutRanges[0] = new float[3][2];
        gamutRanges[0][0] = new float[]{0.7040F, 0.2960F};
        gamutRanges[0][1] = new float[]{0.2151F, 0.7106F};
        gamutRanges[0][2] = new float[]{0.1380F, 0.0800F};

        // gamutB
        gamutRanges[1] = new float[3][2];
        gamutRanges[1][0] = new float[]{0.675F, 0.322F};
        gamutRanges[1][1] = new float[]{0.409F, 0.518F};
        gamutRanges[1][2] = new float[]{0.167F, 0.040F};

        // gamutC
        gamutRanges[2] = new float[3][2];
        gamutRanges[2][0] = new float[]{0.692F, 0.308F};
        gamutRanges[2][1] = new float[]{0.170F, 0.700F};
        gamutRanges[2][2] = new float[]{0.153F, 0.048F};

        // defaultGamut
        gamutRanges[3] = new float[3][2];
        gamutRanges[3][0] = new float[]{1F, 0F};
        gamutRanges[3][1] = new float[]{0F, 1F};
        gamutRanges[3][2] = new float[]{0F, 0F};

        return gamutRanges;
    }

    @Override
    public void init(@NotNull Z2MDeviceService deviceService, @NotNull ApplianceModel.Z2MDeviceDefinition.Options expose) {
        switch (expose.getName()) {
            case "color_xy" -> setDataReader(
                    payload -> {
                        JSONObject color = payload.getJSONObject("color");
                        float x = color.getFloat("x");
                        float y = color.getFloat("y");
                        return new StringType(xyToHex(x, y));
                    });
            case "color_hs" -> setDataReader(
                    payload -> {
                        JSONObject color = payload.getJSONObject("color");
                        float hue = color.getFloat("hue");
                        float saturation = color.getFloat("saturation");
                        return new StringType(hsToHex(hue, saturation, getBrightness(payload)));
                    });
        }
        super.init(deviceService, expose);
    }

    @Override
    public boolean isWritable() {
        List<Options> features = getExpose().getFeatures();
        if (features != null && !features.isEmpty()) {
            Map<String, Options> name2feature = features.stream().collect(Collectors.toMap(Options::getProperty, f -> f));
            switch (getExpose().getName()) {
                case "color_xy" -> {
                    return name2feature.containsKey("x")
                            && name2feature.get("x").isWritable()
                            && name2feature.containsKey("y")
                            && name2feature.get("y").isWritable();
                }
                case "color_hs" -> {
                    return name2feature.containsKey("hue")
                            && name2feature.get("hue").isWritable()
                            && name2feature.containsKey("saturation")
                            && name2feature.get("saturation").isWritable();
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull String getEndpointDefinition() {
        return "color";
    }

    public String getStateColor() {
        return getValue().stringValue().startsWith("#") ? getValue().stringValue() : "#FFFFFF";
    }

    @Override
    public void fireAction(String value) {
        JSONObject color = new JSONObject();
        Color rgbColor = Color.decode(value);

        switch (getExpose().getName()) {
            case "color_xy" ->
                /*float[] cie = rgbToCie(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());
                color.put("x", cie[0]).put("y", cie[1]);*/
                    color.put("r", rgbColor.getRed()).put("g", rgbColor.getGreen()).put("b", rgbColor.getBlue());
            case "color_hs" -> {
                float[] hsb = Color.RGBtoHSB(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue(), null);
                color.put("h", hsb[0]).put("s", hsb[1]).put("b", hsb[2]);
                return;
            }
        }
        getDeviceService().publish("set", new JSONObject().put(getExpose().getProperty(), color));
    }

    private int getBrightness(JSONObject payload) {
        return payload.has("brightness")
                ? payload.getInt("brightness")
                : getDeviceService().getEndpoints().get("brightness").getValue().intValue(255);
    }

    private String hsToHex(float hue, float saturation, int brightness) {
        Color rgb = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return String.format("#%02x%02x%02x", rgb.getRed(), rgb.getGreen(), rgb.getBlue());
    }
}
