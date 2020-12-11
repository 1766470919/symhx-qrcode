package com.symhx.util;

/**
 * @author lj
 * @date 2020/12/11
 */
public class OSUtil {
    public OSUtil() {
    }

    public static boolean isWinOS() {
        boolean isWinOS = false;

        try {
            String osName = System.getProperty("os.name").toLowerCase();
            String sharpOsName = osName.replaceAll("windows", "{windows}").replaceAll("^win([^a-z])", "{windows}$1").replaceAll("([^a-z])win([^a-z])", "$1{windows}$2");
            isWinOS = sharpOsName.contains("{windows}");
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return isWinOS;
    }
}
