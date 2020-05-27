package com.fintech.modules.voiceparse.service;

import com.alibaba.fastjson.JSON;
import com.fintech.modules.common.enums.ResultEnum;
import com.fintech.modules.common.util.ResponseCommonDTOUtil;
import com.fintech.modules.voiceparse.dto.request.Request1001DTO;
import com.fintech.modules.voiceparse.dto.request.Request1002DTO;
import com.fintech.modules.voiceparse.dto.response.Response1002DTO;
import com.fintech.modules.voiceparse.dto.response.ResponseCommonDTO;
import com.fintech.modules.voiceparse.enums.ParseResultEnum;
import com.iflytek.dto.ApiResultDto;
import com.iflytek.util.SliceIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * VoiceParseService
 */
@Service("com.fintech.modules.voiceparse.service.VoiceParseService")
@Slf4j
public class VoiceParseService implements Serializable {
    private static final long serialVersionUID = 1L;

    @Autowired
    private IFlyTekService iFlyTekService;

    /**
     * @methodName: uploadFileNew
     * @param: request
     * @describe: 文件上传
     * @auther: LiuJianbo
     * @date: 2020/5/25
     * @time: 18:23
     */
    public ResponseCommonDTO<String> uploadFileNew(Request1001DTO request) throws Exception {
        String audio_url = request.getAudio_url();
        log.info("audio_url:{}", audio_url);
        URL url = new URL(audio_url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        //conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //conn.setRequestProperty("lfwywxqyh_token",toekn);

        //得到输入流
        InputStream inputStream = null;
        try {
            inputStream = conn.getInputStream();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseCommonDTOUtil.error(ResultEnum.AUDIO_FILE_NOT_FOUND.getMsg());
        }

        int fileLength = conn.getContentLength();

        byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
        log.info("下载文件的数组长度{}", bytes.length);

        String slice_num = (fileLength / IFlyTekService.SLICE_SICE) + (fileLength % IFlyTekService.SLICE_SICE == 0 ? 0 : 1) + "";
        String taskId = iFlyTekService.prepare(conn.getContentLengthLong(), request.getAudio_file(), slice_num);

        // 分片上传文件
        byte[] slice = new byte[IFlyTekService.SLICE_SICE];
        SliceIdGenerator generator = new SliceIdGenerator();

        for (int i = 0; i < Integer.valueOf(slice_num); i++) {
            int start = i * IFlyTekService.SLICE_SICE;
            if (i == (Integer.valueOf(slice_num) - 1)) {
                log.info("start:{}, end:{}", start, bytes.length);
                slice = Arrays.copyOfRange(bytes, start, bytes.length);
            } else {
                int end = (i + 1) * IFlyTekService.SLICE_SICE;
                log.info("start:{}, end:{}", start, end);
                slice = Arrays.copyOfRange(bytes, start, end);
            }
            iFlyTekService.uploadSlice(taskId, generator.getNextSliceId(), slice);
        }

        // 合并文件
        iFlyTekService.merge(taskId);

        if (inputStream != null) {
            inputStream.close();
        }

        log.info("info:" + url + " download success");

        return ResponseCommonDTOUtil.success(taskId);
    }

    /**
     * @methodName: uploadFile
     * @param: request
     * @describe: 文件上传
     * @auther: LiuJianbo
     * @date: 2020/5/25
     * @time: 18:23
     */
    public ResponseCommonDTO<String> uploadFile(Request1001DTO request) throws Exception {
        String audio_url = request.getAudio_url();
        log.info("audio_url:{}", audio_url);
        URL url = new URL(audio_url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        //conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //conn.setRequestProperty("lfwywxqyh_token",toekn);

        //得到输入流
        InputStream inputStream = null;
        try {
            inputStream = conn.getInputStream();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseCommonDTOUtil.error(ResultEnum.AUDIO_FILE_NOT_FOUND.getMsg());
        }

        //获取自己数组
        //byte[] getData = FileUtil.readInputStream(inputStream);

        long fileLength = conn.getContentLength();
        String slice_num = (fileLength / IFlyTekService.SLICE_SICE) + (fileLength % IFlyTekService.SLICE_SICE == 0 ? 0 : 1) + "";

        String taskId = iFlyTekService.prepare(fileLength, request.getAudio_file(), slice_num);

        // 分片上传文件
        int len = 0;
        byte[] slice = new byte[IFlyTekService.SLICE_SICE];
        SliceIdGenerator generator = new SliceIdGenerator();
        while ((len = inputStream.read(slice)) > 0) {
            // 上传分片
            if (inputStream.available() == 0) {
                slice = Arrays.copyOfRange(slice, 0, len);
            }
            iFlyTekService.uploadSlice(taskId, generator.getNextSliceId(), slice);
        }

        // 合并文件
        iFlyTekService.merge(taskId);

        if (inputStream != null) {
            inputStream.close();
        }

        log.info("info:" + url + " download success");

        return ResponseCommonDTOUtil.success(taskId);
    }


    /**
     * @methodName: queryParseResult
     * @param: request
     * @describe: 进件提交
     * @auther: LiuJianbo
     * @date: 2020/5/25
     * @time: 18:24
     */
    public ResponseCommonDTO<Response1002DTO> queryParseResult(Request1002DTO request) throws Exception {
        Response1002DTO response1002DTO = new Response1002DTO();
        String task_id = request.getTask_id();
        ApiResultDto taskProgress = iFlyTekService.getProgress(task_id);
        if (taskProgress.getOk() == 0) {
            if (taskProgress.getErr_no() != 0) {
                response1002DTO.setAsr_progress(ParseResultEnum.PARSE_FAILD.getCode());
                response1002DTO.setAsr_progress_label(ParseResultEnum.PARSE_FAILD.getMsg());
                log.info("任务[{}]失败：{}", task_id, JSON.toJSONString(taskProgress));
                return ResponseCommonDTOUtil.error(taskProgress.getFailed());
            }

            String taskStatus = taskProgress.getData();
            if (JSON.parseObject(taskStatus).getInteger("status") == 9) {
                log.info("任务[{}]完成！", task_id);
                // 获取结果
                String result = iFlyTekService.getResult(task_id);
                log.info("任务[{}]转写结果: {}", task_id, result);
                response1002DTO.setAsr_progress(ParseResultEnum.PARSED.getCode());
                response1002DTO.setAsr_progress_label(ParseResultEnum.PARSED.getMsg());
                response1002DTO.setAsr_text(result);
            } else {
                log.info("任务[{}]处理中：{}", task_id, taskStatus);
                response1002DTO.setAsr_progress(ParseResultEnum.PARSING.getCode());
                response1002DTO.setAsr_progress_label(ParseResultEnum.PARSING.getMsg());
            }
        } else {
            log.error("获取任务[{}]进度失败！,{}", task_id, taskProgress.getFailed());
            return ResponseCommonDTOUtil.error(taskProgress.getFailed());
        }

        return ResponseCommonDTOUtil.success(response1002DTO);
    }

}
