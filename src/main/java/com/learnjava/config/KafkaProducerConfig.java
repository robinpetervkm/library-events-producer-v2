package com.learnjava.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "library.events.producer")
public class KafkaProducerConfig {

    private String sendMode = "async";

    public String getSendMode() {
        return sendMode;
    }

    public void setSendMode(String sendMode) {
        this.sendMode = sendMode;
    }

    public boolean isSync() {
        return "sync".equalsIgnoreCase(sendMode);
    }

    public boolean isAsync() {
        return "async".equalsIgnoreCase(sendMode);
    }
}