package com.sqyy.hospital.common;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sqyy.hospital.service.HospitalPersistenceService;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final FileUploadService fileUploadService;
    private final HospitalPersistenceService persistenceService;

    public UploadController(FileUploadService fileUploadService, HospitalPersistenceService persistenceService) {
        this.fileUploadService = fileUploadService;
        this.persistenceService = persistenceService;
    }

    /** 管理员为任意用户上传头像 */
    @PostMapping("/admin/users/{userId}/avatar")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> uploadUserAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) throws Exception {
        String path = fileUploadService.saveAvatar(file, "user");
        persistenceService.updateUserAvatar(userId, path);
        return ApiResponse.success(path);
    }

    /** 医生/管理员为患者上传头像 */
    @PostMapping("/patients/{patientId}/avatar")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ApiResponse<String> uploadPatientAvatar(
            @PathVariable Long patientId,
            @RequestParam("file") MultipartFile file) throws Exception {
        String path = fileUploadService.saveAvatar(file, "patient");
        persistenceService.updatePatientAvatar(patientId, path);
        return ApiResponse.success(path);
    }

    /** 当前用户上传自己的头像（个人信息） */
    @PostMapping("/auth/profile/avatar")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> uploadMyAvatar(
            Authentication auth,
            @RequestParam("file") MultipartFile file) throws Exception {
        String username = auth.getName();
        var user = persistenceService.listUsers().stream()
                .filter(u -> username.equals(u.username()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("用户不存在"));
        String path = fileUploadService.saveAvatar(file, "user");
        persistenceService.updateUserAvatar(user.id(), path);
        return ApiResponse.success(path);
    }
}
