package com.bjpowernode.crm.commons.enums;

public enum CodeEnum {
    Code200("200","请求成功!"),
    Code500("500","系统忙!请稍后重试....");

    private String code;
    private String message;
    CodeEnum(String code,String message){
        this.code=code;
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

}
