package com.symhx.util;

import com.symhx.gif.GifDecoder;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lj
 * @date 2020/12/11
 */
public class ImageLoadUtil {
    public ImageLoadUtil() {
    }

    public static BufferedImage getImageByPath(String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            return null;
        } else {
            InputStream stream = FileReadUtil.getStreamByFileName(path);
            return ImageIO.read(stream);
        }
    }

    public static GifDecoder getGifByPath(String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            return null;
        } else {
            GifDecoder decoder = new GifDecoder();
            decoder.read(FileReadUtil.getStreamByFileName(path));
            return decoder;
        }
    }
}
