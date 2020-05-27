package com.fintech.modules.voiceparse.dto.response;

import lombok.Data;

/**
 * @author XXXXXXXX
 * @Title: ResponseBaseDTO
 * @Package
 * @Description:
 * @date
 */
@Data
public class ResponseCommonDTO<T> {

    /**
     * 返回码
     */
    private String result;
    /**
     * 返回信息
     */
    private String message;

    private T data;

}
