package com.fintech.modules.common.tool;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @className: HttpClientUtil
 * @package: com.jy.modules.aftloan.autocoll.kcs.util
 * @describe: 调用外部接口的httpClicent工具类
 * @auther: LiuJianbo
 * @date: 2017/12/12
 * @time: 10:36
 */
@SuppressWarnings("deprecation")
public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final String USER_AGENT = "Mozilla/5.0";

    // 设置请求超时30秒钟
    private static int REQUEST_TIMEOUT = 30 * 1000;
    // 连接超时时间
    private static int TIMEOUT = 60 * 1000;
    // 数据传输超时
    private static int SO_TIMEOUT = 60 * 1000;

    /**
     * @param urlStr   外部系统服务url
     * @param paramObj 参数对象
     * @return
     * @methodName: doPost
     * @describe: 调用外部系统服务
     * @auther: LiuJianbo
     * @date: 2018/1/8
     * @time: 17:47
     */
    @SuppressWarnings({"resource"})
    public static Object doPost(String urlStr, Object paramObj) throws Exception {
        String paramStr = com.alibaba.fastjson.JSON.toJSONString(paramObj);
        return doPost(urlStr, paramStr);
    }

    public static Object doPostFrom(String urlStr, String paramKey, String jsonStr) throws Exception {
        return doPostForm(urlStr, paramKey, jsonStr);
    }

    /**
     * @param urlStr  外部系统服务url
     * @param jsonStr JSON串
     * @return
     * @methodName:doPost
     * @describe: 调用外部系统服务
     * @auther: LiuJianbo
     * @date: 2018/1/8
     * @time: 17:49
     */
    public static Object doPost(String urlStr, String jsonStr) throws Exception {
        logger.info("调用外部系统发送的输入参数inputPara:" + jsonStr);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        Object resData = "";
        try {
            HttpPost httpPost = new HttpPost(urlStr.trim());
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).setConnectTimeout(TIMEOUT).setConnectionRequestTimeout(REQUEST_TIMEOUT).setExpectContinueEnabled(false).build();
            httpPost.setConfig(requestConfig);
            // 解决中文乱码问题
            StringEntity entity = new StringEntity(jsonStr, "UTF-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            long startTime = System.currentTimeMillis();
            response = httpClient.execute(httpPost);
            resData = EntityUtils.toString(response.getEntity());
            logger.info(">>> 调用外部系统  耗时：" + (System.currentTimeMillis() - startTime) + "----url---" + urlStr + "接口返回信息---" + resData);
        } catch (Exception e) {
            throw new Exception("调用外部系统异常" + e.getMessage());
        } finally {
            response.close();
            httpClient.close();
        }
        return resData;
    }

    private static Object doPostForm(String urlStr, String paramKey, String paramStr) throws Exception {
        logger.info("调用外部系统发送的输入参数inputPara:" + paramStr);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        Object resData = "";
        try {
            HttpPost httpPost = new HttpPost(urlStr.trim());
            if (StringUtils.isNotEmpty(paramKey)) {
                paramStr = paramKey + "=" + paramStr;
            }
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).setConnectTimeout(TIMEOUT).setConnectionRequestTimeout(REQUEST_TIMEOUT).setExpectContinueEnabled(false).build();
            httpPost.setConfig(requestConfig);
            // 解决中文乱码问题
            StringEntity entity = new StringEntity(paramStr, "UTF-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(entity);
            long startTime = System.currentTimeMillis();
            response = httpClient.execute(httpPost);
            resData = EntityUtils.toString(response.getEntity());
            logger.info(">>> 调用外部系统  耗时：" + (System.currentTimeMillis() - startTime) + "----url---" + urlStr + "接口返回信息---" + resData);
        } catch (Exception e) {
            throw new Exception("调用外部系统异常" + e.getMessage());
        } finally {
            response.close();
            httpClient.close();
        }
        return resData;
    }

}
