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

    @RequestMapping(value = "/normal", method = RequestMethod.POST)
    public void getNormalCode(@RequestBody QrCodeVO qrCodeVO, HttpServletResponse response) throws Exception{
        qrCodeService.getNormalCode(qrCodeVO, response);
    }
}
