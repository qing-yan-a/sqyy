package com.sqyy.hospital.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:}")
    private String uploadDir;

    private Path getUploadBaseDir() {
        if (uploadDir != null && !uploadDir.isBlank()) {
            Path p = Path.of(uploadDir);
            return p.isAbsolute() ? p : Path.of(System.getProperty("user.dir")).resolve(p);
        }
        return Path.of(System.getProperty("user.home"), ".hospital-uploads", "avatars");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = getUploadBaseDir();
        String location = "file:" + dir + "/";
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations(location);
    }
}
