package com.fintech.modules.voiceparse.service;

import com.alibaba.fastjson.JSON;
import com.fintech.modules.voiceparse.config.IFlyTekConfig;
import com.iflytek.dto.ApiResultDto;
import com.iflytek.util.EncryptUtil;
import com.iflytek.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

/**
 * 非实时转写webapi
 */
@Slf4j
@Service("com.fintech.modules.voiceparse.service.IFlyTekService")
public class IFlyTekService {

    @Autowired
    IFlyTekConfig iFlyTekConfig;

    public static String LFASR_HOST;
    public static String APPID;
    public static String SECRET_KEY;
    /**
     * 文件分片大小,可根据实际情况调整
     */
    // 10M
    public static int SLICE_SICE = 10485760;

    public static final String PREPARE = "/prepare";
    public static final String UPLOAD = "/upload";
    public static final String MERGE = "/merge";
    public static final String GET_RESULT = "/getResult";
    public static final String GET_PROGRESS = "/getProgress";

    @PostConstruct
    public void init() {
        LFASR_HOST = iFlyTekConfig.getLfasrHost();
        APPID = iFlyTekConfig.getAppID();
        SECRET_KEY = iFlyTekConfig.getSecretKey();
        SLICE_SICE = iFlyTekConfig.getSliceSize();
    }

    /**
     * 获取每个接口都必须的鉴权参数
     *
     * @return
     * @throws SignatureException
     */
    public static Map<String, String> getBaseAuthParam(String taskId) throws SignatureException {
        Map<String, String> baseParam = new HashMap<String, String>();
        String ts = String.valueOf(System.currentTimeMillis() / 1000L);
        baseParam.put("app_id", APPID);
        baseParam.put("ts", ts);
        baseParam.put("signa", EncryptUtil.HmacSHA1Encrypt(EncryptUtil.MD5(APPID + ts), SECRET_KEY));
        if (taskId != null) {
            baseParam.put("task_id", taskId);
        }
        return baseParam;
    }

    /**
     * 预处理
     *
     * @param fileLength 需要转写的音频
     * @return
     * @throws SignatureException
     */
    public String prepare(long fileLength, String fileName, String slice_num) throws SignatureException {
        Map<String, String> prepareParam = getBaseAuthParam(null);
        log.info("文件长度：{}", fileLength);
        log.info("文件名称：{}", fileName);

        prepareParam.put("file_len", fileLength + "");
        prepareParam.put("file_name", fileName);

        log.info("文件名称：{} 分片数量 {}", fileName, slice_num);
        prepareParam.put("slice_num", slice_num);

        /******************** 可配置参数********************/
        // 转写类型
        prepareParam.put("lfasr_type", iFlyTekConfig.getLfasrType());
        log.info("文件名称：{} 转写类型 {}", fileName, iFlyTekConfig.getLfasrType());
        // 发音人个数，可选值：0-10，0表示盲分 注：发音人分离目前还是测试效果达不到商用标准，如测试无法满足您的需求，请慎用该功能。
        prepareParam.put("speaker_number", iFlyTekConfig.getSpeakerNumber());
        log.info("文件名称：{} 发音人个数 {}", fileName, iFlyTekConfig.getSpeakerNumber());
        // 结果中是否包含发音人分离信息
        prepareParam.put("has_seperate", iFlyTekConfig.getHasSeperate());
        log.info("文件名称：{} 结果中是否包含发音人分离信息 {}", fileName, iFlyTekConfig.getHasSeperate());
        // 设置1: 通用角色分离 2: 电话信道角色分离（适用于speaker_number为2的说话场景）
        prepareParam.put("role_type", iFlyTekConfig.getRoleType());
        log.info("文件名称：{} 角色类型 {}", fileName, iFlyTekConfig.getRoleType());
        // 设置垂直领域个性化参数: #法院: court
        //#教育: edu
        //#金融: finance
        //#医疗: medical
        //#科技: tech
        if (StringUtils.isNotEmpty(iFlyTekConfig.getPd())) {
            prepareParam.put("pd", iFlyTekConfig.getPd());
            log.info("文件名称：{} 垂直领域个性化参数 {}", fileName, iFlyTekConfig.getPd());
        }
        /****************************************************/

        String response = HttpUtil.post(LFASR_HOST + PREPARE, prepareParam);
        if (response == null) {
            log.error("预处理接口请求失败！");
            throw new RuntimeException("预处理接口请求失败！");
        }
        ApiResultDto resultDto = JSON.parseObject(response, ApiResultDto.class);
        String taskId = resultDto.getData();
        if (resultDto.getOk() != 0 || taskId == null) {
            log.error("预处理失败！{}", response);
            throw new RuntimeException("预处理失败！" + response);
        }
        log.info("预处理成功, taskid：{}", taskId);
        return taskId;
    }

    /**
     * 分片上传
     *
     * @param taskId 任务id
     * @param slice  分片的byte数组
     * @throws SignatureException
     */
    public void uploadSlice(String taskId, String sliceId, byte[] slice) throws SignatureException {
        Map<String, String> uploadParam = getBaseAuthParam(taskId);
        uploadParam.put("slice_id", sliceId);

        String response = HttpUtil.postMulti(LFASR_HOST + UPLOAD, uploadParam, slice);
        if (response == null) {
            throw new RuntimeException("分片上传接口请求失败！taskId:" + taskId);
        }
        if (JSON.parseObject(response).getInteger("ok") == 0) {
            log.info("taskId : {} 分片上传成功, sliceId: {} , sliceLen: {}", taskId, sliceId, slice.length);
            return;
        }
        log.info("params: " + JSON.toJSONString(uploadParam));
        throw new RuntimeException("分片上传失败！" + response + "| taskId:" + taskId);
    }

    /**
     * 文件合并
     *
     * @param taskId 任务id
     * @throws SignatureException
     */
    public void merge(String taskId) throws SignatureException {
        String response = HttpUtil.post(LFASR_HOST + MERGE, getBaseAuthParam(taskId));
        if (response == null) {
            throw new RuntimeException("文件合并接口请求失败！taskId: " + taskId);
        }
        if (JSON.parseObject(response).getInteger("ok") == 0) {
            log.info("文件合并成功, taskId: " + taskId);
            return;
        }
        throw new RuntimeException("文件合并失败！" + response + "taskId:" + taskId);
    }

    /**
     * 获取任务进度
     *
     * @param taskId 任务id
     * @throws SignatureException
     */
    public ApiResultDto getProgress(String taskId) throws SignatureException {
        String response = HttpUtil.post(LFASR_HOST + GET_PROGRESS, getBaseAuthParam(taskId));
        if (response == null) {
            throw new RuntimeException("获取任务进度接口请求失败！taskId: " + taskId);
        }
        return JSON.parseObject(response, ApiResultDto.class);
    }

    /**
     * 获取转写结果
     *
     * @param taskId
     * @return
     * @throws SignatureException
     */
    public String getResult(String taskId) throws SignatureException {
        String responseStr = HttpUtil.post(LFASR_HOST + GET_RESULT, getBaseAuthParam(taskId));
        if (responseStr == null) {
            throw new RuntimeException("获取结果接口请求失败！taskId:" + taskId);
        }
        ApiResultDto response = JSON.parseObject(responseStr, ApiResultDto.class);
        if (response.getOk() != 0) {
            throw new RuntimeException("获取结果失败！" + responseStr + "taskId:" + taskId);
        }
        return response.getData();
    }
}