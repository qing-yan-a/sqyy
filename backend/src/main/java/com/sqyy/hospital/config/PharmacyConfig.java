package com.sqyy.hospital.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** 药房配置，支持通过 application.yml 扩展 */
@Configuration
@ConfigurationProperties(prefix = "app.pharmacy")
public class PharmacyConfig {

    private static final List<String> DEFAULT = List.of("西药房", "中药房");

    private List<String> list;

    public List<String> getList() {
        return list != null && !list.isEmpty() ? list : DEFAULT;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
