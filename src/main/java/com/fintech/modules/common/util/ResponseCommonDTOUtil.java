package com.fintech.modules.common.util;

import com.fintech.modules.common.enums.ResultEnum;
import com.fintech.modules.voiceparse.dto.response.ResponseCommonDTO;

/**
 * @className: ${CLASS_NAME}
 * @package: com.jy.modules.common.util
 * @describe: TODO
 * @author: LiuJianbo
 * @date: 2020/5/25
 * @time: 21:36
 */
public class ResponseCommonDTOUtil {

    public static ResponseCommonDTO success(Object object) {
        ResponseCommonDTO responseCommonDTO = new ResponseCommonDTO();
        responseCommonDTO.setData(object);
        responseCommonDTO.setResult(ResultEnum.SUCCESS.getCode());
        responseCommonDTO.setMessage(ResultEnum.SUCCESS.getMsg());
        return responseCommonDTO;
    }

    public static ResponseCommonDTO success() {
        return success(null);
    }

    public static ResponseCommonDTO error(String code, String msg) {
        ResponseCommonDTO responseCommonDTO = new ResponseCommonDTO();
        responseCommonDTO.setResult(code);
        responseCommonDTO.setMessage(msg);
        return responseCommonDTO;
    }

    public static ResponseCommonDTO error(String msg) {
        ResponseCommonDTO responseCommonDTO = new ResponseCommonDTO();
        responseCommonDTO.setResult(ResultEnum.FAILED.getCode());
        responseCommonDTO.setMessage(msg);
        return responseCommonDTO;
    }

    public static ResponseCommonDTO error(ResultEnum resultEnum) {
        ResponseCommonDTO responseCommonDTO = new ResponseCommonDTO();
        responseCommonDTO.setResult(resultEnum.getCode());
        responseCommonDTO.setMessage(resultEnum.getMsg());
        return responseCommonDTO;
    }

}
