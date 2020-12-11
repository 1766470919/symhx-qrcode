package com.symhx.wrapper;

import com.google.zxing.EncodeHintType;
import com.symhx.constans.QuickQrUtil;
import com.symhx.entity.DotSize;
import com.symhx.gif.GifDecoder;
import com.symhx.helper.QrCodeRenderHelper.DetectLocation;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author lj
 * @date 2020/12/11
 */
public class QrCodeOptions {
    private String msg;
    private Integer w;
    private Integer h;
    private QrCodeOptions.DrawOptions drawOptions;
    private QrCodeOptions.BgImgOptions bgImgOptions;
    private QrCodeOptions.LogoOptions logoOptions;
    private QrCodeOptions.DetectOptions detectOptions;
    private Map<EncodeHintType, Object> hints;
    private String picType;

    public QrCodeOptions() {
    }

    public boolean gifQrCode() {
        return this.bgImgOptions != null && this.bgImgOptions.getGifDecoder() != null;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getW() {
        return this.w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    public Integer getH() {
        return this.h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public QrCodeOptions.DrawOptions getDrawOptions() {
        return this.drawOptions;
    }

    public void setDrawOptions(QrCodeOptions.DrawOptions drawOptions) {
        this.drawOptions = drawOptions;
    }

    public QrCodeOptions.BgImgOptions getBgImgOptions() {
        return this.bgImgOptions;
    }

    public void setBgImgOptions(QrCodeOptions.BgImgOptions bgImgOptions) {
        this.bgImgOptions = bgImgOptions;
    }

    public QrCodeOptions.LogoOptions getLogoOptions() {
        return this.logoOptions;
    }

    public void setLogoOptions(QrCodeOptions.LogoOptions logoOptions) {
        this.logoOptions = logoOptions;
    }

    public QrCodeOptions.DetectOptions getDetectOptions() {
        return this.detectOptions;
    }

    public void setDetectOptions(QrCodeOptions.DetectOptions detectOptions) {
        this.detectOptions = detectOptions;
    }

    public Map<EncodeHintType, Object> getHints() {
        return this.hints;
    }

    public void setHints(Map<EncodeHintType, Object> hints) {
        this.hints = hints;
    }

    public String getPicType() {
        return this.picType;
    }

    public void setPicType(String picType) {
        this.picType = picType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            QrCodeOptions options = (QrCodeOptions)o;
            return Objects.equals(this.msg, options.msg) && Objects.equals(this.w, options.w) && Objects.equals(this.h, options.h) && Objects.equals(this.drawOptions, options.drawOptions) && Objects.equals(this.bgImgOptions, options.bgImgOptions) && Objects.equals(this.logoOptions, options.logoOptions) && Objects.equals(this.detectOptions, options.detectOptions) && Objects.equals(this.hints, options.hints) && Objects.equals(this.picType, options.picType);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(new Object[]{this.msg, this.w, this.h, this.drawOptions, this.bgImgOptions, this.logoOptions, this.detectOptions, this.hints, this.picType});
    }

    @Override
    public String toString() {
        return "QrCodeOptions{msg='" + this.msg + '\'' + ", w=" + this.w + ", h=" + this.h + ", drawOptions=" + this.drawOptions + ", bgImgOptions=" + this.bgImgOptions + ", logoOptions=" + this.logoOptions + ", detectOptions=" + this.detectOptions + ", hints=" + this.hints + ", picType='" + this.picType + '\'' + '}';
    }

    public static enum TxtMode {
        RANDOM,
        ORDER;

        private TxtMode() {
        }
    }

    public static enum DrawStyle {
        RECT {
            @Override
            public void draw(Graphics2D g2d, int x, int y, int w, int h, BufferedImage img, String txt) {
                g2d.fillRect(x, y, w, h);
            }

            @Override
            public boolean expand(DotSize dotSize) {
                return dotSize.getRow() == dotSize.getCol();
            }
        },
        CIRCLE {
            @Override
            public void draw(Graphics2D g2d, int x, int y, int w, int h, BufferedImage img, String txt) {
                g2d.fill(new Ellipse2D.Float((float)x, (float)y, (float)w, (float)h));
            }

            @Override
            public boolean expand(DotSize dotSize) {
                return dotSize.getRow() == dotSize.getCol();
            }
        },
        TRIANGLE {
            @Override
            public void draw(Graphics2D g2d, int x, int y, int w, int h, BufferedImage img, String txt) {
                int[] px = new int[]{x, x + (w >> 1), x + w};
                int[] py = new int[]{y + w, y, y + w};
                g2d.fillPolygon(px, py, 3);
            }

            @Override
            public boolean expand(DotSize expandType) {
                return false;
            }
        },
        DIAMOND {
            @Override
            public void draw(Graphics2D g2d, int x, int y, int size, int h, BufferedImage img, String txt) {
                int cell4 = size >> 2;
                int cell2 = size >> 1;
                int[] px = new int[]{x + cell4, x + size - cell4, x + size, x + cell2, x};
                int[] py = new int[]{y, y, y + cell2, y + size, y + cell2};
                g2d.fillPolygon(px, py, 5);
            }

            @Override
            public boolean expand(DotSize dotSize) {
                return dotSize.getRow() == dotSize.getCol();
            }
        },
        SEXANGLE {
            @Override
            public void draw(Graphics2D g2d, int x, int y, int size, int h, BufferedImage img, String txt) {
                int add = size >> 2;
                int[] px = new int[]{x + add, x + size - add, x + size, x + size - add, x + add, x};
                int[] py = new int[]{y, y, y + add + add, y + size, y + size, y + add + add};
                g2d.fillPolygon(px, py, 6);
            }

            @Override
            public boolean expand(DotSize dotSize) {
                return dotSize.getRow() == dotSize.getCol();
            }
        },
        OCTAGON {
            @Override
            public void draw(Graphics2D g2d, int x, int y, int size, int h, BufferedImage img, String txt) {
                int add = size / 3;
                int[] px = new int[]{x + add, x + size - add, x + size, x + size, x + size - add, x + add, x, x};
                int[] py = new int[]{y, y, y + add, y + size - add, y + size, y + size, y + size - add, y + add};
                g2d.fillPolygon(px, py, 8);
            }

            @Override
            public boolean expand(DotSize dotSize) {
                return dotSize.getRow() == dotSize.getCol();
            }
        },
        IMAGE {
            @Override
            public void draw(Graphics2D g2d, int x, int y, int w, int h, BufferedImage img, String txt) {
                g2d.drawImage(img.getScaledInstance(w, h, 4), x, y, (ImageObserver)null);
            }

            @Override
            public boolean expand(DotSize expandType) {
                return true;
            }
        },
        TXT {
            @Override
            public void draw(Graphics2D g2d, int x, int y, int w, int h, BufferedImage img, String txt) {
                Font oldFont = g2d.getFont();
                if (oldFont.getSize() != w) {
                    Font newFont = QuickQrUtil.font(oldFont.getName(), oldFont.getStyle(), w);
                    g2d.setFont(newFont);
                }

                g2d.drawString(txt, x, y + w);
                g2d.setFont(oldFont);
            }

            @Override
            public boolean expand(DotSize dotSize) {
                return dotSize.getRow() == dotSize.getCol();
            }
        };

        private static Map<String, QrCodeOptions.DrawStyle> map = new HashMap(10);

        private DrawStyle() {
        }

        public static QrCodeOptions.DrawStyle getDrawStyle(String name) {
            if (StringUtils.isBlank(name)) {
                return RECT;
            } else {
                QrCodeOptions.DrawStyle style = (QrCodeOptions.DrawStyle)map.get(name.toUpperCase());
                return style == null ? RECT : style;
            }
        }

        public abstract void draw(Graphics2D var1, int var2, int var3, int var4, int var5, BufferedImage var6, String var7);

        public abstract boolean expand(DotSize var1);

        static {
            QrCodeOptions.DrawStyle[] var0 = values();
            int var1 = var0.length;

            for(int var2 = 0; var2 < var1; ++var2) {
                QrCodeOptions.DrawStyle style = var0[var2];
                map.put(style.name(), style);
            }

        }
    }

    public static enum BgImgStyle {
        OVERRIDE,
        FILL,
        PENETRATE;

        private BgImgStyle() {
        }

        public static QrCodeOptions.BgImgStyle getStyle(String name) {
            return "fill".equalsIgnoreCase(name) ? FILL : OVERRIDE;
        }
    }

    public static enum LogoStyle {
        ROUND,
        NORMAL,
        CIRCLE;

        private LogoStyle() {
        }

        public static QrCodeOptions.LogoStyle getStyle(String name) {
            return valueOf(name.toUpperCase());
        }
    }

    public static enum ImgStyle {
        /**
         * 圆角
         */
        ROUND,
        /**
         * 正常
         */
        NORMAL,
        /**
         * 圆形
         */
        CIRCLE;

        private ImgStyle() {
        }

        public static QrCodeOptions.ImgStyle getStyle(String name) {
            return valueOf(name.toUpperCase());
        }
    }

    public static class DrawOptions {
        private Color preColor;
        private Color bgColor;
        private BufferedImage bgImg;
        private QrCodeOptions.DrawStyle drawStyle;
        private String text;
        private String fontName;
        private QrCodeOptions.TxtMode txtMode;
        private int fontStyle;
        private boolean enableScale;
        private boolean diaphaneityFill;
        private Map<DotSize, BufferedImage> imgMapper;
        private QrCodeOptions.ImgStyle qrStyle;
        private java.lang.Float cornerRadius;

        public DrawOptions() {
        }

        public BufferedImage getImage(int row, int col) {
            return this.getImage(DotSize.create(row, col));
        }

        public BufferedImage getImage(DotSize dotSize) {
            return (BufferedImage)this.imgMapper.get(dotSize);
        }

        public String getDrawQrTxt() {
            return QuickQrUtil.qrTxt(this.text, this.txtMode != null && this.txtMode == QrCodeOptions.TxtMode.RANDOM);
        }

        public Color getPreColor() {
            return this.preColor;
        }

        public void setPreColor(Color preColor) {
            this.preColor = preColor;
        }

        public Color getBgColor() {
            return this.bgColor;
        }

        public void setBgColor(Color bgColor) {
            this.bgColor = bgColor;
        }

        public BufferedImage getBgImg() {
            return this.bgImg;
        }

        public void setBgImg(BufferedImage bgImg) {
            this.bgImg = bgImg;
        }

        public QrCodeOptions.DrawStyle getDrawStyle() {
            return this.drawStyle;
        }

        public void setDrawStyle(QrCodeOptions.DrawStyle drawStyle) {
            this.drawStyle = drawStyle;
        }

        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getFontName() {
            return this.fontName;
        }

        public void setFontName(String fontName) {
            this.fontName = fontName;
        }

        public QrCodeOptions.TxtMode getTxtMode() {
            return this.txtMode;
        }

        public void setTxtMode(QrCodeOptions.TxtMode txtMode) {
            this.txtMode = txtMode;
        }

        public int getFontStyle() {
            return this.fontStyle;
        }

        public void setFontStyle(int fontStyle) {
            this.fontStyle = fontStyle;
        }

        public boolean isEnableScale() {
            return this.enableScale;
        }

        public void setEnableScale(boolean enableScale) {
            this.enableScale = enableScale;
        }

        public boolean isDiaphaneityFill() {
            return this.diaphaneityFill;
        }

        public void setDiaphaneityFill(boolean diaphaneityFill) {
            this.diaphaneityFill = diaphaneityFill;
        }

        public Map<DotSize, BufferedImage> getImgMapper() {
            return this.imgMapper;
        }

        public void setImgMapper(Map<DotSize, BufferedImage> imgMapper) {
            this.imgMapper = imgMapper;
        }

        public QrCodeOptions.ImgStyle getQrStyle() {
            return this.qrStyle;
        }

        public void setQrStyle(QrCodeOptions.ImgStyle qrStyle) {
            this.qrStyle = qrStyle;
        }

        public java.lang.Float getCornerRadius() {
            return this.cornerRadius;
        }

        public void setCornerRadius(java.lang.Float cornerRadius) {
            this.cornerRadius = cornerRadius;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                QrCodeOptions.DrawOptions that = (QrCodeOptions.DrawOptions)o;
                return this.fontStyle == that.fontStyle && this.enableScale == that.enableScale && this.diaphaneityFill == that.diaphaneityFill && Objects.equals(this.preColor, that.preColor) && Objects.equals(this.bgColor, that.bgColor) && Objects.equals(this.bgImg, that.bgImg) && this.drawStyle == that.drawStyle && Objects.equals(this.text, that.text) && Objects.equals(this.fontName, that.fontName) && this.txtMode == that.txtMode && Objects.equals(this.imgMapper, that.imgMapper) && this.qrStyle == that.qrStyle && Objects.equals(this.cornerRadius, that.cornerRadius);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(new Object[]{this.preColor, this.bgColor, this.bgImg, this.drawStyle, this.text, this.fontName, this.txtMode, this.fontStyle, this.enableScale, this.diaphaneityFill, this.imgMapper, this.qrStyle, this.cornerRadius});
        }

        @Override
        public String toString() {
            return "DrawOptions{preColor=" + this.preColor + ", bgColor=" + this.bgColor + ", bgImg=" + this.bgImg + ", drawStyle=" + this.drawStyle + ", text='" + this.text + '\'' + ", fontName='" + this.fontName + '\'' + ", txtMode=" + this.txtMode + ", fontStyle=" + this.fontStyle + ", enableScale=" + this.enableScale + ", diaphaneityFill=" + this.diaphaneityFill + ", imgMapper=" + this.imgMapper + ", qrStyle=" + this.qrStyle + ", cornerRadius=" + this.cornerRadius + '}';
        }

        public static QrCodeOptions.DrawOptions.DrawOptionsBuilder builder() {
            return new QrCodeOptions.DrawOptions.DrawOptionsBuilder();
        }

        public static class DrawOptionsBuilder {
            private Color preColor;
            private Color bgColor;
            private boolean diaphaneityFill;
            private String text;
            private QrCodeOptions.TxtMode txtMode;
            private String fontName;
            private Integer fontStyle;
            private BufferedImage bgImg;
            private QrCodeOptions.DrawStyle drawStyle;
            private boolean enableScale;
            private Map<DotSize, BufferedImage> imgMapper = new HashMap();
            private QrCodeOptions.ImgStyle qrStyle;
            private java.lang.Float cornerRadius;

            public DrawOptionsBuilder() {
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder preColor(Color preColor) {
                this.preColor = preColor;
                return this;
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder bgColor(Color bgColor) {
                this.bgColor = bgColor;
                return this;
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder diaphaneityFill(boolean fill) {
                this.diaphaneityFill = fill;
                return this;
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder bgImg(BufferedImage image) {
                this.bgImg = image;
                return this;
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder drawStyle(QrCodeOptions.DrawStyle drawStyle) {
                this.drawStyle = drawStyle;
                return this;
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder text(String text) {
                this.text = text;
                return this;
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder txtMode(QrCodeOptions.TxtMode txtMode) {
                this.txtMode = txtMode;
                return this;
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder fontName(String fontName) {
                this.fontName = fontName;
                return this;
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder fontStyle(int fontStyle) {
                this.fontStyle = fontStyle;
                return this;
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder enableScale(boolean enableScale) {
                this.enableScale = enableScale;
                return this;
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder drawImg(int row, int column, BufferedImage image) {
                this.imgMapper.put(new DotSize(row, column), image);
                return this;
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder qrStyle(QrCodeOptions.ImgStyle qrStyle) {
                this.qrStyle = qrStyle;
                return this;
            }

            public QrCodeOptions.DrawOptions.DrawOptionsBuilder cornerRadius(java.lang.Float cornerRadius) {
                this.cornerRadius = cornerRadius;
                return this;
            }

            public QrCodeOptions.DrawOptions build() {
                QrCodeOptions.DrawOptions drawOptions = new QrCodeOptions.DrawOptions();
                drawOptions.setBgColor(this.bgColor);
                drawOptions.setBgImg(this.bgImg);
                drawOptions.setPreColor(this.preColor);
                drawOptions.setDrawStyle(this.drawStyle);
                drawOptions.setEnableScale(this.enableScale);
                drawOptions.setImgMapper(this.imgMapper);
                drawOptions.setDiaphaneityFill(this.diaphaneityFill);
                drawOptions.setText(this.text == null ? QuickQrUtil.DEFAULT_QR_TXT : this.text);
                drawOptions.setTxtMode(this.txtMode == null ? QrCodeOptions.TxtMode.ORDER : this.txtMode);
                drawOptions.setFontName(this.fontName == null ? QuickQrUtil.DEFAULT_FONT_NAME : this.fontName);
                drawOptions.setFontStyle(this.fontStyle == null ? QuickQrUtil.DEFAULT_FONT_STYLE : this.fontStyle);
                drawOptions.setQrStyle(this.qrStyle == null ? QrCodeOptions.ImgStyle.NORMAL : this.qrStyle);
                drawOptions.setCornerRadius(this.cornerRadius == null ? 0.125F : this.cornerRadius);
                return drawOptions;
            }
        }
    }

    public static class DetectOptions {
        private Color outColor;
        private Color inColor;
        private BufferedImage detectImg;
        private BufferedImage detectImgLT;
        private BufferedImage detectImgRT;
        private BufferedImage detectImgLD;
        private Boolean special;

        public Boolean getSpecial() {
            return BooleanUtils.isTrue(this.special);
        }

        public DetectOptions() {
        }

        public DetectOptions(Color outColor, Color inColor, BufferedImage detectImg, BufferedImage detectImgLT, BufferedImage detectImgRT, BufferedImage detectImgLD, Boolean special) {
            this.outColor = outColor;
            this.inColor = inColor;
            this.detectImg = detectImg;
            this.detectImgLT = detectImgLT;
            this.detectImgRT = detectImgRT;
            this.detectImgLD = detectImgLD;
            this.special = special;
        }

        public Color getOutColor() {
            return this.outColor;
        }

        public void setOutColor(Color outColor) {
            this.outColor = outColor;
        }

        public Color getInColor() {
            return this.inColor;
        }

        public void setInColor(Color inColor) {
            this.inColor = inColor;
        }

        public BufferedImage getDetectImg() {
            return this.detectImg;
        }

        public void setDetectImg(BufferedImage detectImg) {
            this.detectImg = detectImg;
        }

        public BufferedImage getDetectImgLT() {
            return this.detectImgLT;
        }

        public void setDetectImgLT(BufferedImage detectImgLT) {
            this.detectImgLT = detectImgLT;
        }

        public BufferedImage getDetectImgRT() {
            return this.detectImgRT;
        }

        public void setDetectImgRT(BufferedImage detectImgRT) {
            this.detectImgRT = detectImgRT;
        }

        public BufferedImage getDetectImgLD() {
            return this.detectImgLD;
        }

        public void setDetectImgLD(BufferedImage detectImgLD) {
            this.detectImgLD = detectImgLD;
        }

        public void setSpecial(Boolean special) {
            this.special = special;
        }

        public BufferedImage chooseDetectedImg(DetectLocation detectLocation) {
            switch(detectLocation) {
                case LD:
                    return this.detectImgLD == null ? this.detectImg : this.detectImgLD;
                case LT:
                    return this.detectImgLT == null ? this.detectImg : this.detectImgLT;
                case RT:
                    return this.detectImgRT == null ? this.detectImg : this.detectImgRT;
                default:
                    return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                QrCodeOptions.DetectOptions that = (QrCodeOptions.DetectOptions)o;
                return Objects.equals(this.outColor, that.outColor) && Objects.equals(this.inColor, that.inColor) && Objects.equals(this.detectImg, that.detectImg) && Objects.equals(this.detectImgLT, that.detectImgLT) && Objects.equals(this.detectImgRT, that.detectImgRT) && Objects.equals(this.detectImgLD, that.detectImgLD) && Objects.equals(this.special, that.special);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(new Object[]{this.outColor, this.inColor, this.detectImg, this.detectImgLT, this.detectImgRT, this.detectImgLD, this.special});
        }

        @Override
        public String toString() {
            return "DetectOptions{outColor=" + this.outColor + ", inColor=" + this.inColor + ", detectImg=" + this.detectImg + ", detectImgLT=" + this.detectImgLT + ", detectImgRT=" + this.detectImgRT + ", detectImgLD=" + this.detectImgLD + ", special=" + this.special + '}';
        }

        public static QrCodeOptions.DetectOptions.DetectOptionsBuilder builder() {
            return new QrCodeOptions.DetectOptions.DetectOptionsBuilder();
        }

        public static class DetectOptionsBuilder {
            private Color outColor;
            private Color inColor;
            private BufferedImage detectImg;
            private BufferedImage detectImgLT;
            private BufferedImage detectImgRT;
            private BufferedImage detectImgLD;
            private Boolean special;

            public DetectOptionsBuilder() {
            }

            public QrCodeOptions.DetectOptions.DetectOptionsBuilder outColor(Color outColor) {
                this.outColor = outColor;
                return this;
            }

            public QrCodeOptions.DetectOptions.DetectOptionsBuilder inColor(Color inColor) {
                this.inColor = inColor;
                return this;
            }

            public QrCodeOptions.DetectOptions.DetectOptionsBuilder detectImg(BufferedImage detectImg) {
                this.detectImg = detectImg;
                return this;
            }

            public QrCodeOptions.DetectOptions.DetectOptionsBuilder detectImgLT(BufferedImage detectImgLT) {
                this.detectImgLT = detectImgLT;
                return this;
            }

            public QrCodeOptions.DetectOptions.DetectOptionsBuilder detectImgRT(BufferedImage detectImgRT) {
                this.detectImgRT = detectImgRT;
                return this;
            }

            public QrCodeOptions.DetectOptions.DetectOptionsBuilder detectImgLD(BufferedImage detectImgLD) {
                this.detectImgLD = detectImgLD;
                return this;
            }

            public QrCodeOptions.DetectOptions.DetectOptionsBuilder special(Boolean special) {
                this.special = special;
                return this;
            }

            public QrCodeOptions.DetectOptions build() {
                return new QrCodeOptions.DetectOptions(this.outColor, this.inColor, this.detectImg, this.detectImgLT, this.detectImgRT, this.detectImgLD, this.special);
            }
        }
    }

    public static class BgImgOptions {
        private BufferedImage bgImg;
        private QrCodeOptions.ImgStyle imgStyle;
        private float radius;
        private GifDecoder gifDecoder;
        private int bgW;
        private int bgH;
        private QrCodeOptions.BgImgStyle bgImgStyle;
        private float opacity;
        private int startX;
        private int startY;

        public BgImgOptions() {
        }

        public BgImgOptions(BufferedImage bgImg, QrCodeOptions.ImgStyle imgStyle, float radius, GifDecoder gifDecoder, int bgW, int bgH, QrCodeOptions.BgImgStyle bgImgStyle, float opacity, int startX, int startY) {
            this.bgImg = bgImg;
            this.imgStyle = imgStyle;
            this.radius = radius;
            this.gifDecoder = gifDecoder;
            this.bgW = bgW;
            this.bgH = bgH;
            this.bgImgStyle = bgImgStyle;
            this.opacity = opacity;
            this.startX = startX;
            this.startY = startY;
        }

        public int getBgW() {
            if (this.bgImgStyle == QrCodeOptions.BgImgStyle.FILL && this.bgW == 0) {
                return this.bgImg != null ? this.bgImg.getWidth() : this.gifDecoder.getFrame(0).getWidth();
            } else {
                return this.bgW;
            }
        }

        public int getBgH() {
            if (this.bgImgStyle == QrCodeOptions.BgImgStyle.FILL && this.bgH == 0) {
                return this.bgImg != null ? this.bgImg.getHeight() : this.gifDecoder.getFrame(0).getHeight();
            } else {
                return this.bgH;
            }
        }

        public BufferedImage getBgImg() {
            return this.bgImg;
        }

        public void setBgImg(BufferedImage bgImg) {
            this.bgImg = bgImg;
        }

        public GifDecoder getGifDecoder() {
            return this.gifDecoder;
        }

        public void setGifDecoder(GifDecoder gifDecoder) {
            this.gifDecoder = gifDecoder;
        }

        public void setBgW(int bgW) {
            this.bgW = bgW;
        }

        public void setBgH(int bgH) {
            this.bgH = bgH;
        }

        public QrCodeOptions.BgImgStyle getBgImgStyle() {
            return this.bgImgStyle;
        }

        public void setBgImgStyle(QrCodeOptions.BgImgStyle bgImgStyle) {
            this.bgImgStyle = bgImgStyle;
        }

        public float getOpacity() {
            return this.opacity;
        }

        public void setOpacity(float opacity) {
            this.opacity = opacity;
        }

        public int getStartX() {
            return this.startX;
        }

        public void setStartX(int startX) {
            this.startX = startX;
        }

        public int getStartY() {
            return this.startY;
        }

        public void setStartY(int startY) {
            this.startY = startY;
        }

        public QrCodeOptions.ImgStyle getImgStyle() {
            return this.imgStyle;
        }

        public void setImgStyle(QrCodeOptions.ImgStyle imgStyle) {
            this.imgStyle = imgStyle;
        }

        public float getRadius() {
            return this.radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                QrCodeOptions.BgImgOptions that = (QrCodeOptions.BgImgOptions)o;
                return java.lang.Float.compare(that.radius, this.radius) == 0 && this.bgW == that.bgW && this.bgH == that.bgH && java.lang.Float.compare(that.opacity, this.opacity) == 0 && this.startX == that.startX && this.startY == that.startY && Objects.equals(this.bgImg, that.bgImg) && this.imgStyle == that.imgStyle && Objects.equals(this.gifDecoder, that.gifDecoder) && this.bgImgStyle == that.bgImgStyle;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(new Object[]{this.bgImg, this.imgStyle, this.radius, this.gifDecoder, this.bgW, this.bgH, this.bgImgStyle, this.opacity, this.startX, this.startY});
        }

        @Override
        public String toString() {
            return "BgImgOptions{bgImg=" + this.bgImg + ", imgStyle=" + this.imgStyle + ", radius=" + this.radius + ", gifDecoder=" + this.gifDecoder + ", bgW=" + this.bgW + ", bgH=" + this.bgH + ", bgImgStyle=" + this.bgImgStyle + ", opacity=" + this.opacity + ", startX=" + this.startX + ", startY=" + this.startY + '}';
        }

        public static QrCodeOptions.BgImgOptions.BgImgOptionsBuilder builder() {
            return new QrCodeOptions.BgImgOptions.BgImgOptionsBuilder();
        }

        public static class BgImgOptionsBuilder {
            private BufferedImage bgImg;
            private QrCodeOptions.ImgStyle imgStyle;
            private java.lang.Float cornerRadius;
            private GifDecoder gifDecoder;
            private int bgW;
            private int bgH;
            private QrCodeOptions.BgImgStyle bgImgStyle;
            private float opacity;
            private int startX;
            private int startY;

            public BgImgOptionsBuilder() {
            }

            public QrCodeOptions.BgImgOptions.BgImgOptionsBuilder bgImg(BufferedImage bgImg) {
                this.bgImg = bgImg;
                return this;
            }

            public QrCodeOptions.BgImgOptions.BgImgOptionsBuilder imgStyle(QrCodeOptions.ImgStyle imgStyle) {
                this.imgStyle = imgStyle;
                return this;
            }

            public QrCodeOptions.BgImgOptions.BgImgOptionsBuilder cornerRadius(float radius) {
                this.cornerRadius = radius;
                return this;
            }

            public QrCodeOptions.BgImgOptions.BgImgOptionsBuilder gifDecoder(GifDecoder gifDecoder) {
                this.gifDecoder = gifDecoder;
                return this;
            }

            public QrCodeOptions.BgImgOptions.BgImgOptionsBuilder bgW(int bgW) {
                this.bgW = bgW;
                return this;
            }

            public QrCodeOptions.BgImgOptions.BgImgOptionsBuilder bgH(int bgH) {
                this.bgH = bgH;
                return this;
            }

            public QrCodeOptions.BgImgOptions.BgImgOptionsBuilder bgImgStyle(QrCodeOptions.BgImgStyle bgImgStyle) {
                this.bgImgStyle = bgImgStyle;
                return this;
            }

            public QrCodeOptions.BgImgOptions.BgImgOptionsBuilder opacity(float opacity) {
                this.opacity = opacity;
                return this;
            }

            public QrCodeOptions.BgImgOptions.BgImgOptionsBuilder startX(int startX) {
                this.startX = startX;
                return this;
            }

            public QrCodeOptions.BgImgOptions.BgImgOptionsBuilder startY(int startY) {
                this.startY = startY;
                return this;
            }

            public QrCodeOptions.BgImgOptions build() {
                if (this.imgStyle == null) {
                    this.imgStyle = QrCodeOptions.ImgStyle.NORMAL;
                }

                if (this.cornerRadius == null) {
                    this.cornerRadius = 0.125F;
                }

                return new QrCodeOptions.BgImgOptions(this.bgImg, this.imgStyle, this.cornerRadius, this.gifDecoder, this.bgW, this.bgH, this.bgImgStyle, this.opacity, this.startX, this.startY);
            }
        }
    }

    public static class LogoOptions {
        private BufferedImage logo;
        private QrCodeOptions.LogoStyle logoStyle;
        private int rate;
        private boolean border;
        private Color borderColor;
        private Color outerBorderColor;
        private java.lang.Float opacity;

        public LogoOptions() {
        }

        public LogoOptions(BufferedImage logo, QrCodeOptions.LogoStyle logoStyle, int rate, boolean border, Color borderColor, Color outerBorderColor, java.lang.Float opacity) {
            this.logo = logo;
            this.logoStyle = logoStyle;
            this.rate = rate;
            this.border = border;
            this.borderColor = borderColor;
            this.outerBorderColor = outerBorderColor;
            this.opacity = opacity;
        }

        public BufferedImage getLogo() {
            return this.logo;
        }

        public void setLogo(BufferedImage logo) {
            this.logo = logo;
        }

        public QrCodeOptions.LogoStyle getLogoStyle() {
            return this.logoStyle;
        }

        public void setLogoStyle(QrCodeOptions.LogoStyle logoStyle) {
            this.logoStyle = logoStyle;
        }

        public int getRate() {
            return this.rate;
        }

        public void setRate(int rate) {
            this.rate = rate;
        }

        public boolean isBorder() {
            return this.border;
        }

        public void setBorder(boolean border) {
            this.border = border;
        }

        public Color getBorderColor() {
            return this.borderColor;
        }

        public void setBorderColor(Color borderColor) {
            this.borderColor = borderColor;
        }

        public Color getOuterBorderColor() {
            return this.outerBorderColor;
        }

        public void setOuterBorderColor(Color outerBorderColor) {
            this.outerBorderColor = outerBorderColor;
        }

        public java.lang.Float getOpacity() {
            return this.opacity;
        }

        public void setOpacity(java.lang.Float opacity) {
            this.opacity = opacity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                QrCodeOptions.LogoOptions that = (QrCodeOptions.LogoOptions)o;
                return this.rate == that.rate && this.border == that.border && Objects.equals(this.logo, that.logo) && this.logoStyle == that.logoStyle && Objects.equals(this.borderColor, that.borderColor) && Objects.equals(this.outerBorderColor, that.outerBorderColor) && Objects.equals(this.opacity, that.opacity);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(new Object[]{this.logo, this.logoStyle, this.rate, this.border, this.borderColor, this.outerBorderColor, this.opacity});
        }

        @Override
        public String toString() {
            return "LogoOptions{logo=" + this.logo + ", logoStyle=" + this.logoStyle + ", rate=" + this.rate + ", border=" + this.border + ", borderColor=" + this.borderColor + ", outerBorderColor=" + this.outerBorderColor + ", opacity=" + this.opacity + '}';
        }

        public static QrCodeOptions.LogoOptions.LogoOptionsBuilder builder() {
            return new QrCodeOptions.LogoOptions.LogoOptionsBuilder();
        }

        public static class LogoOptionsBuilder {
            private BufferedImage logo;
            private QrCodeOptions.LogoStyle logoStyle;
            private int rate;
            private boolean border;
            private Color borderColor;
            private Color outerBorderColor;
            private java.lang.Float opacity;

            public LogoOptionsBuilder() {
            }

            public QrCodeOptions.LogoOptions.LogoOptionsBuilder logo(BufferedImage logo) {
                this.logo = logo;
                return this;
            }

            public QrCodeOptions.LogoOptions.LogoOptionsBuilder logoStyle(QrCodeOptions.LogoStyle logoStyle) {
                this.logoStyle = logoStyle;
                return this;
            }

            public QrCodeOptions.LogoOptions.LogoOptionsBuilder rate(int rate) {
                this.rate = rate;
                return this;
            }

            public QrCodeOptions.LogoOptions.LogoOptionsBuilder border(boolean border) {
                this.border = border;
                return this;
            }

            public QrCodeOptions.LogoOptions.LogoOptionsBuilder borderColor(Color borderColor) {
                this.borderColor = borderColor;
                return this;
            }

            public QrCodeOptions.LogoOptions.LogoOptionsBuilder outerBorderColor(Color outerBorderColor) {
                this.outerBorderColor = outerBorderColor;
                return this;
            }

            public QrCodeOptions.LogoOptions.LogoOptionsBuilder opacity(java.lang.Float opacity) {
                this.opacity = opacity;
                return this;
            }

            public QrCodeOptions.LogoOptions build() {
                return new QrCodeOptions.LogoOptions(this.logo, this.logoStyle, this.rate, this.border, this.borderColor, this.outerBorderColor, this.opacity);
            }
        }
    }
}
