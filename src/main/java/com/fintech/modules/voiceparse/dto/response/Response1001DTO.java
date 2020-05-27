package com.fintech.modules.voiceparse.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author XXXXXXXX
 * @Title: Response1001DTO
 * @Package
 * @Description:
 * @date
 */
@Data
public class Response1001DTO implements Serializable {

    private static final long serialVersionUID = 5041558456896693773L;
    //1：通过； 0：不通过；
    private String data;
}
