package com.symhx.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D.Float;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class ImageOperateUtil {
    public ImageOperateUtil() {
    }

    public static BufferedImage makeRoundImg(BufferedImage image, boolean borderEnable, Color borderColor) {
        int size;
        int x;
        int y;
        if (image.getWidth() > image.getHeight()) {
            size = image.getHeight();
            y = 0;
            x = image.getWidth() - image.getHeight() >> 1;
        } else {
            size = image.getWidth();
            x = 0;
            y = image.getHeight() - image.getWidth() >> 1;
        }

        BufferedImage ans = makeRoundImg(image, new Rectangle(x, y, size, size), size);
        if (borderEnable) {
            ans = makeRoundBorder(ans, size, borderColor);
        }

        return ans;
    }

    public static BufferedImage makeRoundImg(BufferedImage image, Rectangle rectangle, int cornerRadius) {
        int x = (int)rectangle.getX();
        int y = (int)rectangle.getY();
        int w = (int)rectangle.getWidth();
        int h = (int)rectangle.getHeight();
        BufferedImage output = new BufferedImage(w, h, 2);
        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new Float(0.0F, 0.0F, (float)w, (float)h, (float)cornerRadius, (float)cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, -x, -y, (ImageObserver)null);
        g2.dispose();
        return output;
    }

    public static BufferedImage makeRoundBorder(BufferedImage image, int cornerRadius, Color color) {
        int size = image.getWidth() / 15;
        int w = image.getWidth() + size;
        int h = image.getHeight() + size;
        BufferedImage output = new BufferedImage(w, h, 2);
        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color == null ? Color.WHITE : color);
        g2.fill(new Float(0.0F, 0.0F, (float)w, (float)h, (float)cornerRadius, (float)cornerRadius));
        g2.setComposite(AlphaComposite.getInstance(10, 1.0F));
        g2.drawImage(image, size >> 1, size >> 1, (ImageObserver)null);
        g2.dispose();
        return output;
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, 2);
        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new Float(0.0F, 0.0F, (float)w, (float)h, (float)cornerRadius, (float)cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, (ImageObserver)null);
        g2.dispose();
        return output;
    }
}
