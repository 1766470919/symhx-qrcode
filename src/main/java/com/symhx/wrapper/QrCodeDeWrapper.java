package com.symhx.wrapper;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.symhx.util.ImageLoadUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author lj
 * @date 2020/12/11
 */
public class QrCodeDeWrapper {
    public QrCodeDeWrapper() {
    }

    public static String decode(String qrcodeImg) throws IOException, FormatException, ChecksumException, NotFoundException {
        BufferedImage image = ImageLoadUtil.getImageByPath(qrcodeImg);
        return decode(image);
    }

    public static String decode(BufferedImage image) throws FormatException, ChecksumException, NotFoundException {
        if (image == null) {
            throw new IllegalStateException("can not load qrCode!");
        } else {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader qrCodeReader = new QRCodeReader();
            Result result = qrCodeReader.decode(bitmap);
            return result.getText();
        }
    }
}
