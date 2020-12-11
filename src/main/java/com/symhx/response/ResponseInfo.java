package com.symhx.response;

import com.symhx.wrapper.QrCodeGenWrapper;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lj
 * @date 2020/12/11
 */
@Data
public class ResponseInfo implements Serializable {

    private String message;

    private Integer code;

    public String getMessage() {
        return message;
    }

    public ResponseInfo setMessage(String message) {
        this.message = message;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public ResponseInfo setCode(Integer code) {
        this.code = code;
        return this;
    }

    public static ResponseInfo of(String message) {
        return new ResponseInfo().setMessage(message);
    }

}
