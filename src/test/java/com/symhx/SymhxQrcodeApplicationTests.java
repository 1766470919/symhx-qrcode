package com.symhx;

import com.symhx.wrapper.QrCodeGenWrapper;
import com.symhx.wrapper.QrCodeOptions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SymhxQrcodeApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void normalQrCode() {
        try {
            // 生成二维码，并输出为qr.png图片
            String msg = "LIUJUN";
            boolean ans = QrCodeGenWrapper.of(msg)
                    .setW(500)
                    .setPadding(3)
                    .setQrStyle(QrCodeOptions.ImgStyle.ROUND)
                    // 圆角弧度默认是宽高的 1/8, 可以根据需要自行设置
                    .setQrCornerRadiusRate(0.25F)
                    .setPicType("png")
                    .asFile("C:\\Users\\hy\\Desktop\\code.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
