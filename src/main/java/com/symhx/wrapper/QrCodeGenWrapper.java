package com.symhx.wrapper;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import com.symhx.constans.MediaType;
import com.symhx.constans.QuickQrUtil;
import com.symhx.gif.GifDecoder;
import com.symhx.gif.GifHelper;
import com.symhx.helper.QrCodeGenerateHelper;
import com.symhx.util.Base64Util;
import com.symhx.util.ColorUtil;
import com.symhx.util.FileReadUtil;
import com.symhx.util.FileWriteUtil;
import com.symhx.util.ImageLoadUtil;
import com.symhx.util.IoUtil;
import com.symhx.wrapper.QrCodeOptions.BgImgOptions;
import com.symhx.wrapper.QrCodeOptions.BgImgStyle;
import com.symhx.wrapper.QrCodeOptions.DetectOptions;
import com.symhx.wrapper.QrCodeOptions.DrawOptions;
import com.symhx.wrapper.QrCodeOptions.DrawStyle;
import com.symhx.wrapper.QrCodeOptions.ImgStyle;
import com.symhx.wrapper.QrCodeOptions.LogoOptions;
import com.symhx.wrapper.QrCodeOptions.LogoStyle;
import com.symhx.wrapper.QrCodeOptions.TxtMode;
import com.symhx.wrapper.QrCodeOptions.BgImgOptions.BgImgOptionsBuilder;
import com.symhx.wrapper.QrCodeOptions.DetectOptions.DetectOptionsBuilder;
import com.symhx.wrapper.QrCodeOptions.DrawOptions.DrawOptionsBuilder;
import com.symhx.wrapper.QrCodeOptions.LogoOptions.LogoOptionsBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QrCodeGenWrapper {
    public QrCodeGenWrapper() {
    }

    public static QrCodeGenWrapper.Builder of(String content) {
        return (new QrCodeGenWrapper.Builder()).setMsg(content);
    }

    private static ByteArrayOutputStream asGif(QrCodeOptions qrCodeOptions) throws WriterException {
        ByteArrayOutputStream var4;
        try {
            BitMatrixEx bitMatrix = QrCodeGenerateHelper.encode(qrCodeOptions);
            List<ImmutablePair<BufferedImage, Integer>> list = QrCodeGenerateHelper.toGifImages(qrCodeOptions, bitMatrix);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            GifHelper.saveGif(list, outputStream);
            var4 = outputStream;
        } finally {
            QuickQrUtil.clear();
        }

        return var4;
    }

    private static BufferedImage asBufferedImage(QrCodeOptions qrCodeOptions) throws WriterException, IOException {
        BufferedImage var2;
        try {
            BitMatrixEx bitMatrix = QrCodeGenerateHelper.encode(qrCodeOptions);
            var2 = QrCodeGenerateHelper.toBufferedImage(qrCodeOptions, bitMatrix);
        } finally {
            QuickQrUtil.clear();
        }

        return var2;
    }

    private static String asString(QrCodeOptions qrCodeOptions) throws WriterException, IOException {
        if (qrCodeOptions.gifQrCode()) {
            ByteArrayOutputStream outputStream = asGif(qrCodeOptions);
            Throwable var31 = null;

            String var32;
            try {
                var32 = Base64Util.encode(outputStream);
            } catch (Throwable var26) {
                var31 = var26;
                throw var26;
            } finally {
                if (outputStream != null) {
                    if (var31 != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable var25) {
                            var31.addSuppressed(var25);
                        }
                    } else {
                        outputStream.close();
                    }
                }

            }

            return var32;
        } else {
            BufferedImage bufferedImage = asBufferedImage(qrCodeOptions);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Throwable var3 = null;

            String var4;
            try {
                ImageIO.write(bufferedImage, qrCodeOptions.getPicType(), outputStream);
                var4 = Base64Util.encode(outputStream);
            } catch (Throwable var27) {
                var3 = var27;
                throw var27;
            } finally {
                if (outputStream != null) {
                    if (var3 != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable var24) {
                            var3.addSuppressed(var24);
                        }
                    } else {
                        outputStream.close();
                    }
                }

            }

            return var4;
        }
    }

    private static boolean asFile(QrCodeOptions qrCodeOptions, String absFileName) throws WriterException, IOException {
        File file = new File(absFileName);
        FileWriteUtil.mkDir(file.getParentFile());
        if (qrCodeOptions.gifQrCode()) {
            ByteArrayOutputStream output = asGif(qrCodeOptions);
            Throwable var4 = null;

            try {
                FileOutputStream out = new FileOutputStream(file);
                out.write(output.toByteArray());
                out.flush();
                out.close();
            } catch (Throwable var13) {
                var4 = var13;
                throw var13;
            } finally {
                if (output != null) {
                    if (var4 != null) {
                        try {
                            output.close();
                        } catch (Throwable var12) {
                            var4.addSuppressed(var12);
                        }
                    } else {
                        output.close();
                    }
                }

            }

            return true;
        } else {
            BufferedImage bufferedImage = asBufferedImage(qrCodeOptions);
            if (!ImageIO.write(bufferedImage, qrCodeOptions.getPicType(), file)) {
                throw new IOException("save QrCode image to: " + absFileName + " error!");
            } else {
                return true;
            }
        }
    }

    public static class Builder {
        private static Logger log = LoggerFactory.getLogger(QrCodeGenWrapper.Builder.class);
        private static final MatrixToImageConfig DEFAULT_CONFIG = new MatrixToImageConfig();
        private String msg;
        private Integer w;
        private Integer h;
        private String code = "utf-8";
        private Integer padding;
        private ErrorCorrectionLevel errorCorrection;
        private String picType;
        private BgImgOptionsBuilder bgImgOptions;
        private LogoOptionsBuilder logoOptions;
        private DrawOptionsBuilder drawOptions;
        private DetectOptionsBuilder detectOptions;

        public Builder() {
            this.errorCorrection = ErrorCorrectionLevel.H;
            this.picType = "png";
            this.bgImgOptions = BgImgOptions.builder().bgImgStyle(BgImgStyle.OVERRIDE).opacity(0.85F);
            this.logoOptions = LogoOptions.builder().logoStyle(LogoStyle.NORMAL).border(false).rate(12);
            this.drawOptions = DrawOptions.builder().drawStyle(DrawStyle.RECT).bgColor(Color.WHITE).preColor(Color.BLACK).diaphaneityFill(false).enableScale(false);
            this.detectOptions = DetectOptions.builder();
        }

        public String getMsg() {
            return this.msg;
        }

        public QrCodeGenWrapper.Builder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Integer getW() {
            return this.w == null ? (this.h == null ? 200 : this.h) : this.w;
        }

        public QrCodeGenWrapper.Builder setW(Integer w) {
            if (w != null && w <= 0) {
                throw new IllegalArgumentException("生成二维码的宽必须大于0");
            } else {
                this.w = w;
                return this;
            }
        }

        public Integer getH() {
            return this.h == null ? (this.w == null ? 200 : this.w) : this.h;
        }

        public QrCodeGenWrapper.Builder setH(Integer h) {
            if (h != null && h <= 0) {
                throw new IllegalArgumentException("生成功能二维码的搞必须大于0");
            } else {
                this.h = h;
                return this;
            }
        }

        public QrCodeGenWrapper.Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Integer getPadding() {
            if (this.padding == null) {
                return 1;
            } else if (this.padding < 0) {
                return 0;
            } else {
                return this.padding > 4 ? 4 : this.padding;
            }
        }

        public QrCodeGenWrapper.Builder setPadding(Integer padding) {
            this.padding = padding;
            return this;
        }

        public QrCodeGenWrapper.Builder setPicType(String picType) {
            this.picType = picType;
            return this;
        }

        public QrCodeGenWrapper.Builder setErrorCorrection(ErrorCorrectionLevel errorCorrection) {
            this.errorCorrection = errorCorrection;
            return this;
        }

        public QrCodeGenWrapper.Builder setQrStyle(ImgStyle qrStyle) {
            this.drawOptions.qrStyle(qrStyle);
            return this;
        }

        public QrCodeGenWrapper.Builder setQrCornerRadiusRate(float radius) {
            this.drawOptions.cornerRadius(radius);
            return this;
        }

        public QrCodeGenWrapper.Builder setLogo(String logo) throws IOException {
            try {
                return this.setLogo(ImageLoadUtil.getImageByPath(logo));
            } catch (IOException var3) {
                log.error("load logo error!", var3);
                throw new IOException("load logo error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setLogo(InputStream inputStream) throws IOException {
            try {
                return this.setLogo(ImageIO.read(inputStream));
            } catch (IOException var3) {
                log.error("load backgroundImg error!", var3);
                throw new IOException("load backgroundImg error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setLogo(BufferedImage img) {
            this.logoOptions.logo(img);
            return this;
        }

        public QrCodeGenWrapper.Builder setLogoStyle(QrCodeOptions.LogoStyle logoStyle) {
            this.logoOptions.logoStyle(logoStyle);
            return this;
        }

        public QrCodeGenWrapper.Builder setLogoBgColor(Integer color) {
            return color == null ? this : this.setLogoBgColor(ColorUtil.int2color(color));
        }

        public QrCodeGenWrapper.Builder setLogoBgColor(Color color) {
            this.logoOptions.border(true);
            this.logoOptions.borderColor(color);
            return this;
        }

        public QrCodeGenWrapper.Builder setLogoBorderBgColor(Integer color) {
            return color == null ? this : this.setLogoBorderBgColor(ColorUtil.int2color(color));
        }

        public QrCodeGenWrapper.Builder setLogoBorderBgColor(Color color) {
            this.logoOptions.border(true);
            this.logoOptions.outerBorderColor(color);
            return this;
        }

        public QrCodeGenWrapper.Builder setLogoBorder(boolean border) {
            this.logoOptions.border(border);
            return this;
        }

        public QrCodeGenWrapper.Builder setLogoRate(int rate) {
            this.logoOptions.rate(rate);
            return this;
        }

        public QrCodeGenWrapper.Builder setLogoOpacity(float opacity) {
            this.logoOptions.opacity(opacity);
            return this;
        }

        public QrCodeGenWrapper.Builder setBgImg(String bgImg) throws IOException {
            try {
                return this.setBgImg(FileReadUtil.getStreamByFileName(bgImg));
            } catch (IOException var3) {
                log.error("load backgroundImg error!", var3);
                throw new IOException("load backgroundImg error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setBgImg(InputStream inputStream) throws IOException {
            try {
                ByteArrayInputStream target = IoUtil.toByteArrayInputStream(inputStream);
                MediaType media = MediaType.typeOfMagicNum(FileReadUtil.getMagicNum(target));
                if (media == MediaType.ImageGif) {
                    GifDecoder gifDecoder = new GifDecoder();
                    gifDecoder.read(target);
                    this.bgImgOptions.gifDecoder(gifDecoder);
                    return this;
                } else {
                    return this.setBgImg(ImageIO.read(target));
                }
            } catch (IOException var5) {
                log.error("load backgroundImg error!", var5);
                throw new IOException("load backgroundImg error!", var5);
            }
        }

        public QrCodeGenWrapper.Builder setBgImg(BufferedImage bufferedImage) {
            this.bgImgOptions.bgImg(bufferedImage);
            return this;
        }

        public QrCodeGenWrapper.Builder setBgImgStyle(ImgStyle imgStyle) {
            this.bgImgOptions.imgStyle(imgStyle);
            return this;
        }

        public QrCodeGenWrapper.Builder setBgCornerRadiusRate(float radius) {
            this.bgImgOptions.cornerRadius(radius);
            return this;
        }

        public QrCodeGenWrapper.Builder setBgStyle(BgImgStyle bgImgStyle) {
            this.bgImgOptions.bgImgStyle(bgImgStyle);
            return this;
        }

        public QrCodeGenWrapper.Builder setBgW(int w) {
            this.bgImgOptions.bgW(w);
            return this;
        }

        public QrCodeGenWrapper.Builder setBgH(int h) {
            this.bgImgOptions.bgH(h);
            return this;
        }

        public QrCodeGenWrapper.Builder setBgOpacity(float opacity) {
            this.bgImgOptions.opacity(opacity);
            return this;
        }

        public QrCodeGenWrapper.Builder setBgStartX(int startX) {
            this.bgImgOptions.startX(startX);
            return this;
        }

        public QrCodeGenWrapper.Builder setBgStartY(int startY) {
            this.bgImgOptions.startY(startY);
            return this;
        }

        public QrCodeGenWrapper.Builder setDetectImg(String detectImg) throws IOException {
            try {
                return this.setDetectImg(ImageLoadUtil.getImageByPath(detectImg));
            } catch (IOException var3) {
                log.error("load detectImage error! e:{}", var3);
                throw new IOException("load detectImage error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setDetectImg(InputStream detectImg) throws IOException {
            try {
                return this.setDetectImg(ImageIO.read(detectImg));
            } catch (IOException var3) {
                log.error("load detectImage error! e:{}", var3);
                throw new IOException("load detectImage error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setDetectImg(BufferedImage detectImg) {
            this.detectOptions.detectImg(detectImg);
            this.detectOptions.special(true);
            return this;
        }

        public QrCodeGenWrapper.Builder setLTDetectImg(String detectImg) throws IOException {
            try {
                return this.setLTDetectImg(ImageLoadUtil.getImageByPath(detectImg));
            } catch (IOException var3) {
                log.error("load detectImage error! e:{}", var3);
                throw new IOException("load detectImage error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setLTDetectImg(InputStream detectImg) throws IOException {
            try {
                return this.setLTDetectImg(ImageIO.read(detectImg));
            } catch (IOException var3) {
                log.error("load detectImage error! e:{}", var3);
                throw new IOException("load detectImage error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setLTDetectImg(BufferedImage detectImg) {
            this.detectOptions.detectImgLT(detectImg);
            this.detectOptions.special(true);
            return this;
        }

        public QrCodeGenWrapper.Builder setRTDetectImg(String detectImg) throws IOException {
            try {
                return this.setRTDetectImg(ImageLoadUtil.getImageByPath(detectImg));
            } catch (IOException var3) {
                log.error("load detectImage error! e:{}", var3);
                throw new IOException("load detectImage error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setRTDetectImg(InputStream detectImg) throws IOException {
            try {
                return this.setRTDetectImg(ImageIO.read(detectImg));
            } catch (IOException var3) {
                log.error("load detectImage error! e:{}", var3);
                throw new IOException("load detectImage error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setRTDetectImg(BufferedImage detectImg) {
            this.detectOptions.detectImgRT(detectImg);
            this.detectOptions.special(true);
            return this;
        }

        public QrCodeGenWrapper.Builder setLDDetectImg(String detectImg) throws IOException {
            try {
                return this.setLDDetectImg(ImageLoadUtil.getImageByPath(detectImg));
            } catch (IOException var3) {
                log.error("load detectImage error! e:{}", var3);
                throw new IOException("load detectImage error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setLDDetectImg(InputStream detectImg) throws IOException {
            try {
                return this.setLDDetectImg(ImageIO.read(detectImg));
            } catch (IOException var3) {
                log.error("load detectImage error! e:{}", var3);
                throw new IOException("load detectImage error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setLDDetectImg(BufferedImage detectImg) {
            this.detectOptions.detectImgLD(detectImg);
            this.detectOptions.special(true);
            return this;
        }

        public QrCodeGenWrapper.Builder setDetectOutColor(Integer outColor) {
            return outColor == null ? this : this.setDetectOutColor(ColorUtil.int2color(outColor));
        }

        public QrCodeGenWrapper.Builder setDetectOutColor(Color outColor) {
            this.detectOptions.outColor(outColor);
            return this;
        }

        public QrCodeGenWrapper.Builder setDetectInColor(Integer inColor) {
            return inColor == null ? this : this.setDetectInColor(ColorUtil.int2color(inColor));
        }

        public QrCodeGenWrapper.Builder setDetectInColor(Color inColor) {
            this.detectOptions.inColor(inColor);
            return this;
        }

        public QrCodeGenWrapper.Builder setDetectSpecial() {
            this.detectOptions.special(true);
            return this;
        }

        public QrCodeGenWrapper.Builder setDrawStyle(String style) {
            return this.setDrawStyle(QrCodeOptions.DrawStyle.getDrawStyle(style));
        }

        public QrCodeGenWrapper.Builder setDrawStyle(QrCodeOptions.DrawStyle drawStyle) {
            this.drawOptions.drawStyle(drawStyle);
            return this;
        }

        public QrCodeGenWrapper.Builder setDiaphaneityFill(boolean fill) {
            this.drawOptions.diaphaneityFill(fill);
            return this;
        }

        public QrCodeGenWrapper.Builder setDrawPreColor(int color) {
            return this.setDrawPreColor(ColorUtil.int2color(color));
        }

        public QrCodeGenWrapper.Builder setDrawPreColor(Color color) {
            this.drawOptions.preColor(color);
            return this;
        }

        public QrCodeGenWrapper.Builder setDrawBgColor(int color) {
            return this.setDrawBgColor(ColorUtil.int2color(color));
        }

        public QrCodeGenWrapper.Builder setDrawBgColor(Color color) {
            this.drawOptions.bgColor(color);
            return this;
        }

        public QrCodeGenWrapper.Builder setDrawBgImg(String img) throws IOException {
            try {
                return this.setDrawBgImg(ImageLoadUtil.getImageByPath(img));
            } catch (IOException var3) {
                log.error("load drawBgImg error! e:{}", var3);
                throw new IOException("load drawBgImg error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setDrawBgImg(InputStream img) throws IOException {
            try {
                return this.setDrawBgImg(ImageIO.read(img));
            } catch (IOException var3) {
                log.error("load drawBgImg error! e:{}", var3);
                throw new IOException("load drawBgImg error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setDrawBgImg(BufferedImage img) {
            this.drawOptions.bgImg(img);
            return this;
        }

        public QrCodeGenWrapper.Builder setDrawEnableScale(boolean enable) {
            this.drawOptions.enableScale(enable);
            return this;
        }

        public QrCodeGenWrapper.Builder setDrawImg(String img) throws IOException {
            try {
                return this.setDrawImg(ImageLoadUtil.getImageByPath(img));
            } catch (IOException var3) {
                log.error("load draw img error! e: {}", var3);
                throw new IOException("load draw img error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setDrawImg(InputStream input) throws IOException {
            try {
                return this.setDrawImg(ImageIO.read(input));
            } catch (IOException var3) {
                log.error("load draw img error! e: {}", var3);
                throw new IOException("load draw img error!", var3);
            }
        }

        public QrCodeGenWrapper.Builder setDrawImg(BufferedImage img) {
            this.addImg(1, 1, (BufferedImage)img);
            return this;
        }

        public QrCodeGenWrapper.Builder addImg(int row, int col, BufferedImage img) {
            if (img == null) {
                return this;
            } else {
                this.drawOptions.enableScale(true);
                this.drawOptions.drawImg(row, col, img);
                return this;
            }
        }

        public QrCodeGenWrapper.Builder addImg(int row, int col, String img) throws IOException {
            try {
                return this.addImg(row, col, ImageLoadUtil.getImageByPath(img));
            } catch (IOException var5) {
                log.error("load draw size4img error! e: {}", var5);
                throw new IOException("load draw row:" + row + ", col:" + col + " img error!", var5);
            }
        }

        public QrCodeGenWrapper.Builder addImg(int row, int col, InputStream img) throws IOException {
            try {
                return this.addImg(row, col, ImageIO.read(img));
            } catch (IOException var5) {
                log.error("load draw size4img error! e: {}", var5);
                throw new IOException("load draw row:" + row + ", col:" + col + " img error!", var5);
            }
        }

        public QrCodeGenWrapper.Builder setQrText(String text) {
            this.drawOptions.text(text);
            return this;
        }

        public QrCodeGenWrapper.Builder setQrTxtMode(TxtMode txtMode) {
            this.drawOptions.txtMode(txtMode);
            return this;
        }

        public QrCodeGenWrapper.Builder setQrDotFontName(String fontName) {
            this.drawOptions.fontName(fontName);
            return this;
        }

        public QrCodeGenWrapper.Builder setQrDotFontStyle(int fontStyle) {
            this.drawOptions.fontStyle(fontStyle);
            return this;
        }

        private void validate() {
            if (this.msg == null || this.msg.length() == 0) {
                throw new IllegalArgumentException("生成二维码的内容不能为空!");
            }
        }

        private QrCodeOptions build() {
            this.validate();
            QrCodeOptions qrCodeConfig = new QrCodeOptions();
            qrCodeConfig.setMsg(this.getMsg());
            qrCodeConfig.setH(this.getH());
            qrCodeConfig.setW(this.getW());
            QrCodeOptions.BgImgOptions bgOp = this.bgImgOptions.build();
            if (bgOp.getBgImg() == null && bgOp.getGifDecoder() == null) {
                qrCodeConfig.setBgImgOptions((QrCodeOptions.BgImgOptions)null);
            } else {
                qrCodeConfig.setBgImgOptions(bgOp);
            }

            QrCodeOptions.LogoOptions logoOp = this.logoOptions.build();
            if (logoOp.getLogo() == null) {
                qrCodeConfig.setLogoOptions((QrCodeOptions.LogoOptions)null);
            } else {
                qrCodeConfig.setLogoOptions(logoOp);
            }

            QrCodeOptions.DrawOptions drawOp = this.drawOptions.build();
            qrCodeConfig.setDrawOptions(drawOp);
            QrCodeOptions.DetectOptions detectOp = this.detectOptions.build();
            if (detectOp.getOutColor() == null && detectOp.getInColor() == null) {
                detectOp.setInColor(drawOp.getPreColor());
                detectOp.setOutColor(drawOp.getPreColor());
            } else if (detectOp.getOutColor() == null) {
                detectOp.setOutColor(detectOp.getOutColor());
            } else if (detectOp.getInColor() == null) {
                detectOp.setInColor(detectOp.getInColor());
            }

            qrCodeConfig.setDetectOptions(detectOp);
            if (qrCodeConfig.getBgImgOptions() != null && qrCodeConfig.getBgImgOptions().getBgImgStyle() == BgImgStyle.PENETRATE) {
                drawOp.setPreColor(ColorUtil.OPACITY);
                qrCodeConfig.getBgImgOptions().setOpacity(1.0F);
                qrCodeConfig.getDetectOptions().setInColor(ColorUtil.OPACITY);
                qrCodeConfig.getDetectOptions().setOutColor(ColorUtil.OPACITY);
            }

            qrCodeConfig.setPicType(this.picType);
            Map<EncodeHintType, Object> hints = new HashMap(3);
            hints.put(EncodeHintType.ERROR_CORRECTION, this.errorCorrection);
            hints.put(EncodeHintType.CHARACTER_SET, this.code);
            hints.put(EncodeHintType.MARGIN, this.getPadding());
            qrCodeConfig.setHints(hints);
            return qrCodeConfig;
        }

        public String asString() throws IOException, WriterException {
            return QrCodeGenWrapper.asString(this.build());
        }

        public BufferedImage asBufferedImage() throws IOException, WriterException {
            return QrCodeGenWrapper.asBufferedImage(this.build());
        }

        public ByteArrayOutputStream asStream() throws WriterException, IOException {
            QrCodeOptions options = this.build();
            if (options.gifQrCode()) {
                return QrCodeGenWrapper.asGif(options);
            } else {
                BufferedImage img = QrCodeGenWrapper.asBufferedImage(options);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(img, options.getPicType(), outputStream);
                return outputStream;
            }
        }

        public boolean asFile(String absFileName) throws IOException, WriterException {
            return QrCodeGenWrapper.asFile(this.build(), absFileName);
        }

        @Override
        public String toString() {
            return "Builder{msg='" + this.msg + '\'' + ", w=" + this.w + ", h=" + this.h + ", code='" + this.code + '\'' + ", padding=" + this.padding + ", errorCorrection=" + this.errorCorrection + ", picType='" + this.picType + '\'' + ", bgImgOptions=" + this.bgImgOptions + ", logoOptions=" + this.logoOptions + ", drawOptions=" + this.drawOptions + ", detectOptions=" + this.detectOptions + '}';
        }
    }
}
