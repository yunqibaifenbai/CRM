package com.bjpowernode.crm.commons.domain;

import lombok.Data;

@Data
public class ReturnObject {
    private String code;
    private String message;
    private Object retData;

    public ReturnObject(String code,String message){
        this.code=code;
        this.message=message;
    }
    public ReturnObject(String code,String message,Object retData){
        this.code=code;
        this.message=message;
        this.retData=retData;
    }
    public ReturnObject(Object retData){
        this.retData=retData;
    }
    public ReturnObject(String code,Object retData){
        this.code=code;
        this.retData=retData;
    }
}
