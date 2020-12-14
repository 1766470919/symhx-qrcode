package com.symhx.request;

import com.symhx.gif.GifDecoder;
import com.symhx.wrapper.QrCodeOptions;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lj
 * @date 2020/12/11
 */
@Data
public class QrCodeVO implements Serializable {

    /**
     * 二维码类型
     */
    private CodeType codeType;

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
     * 码眼检测外框颜色
     */
    private String detectOutColor = "#000000";

    /**
     * 码眼检测内颜色
     */
    private String detectInColor = "#000000";

    /**
     * 背景色透明度
     */
    private Float bgOpacity = 1F;

    /**
     * logo信息
     */
    private LogoInfo logoInfo;

    /**
     * 背景信息
     */
    private BackgroundInfo backgroundInfo;

    /**
     * 二维码内容类型
     */
    public enum CodeType {
        /**
         * 文本类型
         */
        textType(1),
        /**
         * 网址类型
         */
        URLType(2),
        /**
         * 文件类型
         */
        fileType(3),
        /**
         * 图片类型
         */
        imageType(4),
        /**
         * 音视频
         */
        audioAndVideoType(5),
        /**
         * 名片
         */
        businessCardType(6),
        /**
         * 微信
         */
        weChatType(7),
        /**
         * 公众号
         */
        publicType(8),
        /**
         * 表单
         */
        formType(9);
        private Integer type;

        CodeType(Integer type) {
            this.type = type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getType() {
            return type;
        }
    }

    @Data
    public static class LogoInfo {
        /**
         * logo地址
         */
        private String logoPath;

        /**
         * logo样式
         */
        private String logoStyle;

        /**
         * logo边框
         */
        private Boolean logoBorder;

        /**
         * 边框颜色
         */
        private String logoBorderColor;

        /**
         * logo背景色
         */
        private String logoBgColor;

        /**
         * logo比率
         */
        private Integer rate;

        private Float opacity;
    }

    @Data
    public static class BackgroundInfo {
        /**
         * 背景地址
         */
        private String bgPath;

        /**
         * 背景样式
         */
        private QrCodeOptions.ImgStyle imgStyle;

        /**
         * 圆角大小
         */
        private Float radius = 0F;

        /**
         * gif解析器
         */
        private GifDecoder gifDecoder;

        /**
         * 背景宽
         */
        private Integer bgW;

        /**
         * 背景高
         */
        private Integer bgH;

        /**
         * 背景填充方式
         */
        private QrCodeOptions.BgImgStyle bgImgStyle;

        /**
         * 背景透明度
         */
        private Float opacity = 0.5F;

        /**
         * 填充开始横坐标
         */
        private Integer startX;

        /**
         * 填充开始纵坐标
         */
        private Integer startY;
    }

}
