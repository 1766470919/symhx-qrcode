package com.symhx.handler;

import com.symhx.DiyException;
import com.symhx.response.ResponseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

/**
 * @author lj
 * @date 2020/12/11
 */
@ControllerAdvice("com.symhx")
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(DiyException.class)
    public ResponseInfo diyHandler(Exception e) {
        log.info("==处理全局异常== 当前时间 {}, 异常信息 {}", LocalDateTime.now(), e.getMessage());
        return ResponseInfo.of(e.getMessage()).setCode(20001);
    }
}
