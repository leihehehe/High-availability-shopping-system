package com.leih.commonutil.util;

public enum ReturnCode {
    RC100(100,"success"),
    RC999(999,"failed"),
    RC300(500,"server is busy"),
    RC201(201,"The rate is limited"),
    RC202(202,"Unauthorized access"),
    RC500(500, "System error"),
    OUT_OF_STOCK(2001,"The item is out of stock"),
    DEAL_NO_EXIST(2002,"The deal does not exist"),
    USER_LIMITED(2002,"The user is limited");
    private final int code;
    private final String message;
    ReturnCode(int code, String message){
        this.code=code;
        this.message=message;
    }
    public int getCode(){
        return code;
    }
    public String getMessage(){
        return message;
    }
}
