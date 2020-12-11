package com.symhx.util;

public class NumUtil {
    public NumUtil() {
    }

    public static Integer decode2int(String str, Integer defaultValue) {
        if (str == null) {
            return defaultValue;
        } else {
            try {
                return Long.decode(str).intValue();
            } catch (Exception var3) {
                return defaultValue;
            }
        }
    }

    public static String toHex(int hex) {
        String str = Integer.toHexString(hex);
        return str.length() == 1 ? "0" + str : str;
    }
}
