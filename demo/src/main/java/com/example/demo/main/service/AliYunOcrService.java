package com.example.demo.main.service;

import com.aliyun.ocr_api20210707.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AliYunOcrService {

    @Value("${oss.ocr.accessKeyId}")
    private String accessKeyId;

    @Value("${oss.ocr.accessKeySecret}")
    private String accessKeySecret;

    public Client createClient() throws Exception {
        return createClient(accessKeyId, accessKeySecret);
    }

    public Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";
        return new com.aliyun.ocr_api20210707.Client(config);
    }

}
