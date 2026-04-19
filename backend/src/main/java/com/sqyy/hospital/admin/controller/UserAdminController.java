package com.sqyy.hospital.admin.controller;

import com.sqyy.hospital.common.ApiResponse;
import com.sqyy.hospital.config.PharmacyConfig;
import com.sqyy.hospital.config.ReceptionConfig;
import com.sqyy.hospital.service.HospitalPersistenceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    private final HospitalPersistenceService persistenceService;
    private final PharmacyConfig pharmacyConfig;
    private final ReceptionConfig receptionConfig;

    public UserAdminController(HospitalPersistenceService persistenceService, PharmacyConfig pharmacyConfig, ReceptionConfig receptionConfig) {
        this.persistenceService = persistenceService;
        this.pharmacyConfig = pharmacyConfig;
        this.receptionConfig = receptionConfig;
    }

    @GetMapping("/pharmacy")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<String>> listPharmacy() {
        return ApiResponse.success(pharmacyConfig.getList());
    }

    @GetMapping("/reception")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<String>> listReception() {
        return ApiResponse.success(receptionConfig.getList());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Map<String, Object>>> listUsers() {
        List<Map<String, Object>> users = persistenceService.listUsers().stream()
                .map(user -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", user.id());
                    m.put("username", user.username());
                    m.put("realName", user.realName());
                    m.put("idCard", user.idCard() != null ? user.idCard() : "");
                    m.put("avatar", user.avatar() != null ? user.avatar() : "");
                    m.put("department", user.department() != null ? user.department() : "");
                    m.put("pharmacy", user.pharmacy() != null ? user.pharmacy() : "");
                    m.put("receptionDesk", user.receptionDesk() != null ? user.receptionDesk() : "");
                    m.put("roles", user.roles());
                    return m;
                })
                .toList();
        return ApiResponse.success(users);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Map<String, Object>>> listRoles() {
        return ApiResponse.success(persistenceService.listRoles());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> createUser(@Valid @RequestBody CreateUserRequest request) {
        var user = persistenceService.createUser(
                request.username(),
                request.password(),
                request.realName(),
                request.phone(),
                request.idCard(),
                request.roles(),
                request.department(),
                request.pharmacy(),
                request.receptionDesk()
        );
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", user.id());
        m.put("username", user.username());
        m.put("realName", user.realName());
        m.put("idCard", user.idCard() != null ? user.idCard() : "");
        m.put("avatar", user.avatar() != null ? user.avatar() : "");
        m.put("department", user.department() != null ? user.department() : "");
        m.put("pharmacy", user.pharmacy() != null ? user.pharmacy() : "");
        m.put("receptionDesk", user.receptionDesk() != null ? user.receptionDesk() : "");
        m.put("roles", user.roles());
        return ApiResponse.success(m);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        persistenceService.deleteUser(userId);
        return ApiResponse.success();
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> updateUser(@PathVariable Long userId,
                                                        @Valid @RequestBody UpdateUserRequest request) {
        var user = persistenceService.updateUser(
                userId,
                request.realName(),
                request.phone(),
                request.idCard(),
                request.roles(),
                request.department(),
                request.pharmacy(),
                request.receptionDesk()
        );
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", user.id());
        m.put("username", user.username());
        m.put("realName", user.realName());
        m.put("phone", user.phone() != null ? user.phone() : "");
        m.put("idCard", user.idCard() != null ? user.idCard() : "");
        m.put("avatar", user.avatar() != null ? user.avatar() : "");
        m.put("department", user.department() != null ? user.department() : "");
        m.put("pharmacy", user.pharmacy() != null ? user.pharmacy() : "");
        m.put("receptionDesk", user.receptionDesk() != null ? user.receptionDesk() : "");
        m.put("roles", user.roles());
        return ApiResponse.success(m);
    }

    public record UpdateUserRequest(
            @NotBlank(message = "姓名不能为空") String realName,
            @NotBlank(message = "手机号不能为空") String phone,
            @NotBlank(message = "身份证号不能为空") String idCard,
            List<String> roles,
            String department,
            String pharmacy,
            String receptionDesk
    ) {
    }

    public record CreateUserRequest(
            @NotBlank(message = "用户名不能为空") String username,
            @NotBlank(message = "密码不能为空") String password,
            @NotBlank(message = "姓名不能为空") String realName,
            @NotBlank(message = "手机号不能为空") String phone,
            @NotBlank(message = "身份证号不能为空") String idCard,
            List<String> roles,
            String department,
            String pharmacy,
            String receptionDesk
    ) {
    }
}
