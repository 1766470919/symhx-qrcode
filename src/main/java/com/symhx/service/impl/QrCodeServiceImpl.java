package com.symhx.service.impl;

import com.google.zxing.WriterException;
import com.symhx.DiyException;
import com.symhx.request.QrCodeVO;
import com.symhx.service.IQrCodeService;
import com.symhx.wrapper.QrCodeGenWrapper;
import com.symhx.wrapper.QrCodeOptions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author lj
 * @date 2020/12/11
 */
@Service("qrCodeService")
public class QrCodeServiceImpl implements IQrCodeService {

    /**
     * 获取普通二维码
     *
     * @param response
     */
    @Override
    public void getNormalCode(QrCodeVO codeVO, HttpServletResponse response) throws Exception{
        if (StringUtils.isBlank(codeVO.getContent())) {
            throw new DiyException("正文不能为空");
        }
        Integer unified = null;
        if (null != codeVO.getWidth() && null != codeVO.getHeight()) {
            unified = Math.min(codeVO.getWidth(), codeVO.getHeight());
        } else {
            unified = 200;
        }
        Integer padding = 5;
        if (null != codeVO.getPadding()) {
            padding = codeVO.getPadding();
        }

        ServletOutputStream outputStream = response.getOutputStream();
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        QrCodeGenWrapper.Builder of = QrCodeGenWrapper.of(codeVO.getContent())
                .setW(unified)
                .setPadding(padding)
                .setQrStyle(QrCodeOptions.ImgStyle.valueOf(codeVO.getRoundType()))
                // 圆角弧度默认是宽高的 1/8, 可以根据需要自行设置
                .setQrCornerRadiusRate(codeVO.getRadiusRate())
                .setPicType(codeVO.getSuffix())
                .setDrawBgColor(Color.decode(codeVO.getBgColor()))
                .setDrawPreColor(Color.decode(codeVO.getPreColor()))
                .setDetectOutColor(Color.decode(codeVO.getDetectOutColor()))
                .setDetectInColor(Color.decode(codeVO.getDetectInColor()));
        if (codeVO.getCodeEyeColorSync()) {
            of.setDetectInColor(Color.decode(codeVO.getBgColor()))
                    .setDetectOutColor(Color.decode(codeVO.getBgColor()));
        }
        if (null != codeVO.getBgOperacity()) {
            of.setBgOpacity(codeVO.getBgOperacity());
        }
        BufferedImage image = of.asBufferedImage();
        ImageIO.write(image, "png", outputStream);

    }

    public void getDiyCode(QrCodeVO codeVO, HttpServletResponse response) throws Exception {

    }
}
