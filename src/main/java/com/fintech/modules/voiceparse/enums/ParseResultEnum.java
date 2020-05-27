package com.fintech.modules.voiceparse.enums;

import lombok.Getter;

/**
 * @className: ResultEnum
 * @package: com.jy.modules.common.enums
 * @describe: ParseResultEnum
 * @author: LiuJianbo
 * @date: 2020/5/25
 * @time: 16:27
 */
@Getter
public enum ParseResultEnum {

    TO_PARSE("10", "待转写"),
    PARSING("20", "转写中"),
    PARSE_FAILD("30", "转写失败"),
    PARSED("40", "转写完成");
    private String code;

    private String msg;

    ParseResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
