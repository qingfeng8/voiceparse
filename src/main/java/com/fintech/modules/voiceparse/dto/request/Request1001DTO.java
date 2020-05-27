package com.fintech.modules.voiceparse.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * 语音文件上传入参定义
 */
@Data
public class Request1001DTO extends RequestCommonDTO implements Serializable {

    private static final long serialVersionUID = 2235877351516117375L;

    @NotBlank(message = "音频文件url不能为空")
    private String audio_url;

    @NotBlank(message = "音频文件名称不能为空")
    private String audio_file;

    private String ext_parameter;

}
