package com.james.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(XueChengPlusException.class)
    public RestErrorResponse customException(XueChengPlusException e){
        log.error("系统异常,{}",e.getMessage());
        String message = e.getMessage();
        RestErrorResponse restErrorResponse = new RestErrorResponse(message);
        return restErrorResponse;
    }

    @ExceptionHandler(Exception.class)
    public RestErrorResponse exception(Exception e){
        log.error("系统异常,{}",e.getMessage());
        RestErrorResponse restErrorResponse = new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
        return restErrorResponse;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e){

        BindingResult bindingResult = e.getBindingResult();
        //存储错误信息
        ArrayList<String> errors = new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(
                item->{
                    errors.add(item.getDefaultMessage());
                }
        );
        //将list中的错误信息拼接起来
        String errMessage = StringUtils.join(errors, ",");
        log.error("系统异常,{}, {}",e.getMessage(),errMessage);
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }

}
