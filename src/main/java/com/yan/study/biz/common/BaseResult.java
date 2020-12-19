package com.yan.study.biz.common;

public class BaseResult<R> {

    private Integer code;
    private String message;
    private R result;

    public BaseResult(Integer code, String message, R result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public static <T> BaseResult<T>  success(T result) {
        return new BaseResult<T>(1, "success", result);
    }

    public static <T> BaseResult<T> fail(String message) {
        return new BaseResult<T>(0, message, null);
    }

}
