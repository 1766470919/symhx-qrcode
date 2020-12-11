package com.symhx.util;

import com.symhx.constans.MediaType;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lj
 * @date 2020/12/11
 */
public class BasicFileUtil {
    public BasicFileUtil() {
    }

    public static boolean isAbsFile(String fileName) {
        if (!OSUtil.isWinOS()) {
            return fileName.startsWith("/");
        } else {
            return fileName.contains(":") || fileName.startsWith("\\");
        }
    }

    public static String parseHomeDir2AbsDir(String path) {
        String homeDir = System.getProperties().getProperty("user.home");
        return StringUtils.replace(path, "~", homeDir);
    }

    public static MediaType getMediaType(String path) {
        String magicNum = FileReadUtil.getMagicNum(path);
        return magicNum == null ? null : MediaType.typeOfMagicNum(magicNum);
    }
}
