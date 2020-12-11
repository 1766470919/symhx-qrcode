package com.symhx.helper;

import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.symhx.constans.QuickQrUtil;
import com.symhx.entity.DotSize;
import com.symhx.util.GraphicUtil;
import com.symhx.util.ImageOperateUtil;
import com.symhx.wrapper.BitMatrixEx;
import com.symhx.wrapper.QrCodeOptions;
import com.symhx.wrapper.QrCodeOptions.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author lj
 * @date 2020/12/11
 */
public class QrCodeRenderHelper {
    public QrCodeRenderHelper() {
    }

    public static BufferedImage drawLogo(BufferedImage qrImg, LogoOptions logoOptions) {
        int qrWidth = qrImg.getWidth();
        int qrHeight = qrImg.getHeight();
        BufferedImage logoImg = logoOptions.getLogo();
        int radius = 0;
        if (logoOptions.getLogoStyle() == LogoStyle.ROUND) {
            radius = logoImg.getWidth() >> 2;
            logoImg = ImageOperateUtil.makeRoundedCorner(logoImg, radius);
        } else if (logoOptions.getLogoStyle() == LogoStyle.CIRCLE) {
            radius = Math.min(logoImg.getWidth(), logoImg.getHeight());
            logoImg = ImageOperateUtil.makeRoundImg(logoImg, false, (Color)null);
        }

        if (logoOptions.isBorder()) {
            if (logoOptions.getOuterBorderColor() != null) {
                logoImg = ImageOperateUtil.makeRoundBorder(logoImg, radius, logoOptions.getOuterBorderColor());
            }

            logoImg = ImageOperateUtil.makeRoundBorder(logoImg, radius, logoOptions.getBorderColor());
        }

        int logoRate = logoOptions.getRate();
        int calculateQrLogoWidth = (qrWidth << 1) / logoRate;
        int calculateQrLogoHeight = (qrHeight << 1) / logoRate;
        int logoWidth;
        int logoHeight;
        if (calculateQrLogoWidth < logoImg.getWidth()) {
            logoWidth = calculateQrLogoWidth;
            logoHeight = calculateQrLogoWidth * logoImg.getHeight() / logoImg.getWidth();
        } else if (calculateQrLogoHeight < logoImg.getHeight()) {
            logoHeight = calculateQrLogoHeight;
            logoWidth = calculateQrLogoHeight * logoImg.getWidth() / logoImg.getHeight();
        } else {
            logoWidth = logoImg.getWidth();
            logoHeight = logoImg.getHeight();
        }

        int logoOffsetX = qrWidth - logoWidth >> 1;
        int logoOffsetY = qrHeight - logoHeight >> 1;
        Graphics2D qrImgGraphic = GraphicUtil.getG2d(qrImg);
        if (logoOptions.getOpacity() != null) {
            qrImgGraphic.setComposite(AlphaComposite.getInstance(10, logoOptions.getOpacity()));
        }

        qrImgGraphic.drawImage(logoImg.getScaledInstance(logoWidth, logoHeight, 4), logoOffsetX, logoOffsetY, (ImageObserver)null);
        qrImgGraphic.dispose();
        logoImg.flush();
        return qrImg;
    }

    public static BufferedImage drawBackground(BufferedImage qrImg, BgImgOptions bgImgOptions) {
        int qrWidth = qrImg.getWidth();
        int qrHeight = qrImg.getHeight();
        int bgW = Math.max(bgImgOptions.getBgW(), qrWidth);
        int bgH = Math.max(bgImgOptions.getBgH(), qrHeight);
        BufferedImage bgImg = bgImgOptions.getBgImg();
        if (bgImg.getWidth() != bgW || bgImg.getHeight() != bgH) {
            BufferedImage temp = new BufferedImage(bgW, bgH, 2);
            temp.getGraphics().drawImage(bgImg.getScaledInstance(bgW, bgH, 4), 0, 0, (ImageObserver)null);
            bgImg = temp;
        }

        if (bgImgOptions.getImgStyle() == ImgStyle.ROUND) {
            int cornerRadius = (int)((float)Math.min(bgW, bgH) * bgImgOptions.getRadius());
            bgImg = ImageOperateUtil.makeRoundedCorner(bgImg, cornerRadius);
        } else if (bgImgOptions.getImgStyle() == ImgStyle.CIRCLE) {
            bgImg = ImageOperateUtil.makeRoundImg(bgImg, false, (Color)null);
        }

        Graphics2D bgImgGraphic = GraphicUtil.getG2d(bgImg);
        if (bgImgOptions.getBgImgStyle() == BgImgStyle.FILL) {
            bgImgGraphic.setComposite(AlphaComposite.getInstance(10, 1.0F));
            bgImgGraphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            bgImgGraphic.drawImage(qrImg.getScaledInstance(qrWidth, qrHeight, 4), bgImgOptions.getStartX(), bgImgOptions.getStartY(), (ImageObserver)null);
        } else {
            int bgOffsetX = bgW - qrWidth >> 1;
            int bgOffsetY = bgH - qrHeight >> 1;
            bgImgGraphic.setComposite(AlphaComposite.getInstance(10, bgImgOptions.getOpacity()));
            bgImgGraphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            bgImgGraphic.drawImage(qrImg.getScaledInstance(qrWidth, qrHeight, 4), bgOffsetX, bgOffsetY, (ImageObserver)null);
            bgImgGraphic.setComposite(AlphaComposite.getInstance(10, 1.0F));
        }

        bgImgGraphic.dispose();
        bgImg.flush();
        return bgImg;
    }

    public static java.util.List<ImmutablePair<BufferedImage, Integer>> drawGifBackground(BufferedImage qrImg, BgImgOptions bgImgOptions) {
        int qrWidth = qrImg.getWidth();
        int qrHeight = qrImg.getHeight();
        int bgW = Math.max(bgImgOptions.getBgW(), qrWidth);
        int bgH = Math.max(bgImgOptions.getBgH(), qrHeight);
        boolean fillMode = bgImgOptions.getBgImgStyle() == BgImgStyle.FILL;
        int bgOffsetX = fillMode ? bgImgOptions.getStartX() : bgW - qrWidth >> 1;
        int bgOffsetY = fillMode ? bgImgOptions.getStartY() : bgH - qrHeight >> 1;
        int gifImgLen = bgImgOptions.getGifDecoder().getFrameCount();
        java.util.List<ImmutablePair<BufferedImage, Integer>> result = new ArrayList(gifImgLen);
        int index = 0;

        for(int len = bgImgOptions.getGifDecoder().getFrameCount(); index < len; ++index) {
            BufferedImage bgImg = bgImgOptions.getGifDecoder().getFrame(index);
            BufferedImage temp = new BufferedImage(bgW, bgH, 1);
            temp.getGraphics().setColor(Color.WHITE);
            temp.getGraphics().fillRect(0, 0, bgW, bgH);
            temp.getGraphics().drawImage(bgImg.getScaledInstance(bgW, bgH, 4), 0, 0, (ImageObserver)null);
            Graphics2D bgGraphic = GraphicUtil.getG2d(temp);
            if (bgImgOptions.getBgImgStyle() == BgImgStyle.FILL) {
                bgGraphic.setComposite(AlphaComposite.getInstance(10, 1.0F));
                bgGraphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                bgGraphic.drawImage(qrImg.getScaledInstance(qrWidth, qrHeight, 4), bgOffsetX, bgOffsetY, (ImageObserver)null);
            } else {
                bgGraphic.setComposite(AlphaComposite.getInstance(10, bgImgOptions.getOpacity()));
                bgGraphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                bgGraphic.drawImage(qrImg.getScaledInstance(qrWidth, qrHeight, 4), bgOffsetX, bgOffsetY, (ImageObserver)null);
                bgGraphic.setComposite(AlphaComposite.getInstance(10, 1.0F));
            }

            bgGraphic.dispose();
            temp.flush();
            result.add(ImmutablePair.of(temp, bgImgOptions.getGifDecoder().getDelay(index)));
        }

        return result;
    }

    public static BufferedImage drawQrInfo(QrCodeOptions qrCodeConfig, BitMatrixEx bitMatrix) {
        int qrWidth = bitMatrix.getWidth();
        int qrHeight = bitMatrix.getHeight();
        int infoSize = bitMatrix.getMultiple();
        BufferedImage qrImg = new BufferedImage(qrWidth, qrHeight, 2);
        QrCodeOptions.DrawOptions drawOptions = qrCodeConfig.getDrawOptions();
        Color bgColor = drawOptions.getBgColor();
        Color preColor = drawOptions.getPreColor();
        Color detectOutColor = qrCodeConfig.getDetectOptions().getOutColor();
        Color detectInnerColor = qrCodeConfig.getDetectOptions().getInColor();
        if (detectInnerColor != null || detectOutColor != null) {
            if (detectInnerColor == null) {
                detectInnerColor = detectOutColor;
            } else if (detectOutColor == null) {
                detectOutColor = detectInnerColor;
            }
        }

        int leftPadding = bitMatrix.getLeftPadding();
        int topPadding = bitMatrix.getTopPadding();
        Graphics2D g2 = GraphicUtil.getG2d(qrImg);
        if (!drawOptions.isDiaphaneityFill()) {
            g2.setComposite(AlphaComposite.Src);
        }

        g2.setColor(bgColor);
        g2.fillRect(0, 0, qrWidth, qrHeight);
        if (drawOptions.getDrawStyle() == DrawStyle.TXT) {
            g2.setFont(QuickQrUtil.font(drawOptions.getFontName(), drawOptions.getFontStyle(), infoSize));
        }

        int detectCornerSize = bitMatrix.getByteMatrix().get(0, 5) == 1 ? 7 : 5;
        int matrixW = bitMatrix.getByteMatrix().getWidth();
        int matrixH = bitMatrix.getByteMatrix().getHeight();
        DrawStyle drawStyle = drawOptions.getDrawStyle();

        for(int x = 0; x < matrixW; ++x) {
            for(int y = 0; y < matrixH; ++y) {
                QrCodeRenderHelper.DetectLocation detectLocation = inDetectCornerArea(x, y, matrixW, matrixH, detectCornerSize);
                if (bitMatrix.getByteMatrix().get(x, y) == 0) {
                    if (!detectLocation.detectedArea() || !qrCodeConfig.getDetectOptions().getSpecial()) {
                        drawQrDotBgImg(qrCodeConfig, g2, leftPadding, topPadding, infoSize, x, y);
                    }
                } else if (detectLocation.detectedArea() && qrCodeConfig.getDetectOptions().getSpecial()) {
                    drawDetectImg(qrCodeConfig, g2, bitMatrix, matrixW, matrixH, leftPadding, topPadding, infoSize, detectCornerSize, x, y, detectOutColor, detectInnerColor, detectLocation);
                } else {
                    g2.setColor(preColor);
                    drawQrDotImg(qrCodeConfig, drawStyle, g2, bitMatrix, leftPadding, topPadding, infoSize, x, y);
                }
            }
        }

        g2.dispose();
        qrImg = scaleQr2RealSize(qrCodeConfig, bitMatrix, qrImg);
        if (drawOptions.getQrStyle() == ImgStyle.CIRCLE) {
            return ImageOperateUtil.makeRoundImg(qrImg, false, (Color)null);
        } else if (drawOptions.getQrStyle() == ImgStyle.ROUND) {
            float radius = (float)Math.min(qrCodeConfig.getW(), qrCodeConfig.getH()) * drawOptions.getCornerRadius();
            return ImageOperateUtil.makeRoundedCorner(qrImg, (int)radius);
        } else {
            return qrImg;
        }
    }

    private static BufferedImage scaleQr2RealSize(QrCodeOptions qrCodeConfig, BitMatrixEx bitMatrix, BufferedImage qrCode) {
        int qrCodeWidth = bitMatrix.getWidth();
        int qrCodeHeight = bitMatrix.getHeight();
        int realQrCodeWidth = qrCodeConfig.getW();
        int realQrCodeHeight = qrCodeConfig.getH();
        if (qrCodeWidth != realQrCodeWidth || qrCodeHeight != realQrCodeHeight) {
            qrCode = GraphicUtil.createImg(realQrCodeWidth, realQrCodeHeight, 0, 0, qrCode);
        }

        return qrCode;
    }

    private static QrCodeRenderHelper.DetectLocation inDetectCornerArea(int x, int y, int matrixW, int matrixH, int detectCornerSize) {
        if (x < detectCornerSize && y < detectCornerSize) {
            return QrCodeRenderHelper.DetectLocation.LT;
        } else if (x < detectCornerSize && y >= matrixH - detectCornerSize) {
            return QrCodeRenderHelper.DetectLocation.LD;
        } else {
            return x >= matrixW - detectCornerSize && y < detectCornerSize ? QrCodeRenderHelper.DetectLocation.RT : QrCodeRenderHelper.DetectLocation.NONE;
        }
    }

    private static boolean inOuterDetectCornerArea(int x, int y, int matrixW, int matrixH, int detectCornerSize) {
        return x == 0 || x == detectCornerSize - 1 || x == matrixW - 1 || x == matrixW - detectCornerSize || y == 0 || y == detectCornerSize - 1 || y == matrixH - 1 || y == matrixH - detectCornerSize;
    }

    private static void drawDetectImg(QrCodeOptions qrCodeConfig, Graphics2D g2, BitMatrixEx bitMatrix, int matrixW, int matrixH, int leftPadding, int topPadding, int infoSize, int detectCornerSize, int x, int y, Color detectOutColor, Color detectInnerColor, QrCodeRenderHelper.DetectLocation detectLocation) {
        BufferedImage detectedImg = qrCodeConfig.getDetectOptions().chooseDetectedImg(detectLocation);
        if (detectedImg == null) {
            if (inOuterDetectCornerArea(x, y, matrixW, matrixH, detectCornerSize)) {
                g2.setColor(detectOutColor);
            } else {
                g2.setColor(detectInnerColor);
            }

            g2.fillRect(leftPadding + x * infoSize, topPadding + y * infoSize, infoSize, infoSize);
        } else {
            g2.drawImage(detectedImg.getScaledInstance(infoSize * detectCornerSize, infoSize * detectCornerSize, 4), leftPadding + x * infoSize, topPadding + y * infoSize, (ImageObserver)null);

            for(int addX = 0; addX < detectCornerSize; ++addX) {
                for(int addY = 0; addY < detectCornerSize; ++addY) {
                    bitMatrix.getByteMatrix().set(x + addX, y + addY, 0);
                }
            }

        }
    }

    private static void drawQrDotBgImg(QrCodeOptions qrCodeConfig, Graphics2D g2, int leftPadding, int topPadding, int infoSize, int x, int y) {
        if (qrCodeConfig.getDrawOptions().getBgImg() != null) {
            DrawStyle.IMAGE.draw(g2, leftPadding + x * infoSize, topPadding + y * infoSize, infoSize, infoSize, qrCodeConfig.getDrawOptions().getBgImg(), (String)null);
        }
    }

    private static void drawQrDotImg(QrCodeOptions qrCodeConfig, DrawStyle drawStyle, Graphics2D g2, BitMatrixEx bitMatrix, int leftPadding, int topPadding, int infoSize, int x, int y) {
        if (drawStyle != DrawStyle.IMAGE) {
            drawGeometricFigure(qrCodeConfig, drawStyle, g2, bitMatrix, leftPadding, topPadding, infoSize, x, y);
        } else {
            drawSpecialImg(qrCodeConfig, drawStyle, g2, bitMatrix, leftPadding, topPadding, infoSize, x, y);
        }

    }

    private static void drawGeometricFigure(QrCodeOptions qrCodeConfig, DrawStyle drawStyle, Graphics2D g2, BitMatrixEx bitMatrix, int leftPadding, int topPadding, int infoSize, int x, int y) {
        if (!qrCodeConfig.getDrawOptions().isEnableScale()) {
            drawStyle.draw(g2, leftPadding + x * infoSize, topPadding + y * infoSize, infoSize, infoSize, qrCodeConfig.getDrawOptions().getImage(1, 1), qrCodeConfig.getDrawOptions().getDrawQrTxt());
        } else {
            int maxRow = getMaxRow(bitMatrix.getByteMatrix(), x, y);
            int maxCol = getMaxCol(bitMatrix.getByteMatrix(), x, y);
            java.util.List<DotSize> availableSize = getAvailableSize(bitMatrix.getByteMatrix(), x, y, maxRow, maxCol);
            Iterator var12 = availableSize.iterator();

            DotSize dotSize;
            do {
                if (!var12.hasNext()) {
                    drawStyle.draw(g2, leftPadding + x * infoSize, topPadding + y * infoSize, infoSize, infoSize, qrCodeConfig.getDrawOptions().getImage(1, 1), qrCodeConfig.getDrawOptions().getDrawQrTxt());
                    return;
                }

                dotSize = (DotSize)var12.next();
            } while(!drawStyle.expand(dotSize));

            drawStyle.draw(g2, leftPadding + x * infoSize, topPadding + y * infoSize, dotSize.getCol() * infoSize, dotSize.getRow() * infoSize, qrCodeConfig.getDrawOptions().getImage(dotSize), qrCodeConfig.getDrawOptions().getDrawQrTxt());

            for(int col = 0; col < dotSize.getCol(); ++col) {
                for(int row = 0; row < dotSize.getRow(); ++row) {
                    bitMatrix.getByteMatrix().set(x + col, y + row, 0);
                }
            }

        }
    }

    private static void drawSpecialImg(QrCodeOptions qrCodeConfig, DrawStyle drawStyle, Graphics2D g2, BitMatrixEx bitMatrix, int leftPadding, int topPadding, int infoSize, int x, int y) {
        int maxRow = getMaxRow(bitMatrix.getByteMatrix(), x, y);
        int maxCol = getMaxCol(bitMatrix.getByteMatrix(), x, y);
        java.util.List<DotSize> availableSize = getAvailableSize(bitMatrix.getByteMatrix(), x, y, maxRow, maxCol);
        Iterator var13 = availableSize.iterator();

        BufferedImage drawImg;
        DotSize dotSize;
        do {
            if (!var13.hasNext()) {
                drawStyle.draw(g2, leftPadding + x * infoSize, topPadding + y * infoSize, infoSize, infoSize, qrCodeConfig.getDrawOptions().getImage(DotSize.SIZE_1_1), qrCodeConfig.getDrawOptions().getDrawQrTxt());
                return;
            }

            dotSize = (DotSize)var13.next();
            drawImg = qrCodeConfig.getDrawOptions().getImage(dotSize);
        } while(drawImg == null);

        drawStyle.draw(g2, leftPadding + x * infoSize, topPadding + y * infoSize, dotSize.getCol() * infoSize, dotSize.getRow() * infoSize, drawImg, qrCodeConfig.getDrawOptions().getDrawQrTxt());

        for(int col = 0; col < dotSize.getCol(); ++col) {
            for(int row = 0; row < dotSize.getRow(); ++row) {
                bitMatrix.getByteMatrix().set(x + col, y + row, 0);
            }
        }

    }

    private static int getMaxRow(ByteMatrix bitMatrix, int x, int y) {
        int cnt = 1;

        while(true) {
            ++y;
            if (y >= bitMatrix.getHeight() || bitMatrix.get(x, y) == 0) {
                return cnt;
            }

            ++cnt;
        }
    }

    private static int getMaxCol(ByteMatrix bitMatrix, int x, int y) {
        int cnt = 1;

        while(true) {
            ++x;
            if (x >= bitMatrix.getWidth() || bitMatrix.get(x, y) == 0) {
                return cnt;
            }

            ++cnt;
        }
    }

    private static java.util.List<DotSize> getAvailableSize(ByteMatrix bitMatrix, int x, int y, int maxRow, int maxCol) {
        if (maxRow == 1) {
            return Collections.singletonList(DotSize.create(1, maxCol));
        } else if (maxCol == 1) {
            return Collections.singletonList(DotSize.create(maxRow, 1));
        } else {
            List<DotSize> container = new ArrayList();
            int col = 1;
            int lastRow = maxRow;

            while(col < maxCol) {
                int offset = 0;
                int row = 1;

                while(true) {
                    ++offset;
                    if (offset >= lastRow || bitMatrix.get(x + col, y + offset) == 0) {
                        ++col;
                        lastRow = row;
                        container.add(new DotSize(row, col));
                        break;
                    }

                    ++row;
                }
            }

            container.sort((o1, o2) -> {
                return o2.size() - o1.size();
            });
            return container;
        }
    }

    public static enum DetectLocation {
        LT,
        LD,
        RT,
        NONE {
            public boolean detectedArea() {
                return false;
            }
        };

        private DetectLocation() {
        }

        public boolean detectedArea() {
            return true;
        }
    }
}
