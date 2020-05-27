package com.fintech.modules.voiceparse.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 语音转换结果查询
 */
@Data
public class Response1002DTO implements Serializable {

    /**
     * 转写进度
     */
    private String asr_progress;
    /**
     * 转写进度描述
     */
    private String asr_progress_label;
    /**
     * 用户提交音频文件时传递扩展参数的值5
     */
    //private String ext_parameter;
    /**
     * 语音转换结果文本
     */
    private String asr_text;


}
