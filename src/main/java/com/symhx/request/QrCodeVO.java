package com.symhx.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lj
 * @date 2020/12/11
 */
@Data
public class QrCodeVO implements Serializable {

    /**
     * 正文内容
     */
    private String content;

    /**
     * 宽
     */
    private Integer width;

    /**
     * 高
     */
    private Integer height;

    /**
     * 内边距
     */
    private Integer padding;

    /**
     * 类型后缀 支持[png,jpg,jpeg]格式
     */
    private String suffix = "png";

    /**
     * ROUND,
     * NORMAL,
     * CIRCLE;
     */
    private String roundType = "NORMAL";

    /**
     * 半径率 [0-1]之间
     * 当roundType: 圆角类型为ROUND时生效。
     */
    private Float radiusRate = 0.125F;

    /**
     * 背景色
     */
    private String bgColor = "#FFFFFF";

    /**
     * 前景色
     */
    private String preColor = "#000000";

    /**
     * 同步到码眼
     */
    private Boolean codeEyeColorSync = true;

    /**
     * 网址
     */
    private String urlAddress;

}
