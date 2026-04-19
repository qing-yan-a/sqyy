package com.sqyy.hospital.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_SIZE = 2 * 1024 * 1024; // 2MB

    @Value("${app.upload.dir:}")
    private String uploadDir;

    private Path getUploadBaseDir() {
        if (uploadDir != null && !uploadDir.isBlank()) {
            Path p = Path.of(uploadDir);
            return p.isAbsolute() ? p : Path.of(System.getProperty("user.dir")).resolve(p);
        }
        return Path.of(System.getProperty("user.home"), ".hospital-uploads", "avatars");
    }

    public String saveAvatar(MultipartFile file, String prefix) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("图片大小不能超过 2MB");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new BusinessException("文件名无效");
        }
        String ext = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BusinessException("仅支持 jpg、png、gif、webp 格式");
        }

        Path dir = getUploadBaseDir();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String filename = prefix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12) + "." + ext;
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());
        return "/avatars/" + filename;
    }
}
