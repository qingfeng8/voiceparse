package com.fintech.modules.voiceparse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "iflytek")
@Data
public class IFlyTekConfig {

    String lfasrHost;
    String appID;
    String secretKey;
    int sliceSize;

    String lfasrType;
    String speakerNumber;
    String hasSeperate;
    String roleType;
    String pd;

}
