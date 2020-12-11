package com.symhx.helper;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import com.symhx.wrapper.BitMatrixEx;
import com.symhx.wrapper.QrCodeOptions;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symhx.wrapper.QrCodeOptions.BgImgStyle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lj
 * @date 2020/12/11
 */
public class QrCodeGenerateHelper {
    private static Logger log = LoggerFactory.getLogger(QrCodeGenerateHelper.class);
    private static final int QUIET_ZONE_SIZE = 4;

    public QrCodeGenerateHelper() {
    }

    public static BitMatrixEx encode(QrCodeOptions qrCodeConfig) throws WriterException {
        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
        int quietZone = 1;
        if (qrCodeConfig.getHints() != null) {
            if (qrCodeConfig.getHints().containsKey(EncodeHintType.ERROR_CORRECTION)) {
                errorCorrectionLevel = ErrorCorrectionLevel.valueOf(qrCodeConfig.getHints().get(EncodeHintType.ERROR_CORRECTION).toString());
            }

            if (qrCodeConfig.getHints().containsKey(EncodeHintType.MARGIN)) {
                quietZone = Integer.parseInt(qrCodeConfig.getHints().get(EncodeHintType.MARGIN).toString());
            }

            if (quietZone > 4) {
                quietZone = 4;
            } else if (quietZone < 0) {
                quietZone = 0;
            }
        }

        QRCode code = Encoder.encode(qrCodeConfig.getMsg(), errorCorrectionLevel, qrCodeConfig.getHints());
        return renderResult(code, qrCodeConfig.getW(), qrCodeConfig.getH(), quietZone);
    }

    private static BitMatrixEx renderResult(QRCode code, int width, int height, int quietZone) {
        ByteMatrix input = code.getMatrix();
        if (input == null) {
            throw new IllegalStateException();
        } else {
            int inputWidth = input.getWidth();
            int inputHeight = input.getHeight();
            int qrWidth = inputWidth + quietZone * 2;
            int qrHeight = inputHeight + quietZone * 2;
            int minSize = Math.min(width, height);
            int scale = calculateScale(qrWidth, minSize);
            int padding;
            int tmpValue;
            if (scale > 0) {
                if (log.isDebugEnabled()) {
                    log.debug("qrCode scale enable! scale: {}, qrSize:{}, expectSize:{}x{}", new Object[]{scale, qrWidth, width, height});
                }

                padding = (minSize - qrWidth * scale) / 4 * quietZone;
                tmpValue = qrWidth * scale + padding;
                if (width == height) {
                    width = tmpValue;
                    height = tmpValue;
                } else if (width > height) {
                    width = width * tmpValue / height;
                    height = tmpValue;
                } else {
                    height = height * tmpValue / width;
                    width = tmpValue;
                }
            }

            padding = Math.max(width, qrWidth);
            tmpValue = Math.max(height, qrHeight);
            int multiple = Math.min(padding / qrWidth, tmpValue / qrHeight);
            int leftPadding = (padding - inputWidth * multiple) / 2;
            int topPadding = (tmpValue - inputHeight * multiple) / 2;
            BitMatrixEx res = new BitMatrixEx();
            res.setByteMatrix(input);
            res.setLeftPadding(leftPadding);
            res.setTopPadding(topPadding);
            res.setMultiple(multiple);
            res.setWidth(padding);
            res.setHeight(tmpValue);
            return res;
        }
    }

    private static int calculateScale(int qrCodeSize, int expectSize) {
        if (qrCodeSize >= expectSize) {
            return 0;
        } else {
            int scale = expectSize / qrCodeSize;
            int abs = expectSize - scale * qrCodeSize;
            return (double)abs < (double)expectSize * 0.15D ? 0 : scale;
        }
    }

    public static BufferedImage toBufferedImage(QrCodeOptions qrCodeConfig, BitMatrixEx bitMatrix) throws IOException {
        BufferedImage qrCode = QrCodeRenderHelper.drawQrInfo(qrCodeConfig, bitMatrix);
        boolean logoAlreadyDraw = false;
        if (qrCodeConfig.getBgImgOptions() != null) {
            if (qrCodeConfig.getBgImgOptions().getBgImgStyle() == QrCodeOptions.BgImgStyle.FILL && qrCodeConfig.getLogoOptions() != null) {
                qrCode = QrCodeRenderHelper.drawLogo(qrCode, qrCodeConfig.getLogoOptions());
                logoAlreadyDraw = true;
            }

            qrCode = QrCodeRenderHelper.drawBackground(qrCode, qrCodeConfig.getBgImgOptions());
        }

        if (qrCodeConfig.getLogoOptions() != null && !logoAlreadyDraw) {
            qrCode = QrCodeRenderHelper.drawLogo(qrCode, qrCodeConfig.getLogoOptions());
        }

        return qrCode;
    }

    public static List<ImmutablePair<BufferedImage, Integer>> toGifImages(QrCodeOptions qrCodeConfig, BitMatrixEx bitMatrix) {
        if (qrCodeConfig.getBgImgOptions() != null && qrCodeConfig.getBgImgOptions().getGifDecoder().getFrameCount() > 0) {
            BufferedImage qrCode = QrCodeRenderHelper.drawQrInfo(qrCodeConfig, bitMatrix);
            boolean logoAlreadyDraw = false;
            if (qrCodeConfig.getBgImgOptions().getBgImgStyle() == BgImgStyle.FILL && qrCodeConfig.getLogoOptions() != null) {
                qrCode = QrCodeRenderHelper.drawLogo(qrCode, qrCodeConfig.getLogoOptions());
                logoAlreadyDraw = true;
            }

            List<ImmutablePair<BufferedImage, Integer>> bgList = QrCodeRenderHelper.drawGifBackground(qrCode, qrCodeConfig.getBgImgOptions());
            if (qrCodeConfig.getLogoOptions() != null && !logoAlreadyDraw) {
                List<ImmutablePair<BufferedImage, Integer>> result = new ArrayList(bgList.size());
                Iterator var6 = bgList.iterator();

                while(var6.hasNext()) {
                    ImmutablePair<BufferedImage, Integer> pair = (ImmutablePair)var6.next();
                    result.add(ImmutablePair.of(QrCodeRenderHelper.drawLogo((BufferedImage)pair.getLeft(), qrCodeConfig.getLogoOptions()), pair.getRight()));
                }

                return result;
            } else {
                return bgList;
            }
        } else {
            throw new IllegalArgumentException("animated background image should not be null!");
        }
    }
}
