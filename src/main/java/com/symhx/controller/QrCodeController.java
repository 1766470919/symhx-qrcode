package com.symhx.controller;

import com.symhx.request.QrCodeVO;
import com.symhx.response.ResponseInfo;
import com.symhx.service.IQrCodeService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lj
 * @date 2020/12/11
 */
@RestController
@RequestMapping(value = "/sy/qrcode")
public class QrCodeController {

    @Resource
    private IQrCodeService qrCodeService;

    /**
     * 常见二维码
     * @param qrCodeVO
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/commonCode", method = RequestMethod.POST)
    public void commonQRCode(@RequestBody QrCodeVO qrCodeVO, HttpServletResponse response) {
        qrCodeService.getCommonQRCode(qrCodeVO, response);
    }

    /**
     * 个性化二维码
     * @param qrCodeVO
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/personalizedQRCode", method = RequestMethod.POST)
    public void personalizedQRCode(@RequestBody QrCodeVO qrCodeVO, HttpServletResponse response) {
        qrCodeService.getPersonalizedQRCode(qrCodeVO, response);
    }
}
