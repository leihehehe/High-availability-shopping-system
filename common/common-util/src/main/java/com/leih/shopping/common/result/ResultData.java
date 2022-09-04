package com.leih.shopping.common.result;

import lombok.Data;


@Data
public class ResultData <T>{
    private int status;
    private String message;
    private T data;
    private long timestamp;

    public ResultData(){
        this.timestamp=System.currentTimeMillis();
    }

    public static <T> ResultData<T> success(T data){
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(ResultCodeEnum.SUCCESS.getCode());
        resultData.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        resultData.setData(data);
        return resultData;
    }

    public static <T> ResultData<T> fail(ResultCodeEnum code){
        ResultData<T> resultData= new ResultData<>();
        resultData.setStatus(code.getCode());
        resultData.setMessage(code.getMessage());
        return resultData;
    }
    public static <T> ResultData<T> build(T data, ResultCodeEnum code){
        ResultData<T> resultData= new ResultData<>();
        resultData.setStatus(code.getCode());
        resultData.setMessage(code.getMessage());
        resultData.setData(data);
        return resultData;
    }
}
