package com.fintech.modules.voiceparse.rest;

import com.fintech.modules.voiceparse.dto.request.Request1001DTO;
import com.fintech.modules.voiceparse.dto.request.Request1002DTO;
import com.fintech.modules.voiceparse.dto.response.Response1002DTO;
import com.fintech.modules.voiceparse.dto.response.ResponseCommonDTO;
import com.fintech.modules.voiceparse.service.VoiceParseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 语音转换rest接口定义
 */
@Controller
@RequestMapping(value = "/api/voice/parse")
public class VoiceParseRest {

    @Autowired
    private VoiceParseService voiceParseService;

    @ResponseBody
    @RequestMapping(value = "/1001/v1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseCommonDTO<String> uploadFile(@RequestBody Request1001DTO request) throws Exception {
        return voiceParseService.uploadFileNew(request);
    }

    @ResponseBody
    @RequestMapping(value = "/1002/v1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseCommonDTO<Response1002DTO> queryParseResult(@RequestBody Request1002DTO request) throws Exception {

        return voiceParseService.queryParseResult(request);
    }

}
