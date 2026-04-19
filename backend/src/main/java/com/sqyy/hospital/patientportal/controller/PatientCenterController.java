package com.sqyy.hospital.patientportal.controller;

import com.sqyy.hospital.auth.TokenService;
import com.sqyy.hospital.common.ApiResponse;
import com.sqyy.hospital.common.FileUploadService;
import com.sqyy.hospital.service.HospitalPersistenceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/patient-center")
public class PatientCenterController {

    private final HospitalPersistenceService persistenceService;
    private final TokenService tokenService;
    private final FileUploadService fileUploadService;

    public PatientCenterController(HospitalPersistenceService persistenceService, TokenService tokenService,
                                   FileUploadService fileUploadService) {
        this.persistenceService = persistenceService;
        this.tokenService = tokenService;
        this.fileUploadService = fileUploadService;
    }

    /**
     * 患者注册
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody PatientRegisterRequest request) {
        var user = persistenceService.registerPatient(
                request.username(), request.password(), request.name(),
                request.gender(), request.age(), request.phone(), request.idCard(),
                request.address(), request.allergyHistory(), request.medicalHistory()
        );
        return ApiResponse.success(Map.of("message", "注册成功", "userId", user.id()));
    }

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<Map<String, Object>> getProfile(HttpServletRequest request) {
        String username = getUsername(request);
        return ApiResponse.success(persistenceService.getPatientCenterProfile(username));
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<Void> updateProfile(@RequestBody UpdateProfileRequest body, HttpServletRequest request) {
        String username = getUsername(request);
        persistenceService.updatePatientCenterProfile(username, body.phone(), body.address(),
                body.allergyHistory(), body.medicalHistory());
        return ApiResponse.success();
    }

    /**
     * 我的病例时间轴
     */
    @GetMapping("/timeline")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<Map<String, Object>> myTimeline(HttpServletRequest request) {
        String username = getUsername(request);
        Map<String, Object> profile = persistenceService.getPatientCenterProfile(username);
        Long patientId = (Long) profile.get("patientId");
        return ApiResponse.success(persistenceService.buildTimeline(patientId));
    }

    /**
     * 患者自助挂号
     */
    @PostMapping("/registration")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<Map<String, Object>> registerVisit(@Valid @RequestBody PatientRegistrationRequest body,
                                                           HttpServletRequest request) {
        String username = getUsername(request);
        Map<String, Object> result = persistenceService.patientRegister(
                username, body.department(), body.doctorName(), body.appointmentDate(), body.timeSlot()
        );
        return ApiResponse.success(result);
    }

    /**
     * 我的就诊/挂号记录
     */
    @GetMapping("/registrations")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<List<?>> myRegistrations(HttpServletRequest request) {
        String username = getUsername(request);
        Map<String, Object> profile = persistenceService.getPatientCenterProfile(username);
        Long patientId = (Long) profile.get("patientId");
        return ApiResponse.success(persistenceService.listVisits(patientId));
    }

    /**
     * 退号：取消待就诊的挂号
     */
    @PostMapping("/registrations/{visitId}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<Void> cancelRegistration(@PathVariable Long visitId, HttpServletRequest request) {
        String username = getUsername(request);
        persistenceService.cancelRegistration(username, visitId);
        return ApiResponse.success();
    }

    /**
     * 患者端获取医生列表
     */
    @GetMapping("/doctors")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<List<Map<String, Object>>> listDoctors(@RequestParam(required = false) String department) {
        List<Map<String, Object>> doctors = persistenceService.listDoctorUsers(department).stream()
                .map(u -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", u.id());
                    m.put("realName", u.realName());
                    m.put("username", u.username());
                    m.put("department", u.department() != null ? u.department() : "");
                    return m;
                })
                .toList();
        return ApiResponse.success(doctors);
    }

    /**
     * 患者端上传头像
     */
    @PostMapping("/avatar")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<String> uploadMyAvatar(@RequestParam("file") MultipartFile file,
                                              HttpServletRequest request) throws Exception {
        String username = getUsername(request);
        Map<String, Object> profile = persistenceService.getPatientCenterProfile(username);
        Long patientId = (Long) profile.get("patientId");
        // 更新 sys_user 头像
        String path = fileUploadService.saveAvatar(file, "user");
        Long userId = (Long) profile.get("userId");
        persistenceService.updateUserAvatar(userId, path);
        // 同步更新 patient 头像
        persistenceService.updatePatientAvatar(patientId, path);
        return ApiResponse.success(path);
    }

    private String getUsername(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return tokenService.parse(header.substring(7)).getSubject();
        }
        return "";
    }

    public record PatientRegisterRequest(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String name,
            @NotBlank String gender,
            @Min(0) int age,
            String phone,
            String idCard,
            String address,
            String allergyHistory,
            String medicalHistory
    ) {}

    public record UpdateProfileRequest(
            String phone,
            String address,
            String allergyHistory,
            String medicalHistory
    ) {}

    public record PatientRegistrationRequest(
            @NotBlank String department,
            @NotBlank String doctorName,
            @NotBlank String appointmentDate,
            @NotBlank String timeSlot
    ) {}
}
