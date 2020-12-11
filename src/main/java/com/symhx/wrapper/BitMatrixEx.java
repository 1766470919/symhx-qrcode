package com.symhx.wrapper;

import com.google.zxing.qrcode.encoder.ByteMatrix;

/**
 * @author lj
 * @date 2020/12/11
 */
public class BitMatrixEx {
    private int width;
    private int height;
    private int leftPadding;
    private int topPadding;
    private int multiple;
    private ByteMatrix byteMatrix;

    public BitMatrixEx() {
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLeftPadding() {
        return this.leftPadding;
    }

    public void setLeftPadding(int leftPadding) {
        this.leftPadding = leftPadding;
    }

    public int getTopPadding() {
        return this.topPadding;
    }

    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    public int getMultiple() {
        return this.multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }

    public ByteMatrix getByteMatrix() {
        return this.byteMatrix;
    }

    public void setByteMatrix(ByteMatrix byteMatrix) {
        this.byteMatrix = byteMatrix;
    }
}
