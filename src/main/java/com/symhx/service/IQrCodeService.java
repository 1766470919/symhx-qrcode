package com.symhx.service;

import com.symhx.request.QrCodeVO;

import javax.servlet.http.HttpServletResponse;

/**
 * @author lj
 * @date 2020/12/11
 */
public interface IQrCodeService {
    /**
     * 获取普通二维码
     *
     * @param codeVO
     * @param response
     */
    void getCommonQRCode(QrCodeVO codeVO, HttpServletResponse response) throws Exception;

    /**
     * 个性二维码
     *
     * @param codeVO
     * @param response
     * @throws Exception
     */
    void getPersonalizedQRCode(QrCodeVO codeVO, HttpServletResponse response) throws Exception;
}
