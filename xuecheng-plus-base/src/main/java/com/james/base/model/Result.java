package com.james.base.model;

import lombok.Data;

@Data
public class Result {
    private String code;
    private String errMessage;

    public static Result error(String errMessage){
        Result result = new Result();
        result.code = "120409";
        result.errMessage = errMessage;
        return result;
    }
    public static Result success(){
        Result result = new Result();
        result.code = "200";
        return result;
    }
}
