package com.sqyy.hospital;

import com.sqyy.hospital.config.DepartmentConfig;
import com.sqyy.hospital.config.CozeProperties;
import com.sqyy.hospital.config.PharmacyConfig;
import com.sqyy.hospital.config.ReceptionConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.sqyy.hospital.persistence.mapper")
@EnableConfigurationProperties({ DepartmentConfig.class, PharmacyConfig.class, ReceptionConfig.class, CozeProperties.class })
public class CommunityHospitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityHospitalApplication.class, args);
    }
}
