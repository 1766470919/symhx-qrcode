package com.symhx.util;

import java.awt.Color;

public class ColorUtil {
    public static Color OPACITY = int2color(16777215);
    public static Color OFF_WHITE = int2color(-528682);

    public ColorUtil() {
    }

    public static Color int2color(int color) {
        int a = (-16777216 & color) >>> 24;
        int r = (16711680 & color) >> 16;
        int g = ('\uff00' & color) >> 8;
        int b = 255 & color;
        return new Color(r, g, b, a);
    }

    public static String int2htmlColor(int color) {
        int a = (-16777216 & color) >>> 24;
        int r = (16711680 & color) >> 16;
        int g = ('\uff00' & color) >> 8;
        int b = 255 & color;
        return "#" + NumUtil.toHex(r) + NumUtil.toHex(g) + NumUtil.toHex(b) + NumUtil.toHex(a);
    }
}
