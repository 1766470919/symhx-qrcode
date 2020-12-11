package com.symhx.util;


import com.symhx.constans.MediaType;

public class DomUtil {
    public DomUtil() {
    }

    public static String toDomSrc(String base64str, MediaType mediaType) {
        return mediaType.getPrefix() + base64str;
    }
}
