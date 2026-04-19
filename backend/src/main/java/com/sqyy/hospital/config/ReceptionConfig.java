package com.sqyy.hospital.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** 前台配置，支持通过 application.yml 扩展 */
@Configuration
@ConfigurationProperties(prefix = "app.reception")
public class ReceptionConfig {

    private static final List<String> DEFAULT = List.of("一号前台", "二号前台");

    private List<String> list;

    public List<String> getList() {
        return list != null && !list.isEmpty() ? list : DEFAULT;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
