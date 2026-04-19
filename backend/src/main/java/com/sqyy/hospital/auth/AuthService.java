package com.sqyy.hospital.auth;

import com.sqyy.hospital.model.HospitalModels;
import com.sqyy.hospital.service.HospitalPersistenceService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthService {

    private final HospitalPersistenceService persistenceService;
    private final TokenService tokenService;

    public AuthService(HospitalPersistenceService persistenceService, TokenService tokenService) {
        this.persistenceService = persistenceService;
        this.tokenService = tokenService;
    }

    public Map<String, Object> login(String username, String password) {
        HospitalModels.UserAccount user = persistenceService.authenticate(username, password);
        String token = tokenService.generateToken(user.id(), user.username(), user.roles());

        Map<String, Object> profile = buildProfileMap(user);

        return Map.of(
                "token", token,
                "user", profile
        );
    }

    public Map<String, Object> buildProfile(String username) {
        HospitalModels.UserAccount user = persistenceService.listUsers().stream()
                .filter(item -> item.username().equals(username))
                .findFirst()
                .orElseThrow();
        return buildProfileMap(user);
    }

    private Map<String, Object> buildProfileMap(HospitalModels.UserAccount user) {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("id", user.id());
        p.put("username", user.username());
        p.put("realName", user.realName());
        p.put("phone", user.phone() != null ? user.phone() : "");
        p.put("idCard", user.idCard() != null ? user.idCard() : "");
        p.put("avatar", user.avatar() != null ? user.avatar() : "");
        p.put("roles", user.roles());
        p.put("department", user.department() != null ? user.department() : "");
        p.put("pharmacy", user.pharmacy() != null ? user.pharmacy() : "");
        p.put("receptionDesk", user.receptionDesk() != null ? user.receptionDesk() : "");
        // 患者端：返回绑定的 patientId
        p.put("patientId", user.patientId());
        return p;
    }

    public void updateProfile(String username, String phone, String idCard) {
        persistenceService.updateUserProfile(username, phone, idCard);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        persistenceService.changePassword(username, oldPassword, newPassword);
    }
}
