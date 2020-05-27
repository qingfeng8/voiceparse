package com.fintech.modules.common.enums;

import lombok.Getter;

/**
 * @className: ResultEnum
 * @package: com.jy.modules.common.enums
 * @describe: ResultEnum
 * @author: LiuJianbo
 * @date: 2019/2/1
 * @time: 16:27
 */
@Getter
public enum ResultEnum {

    SUCCESS("200", "成功"),
    INVALID_PARAM("1101", "参数校验异常"),
    PARAM_CONVERT_ERROR("1102", "参数转换异常,请检查参数格式"),
    REQUEST_TO_FAST("1103", "申请频率太快，请稍后"),
    FAILED("201", "失败"),
    AUDIO_FILE_NOT_FOUND("301", "音频文件未找到"),
    ERROR("9999", "系统处理异常"),

  ;

    private String code;

    private String msg;

    ResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
