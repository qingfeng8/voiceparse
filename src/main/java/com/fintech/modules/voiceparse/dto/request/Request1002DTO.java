package com.fintech.modules.voiceparse.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @className: Request1002DTO
 * @package: com.jy.modules.car.dto.request
 * @describe: 转换结果查询接口入参
 * @auther: LiuJianbo
 * @date: 2020/5/25
 * @time: 15:32
 */
@Data
public class Request1002DTO extends RequestCommonDTO implements Serializable {

    private static final long serialVersionUID = 2100277351516117375L;

    /**
     * task_id 任务id
     */
    @NotBlank(message = "task_id不能为空")
    private String task_id;

}
