package com.hhz.bms.bms.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum StatusCode {
    SUCCESS(200, "OK"),
    OPERATION_SUCCESS(201, "操作成功"),

    OPERATION_FAILED(501, "操作失败"),
    VALIDATE_ERROR(502, "请求参数错误"),
    DATA_UNEXISTS(503, "请求数据不存在");

    //状态码、状态码描述
    private Integer code;
    private String msg;
}