package com.symhx.service.impl;

import com.google.zxing.WriterException;
import com.symhx.DiyException;
import com.symhx.request.QrCodeVO;
import com.symhx.service.IQrCodeService;
import com.symhx.wrapper.QrCodeGenWrapper;
import com.symhx.wrapper.QrCodeOptions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;

/**
 * @author lj
 * @date 2020/12/11
 */
@Service("qrCodeService")
public class QrCodeServiceImpl implements IQrCodeService {

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";

    private QrCodeGenWrapper.Builder OrdinaryQRCode() {
        QrCodeGenWrapper.Builder of = QrCodeGenWrapper.of("")
                .setW(200)
                .setPadding(0)
                .setQrStyle(QrCodeOptions.ImgStyle.NORMAL)
                .setPicType("png")
                .setDrawBgColor(Color.WHITE)
                .setDrawPreColor(Color.BLACK)
                .setDetectOutColor(Color.BLACK)
                .setDetectInColor(Color.BLACK);
        return of;
    }

    /**
     * 文本二维码
     * @param content
     */
    public QrCodeGenWrapper.Builder getTextQRCode(String content) throws Exception{
        if (StringUtils.isBlank(content)) {
            throw new DiyException("正文内容不能为空");
        }
        return OrdinaryQRCode().setMsg(content);
    }

    /**
     * 网址二维码
     * @param content
     */
    public QrCodeGenWrapper.Builder getURLQRCode(String content) throws Exception{
        if (StringUtils.isBlank(content)) {
            throw new DiyException("网址不能为空");
        }
        if (!content.startsWith(HTTP_PREFIX) || !content.startsWith(HTTPS_PREFIX)) {
            throw new DiyException("请输入正确的网址");
        }
        return OrdinaryQRCode().setMsg(content);
    }


    /**
     * 获取常规二维码
     *
     * @param response
     */
    @Override
    public void getCommonQRCode(QrCodeVO codeVO, HttpServletResponse response) throws Exception{
        QrCodeGenWrapper.Builder builder = null;
        if (codeVO.getCodeType().equals(QrCodeVO.CodeType.textType)) {
            builder = getTextQRCode(codeVO.getContent());
        } else if (codeVO.getCodeType().equals(QrCodeVO.CodeType.URLType)) {
            builder = getURLQRCode(codeVO.getContent());
        }

        ServletOutputStream outputStream = response.getOutputStream();
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        BufferedImage image = builder.asBufferedImage();
        ImageIO.write(image, "png", outputStream);
    }

    /**
     * 个性二维码
     *
     * @param codeVO
     * @param response
     * @throws Exception
     */
    @Override
    public void getPersonalizedQRCode(QrCodeVO codeVO, HttpServletResponse response) throws Exception {
        QrCodeGenWrapper.Builder builder = null;
        if (codeVO.getCodeType().equals(QrCodeVO.CodeType.textType)) {
            builder = getTextQRCode(codeVO.getContent());
        } else if (codeVO.getCodeType().equals(QrCodeVO.CodeType.URLType)) {
            builder = getURLQRCode(codeVO.getContent());
        } else {
            builder = getTextQRCode(codeVO.getContent());
        }

        ServletOutputStream outputStream = response.getOutputStream();
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        Integer scale = null;
        if (null != codeVO.getWidth() && null != codeVO.getHeight()) {
            scale = Math.min(codeVO.getWidth(), codeVO.getHeight());
        } else {
            scale = 200;
        }
        Integer padding = 0;
        if (null != codeVO.getPadding()) {
            padding = codeVO.getPadding();
        }

        builder.setW(scale)
                .setPadding(padding)
                .setQrStyle(QrCodeOptions.ImgStyle.valueOf(codeVO.getRoundType()))
                .setQrCornerRadiusRate(codeVO.getRadiusRate())
                .setPicType(StringUtils.isNotBlank(codeVO.getSuffix()) ? codeVO.getSuffix() : "png")
                .setDrawBgColor(Color.decode(codeVO.getBgColor()))
                .setDrawPreColor(Color.decode(codeVO.getPreColor()))
                .setDetectOutColor(Color.decode(codeVO.getPreColor()))
                .setDetectInColor(Color.decode(codeVO.getPreColor()));
        if (!codeVO.getCodeEyeColorSync()) {
            // 自定义码眼颜色
            builder.setDetectInColor(Color.decode(codeVO.getDetectInColor()))
                    .setDetectOutColor(Color.decode(codeVO.getDetectOutColor()))
                    .setDetectSpecial();
        }
        // 透明色背景
            if (null != codeVO.getBgOpacity() && codeVO.getBgOpacity().equals(0F)) {
            builder.setQrStyle(QrCodeOptions.ImgStyle.NORMAL)
                    .setDrawBgColor(new Color(255, 255, 255, 0));
        }
        // 设置Logo
        if (null != codeVO.getLogoInfo() && StringUtils.isNotBlank(codeVO.getLogoInfo().getLogoPath())) {
            setLogoInfo(builder, codeVO);
        }
        // 设置背景
        if (null != codeVO.getBackgroundInfo() && StringUtils.isNotBlank(codeVO.getBackgroundInfo().getBgPath())) {
            setBackgroundInfo(builder, codeVO);
        }


        BufferedImage image = builder.asBufferedImage();
        ImageIO.write(image, "png", outputStream);
    }

    /**
     * 二维码Logo设置
     * @param builder
     * @param qrCodeVO
     * @throws Exception
     */
    private void setLogoInfo(QrCodeGenWrapper.Builder builder, QrCodeVO qrCodeVO) throws Exception{
        builder.setLogo(qrCodeVO.getLogoInfo().getLogoPath())
                .setLogoBgColor(0xfffefefe)
                .setLogoStyle(QrCodeOptions.LogoStyle.NORMAL);
        if (null != qrCodeVO.getLogoInfo().getLogoBorder() && qrCodeVO.getLogoInfo().getLogoBorder()) {
            builder.setLogoBorder(true)
                    .setLogoBorderBgColor(0xffc7c7c7);
        }
        if (StringUtils.isNotBlank(qrCodeVO.getLogoInfo().getLogoStyle())) {
            builder.setLogoStyle(QrCodeOptions.LogoStyle.valueOf(qrCodeVO.getLogoInfo().getLogoStyle()));
        }
        if (null != qrCodeVO.getLogoInfo().getRate()) {
            builder.setLogoRate(qrCodeVO.getLogoInfo().getRate());
        }
    }

    /**
     * 二维码背景设置
     * @param builder
     * @param qrCodeVO
     * @throws Exception
     */
    private void setBackgroundInfo(QrCodeGenWrapper.Builder builder, QrCodeVO qrCodeVO) throws Exception {
        builder.setBgImg(qrCodeVO.getBackgroundInfo().getBgPath())
                .setBgOpacity(qrCodeVO.getBackgroundInfo().getOpacity())
                .setBgImgStyle(qrCodeVO.getBackgroundInfo().getImgStyle())
                .setBgW(null != qrCodeVO.getBackgroundInfo().getBgW() ? qrCodeVO.getBackgroundInfo().getBgW() : 200)
                .setBgH(null != qrCodeVO.getBackgroundInfo().getBgH() ? qrCodeVO.getBackgroundInfo().getBgH() : 200)
                .setBgStyle(qrCodeVO.getBackgroundInfo().getBgImgStyle());
    }
}
