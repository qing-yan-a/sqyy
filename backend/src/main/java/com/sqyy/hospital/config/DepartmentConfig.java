package com.sqyy.hospital.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 科室配置，支持通过 application.yml 扩展
 * app.departments 可覆盖默认科室列表
 */
@Configuration
@ConfigurationProperties(prefix = "app.departments")
public class DepartmentConfig {

    private static final List<String> DEFAULT_DEPARTMENTS = List.of(
            "全科诊室",
            "预防保健室",
            "妇幼保健诊室"
    );

    private List<String> list;

    public List<String> getList() {
        return list != null && !list.isEmpty() ? list : DEFAULT_DEPARTMENTS;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
