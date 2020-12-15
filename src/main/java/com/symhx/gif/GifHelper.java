package com.symhx.gif;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import com.symhx.util.FileReadUtil;
import com.symhx.util.FileWriteUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class GifHelper {
    public GifHelper() {
    }

    public static int loadGif(String gif, List<BufferedImage> list) throws IOException {
        return loadGif(FileReadUtil.getStreamByFileName(gif), list);
    }

    public static int loadGif(InputStream stream, List<BufferedImage> list) {
        GifDecoder decoder = new GifDecoder();
        decoder.read(stream);
        int delay = 100;

        for(int i = 0; i < decoder.getFrameCount(); ++i) {
            if (delay > decoder.getDelay(i)) {
                delay = decoder.getDelay(i);
            }

            list.add(decoder.getFrame(i));
        }

        return delay;
    }

    public static void saveGif(List<BufferedImage> frames, int delay, String out) throws FileNotFoundException {
        FileWriteUtil.mkDir((new File(out)).getParentFile());
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(out));
        saveGif(frames, delay, (OutputStream)outputStream);
    }

    public static void saveGif(List<BufferedImage> frames, int delay, OutputStream out) {
        GifEncoder encoder = new GifEncoder();
        encoder.setRepeat(0);
        encoder.start(out);
        encoder.setDelay(delay);
        Iterator var4 = frames.iterator();

        while(var4.hasNext()) {
            BufferedImage img = (BufferedImage)var4.next();
            encoder.setDelay(delay);
            encoder.addFrame(img);
        }

        encoder.addFrame((BufferedImage)frames.get(frames.size() - 1));
        encoder.finish();
    }

    public static void saveGif(List<ImmutablePair<BufferedImage, Integer>> frames, OutputStream out) {
        GifEncoder encoder = new GifEncoder();
        encoder.setRepeat(0);
        encoder.start(out);
        Iterator var3 = frames.iterator();

        while(var3.hasNext()) {
            ImmutablePair<BufferedImage, Integer> frame = (ImmutablePair)var3.next();
            encoder.setDelay((Integer)frame.getRight());
            encoder.addFrame((BufferedImage)frame.getLeft());
        }

        encoder.finish();
    }
}
