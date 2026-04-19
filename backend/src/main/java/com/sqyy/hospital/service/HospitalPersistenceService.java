package com.sqyy.hospital.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqyy.hospital.common.BusinessException;
import com.sqyy.hospital.model.HospitalModels;
import com.sqyy.hospital.persistence.entity.*;
import com.sqyy.hospital.persistence.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class HospitalPersistenceService {

    private static final String PAYMENT_UNPAID = "UNPAID";
    private static final String PAYMENT_PAID = "PAID";
    private static final String PICKUP_PENDING = "PENDING";
    private static final String PICKUP_DISPENSED = "DISPENSED";
    private static final String INJECTION_NOT_REQUIRED = "NOT_REQUIRED";
    private static final String INJECTION_PENDING = "PENDING";
    private static final String INJECTION_COMPLETED = "COMPLETED";

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PatientMapper patientMapper;
    private final VisitRecordMapper visitRecordMapper;
    private final DiagnosisRecordMapper diagnosisRecordMapper;
    private final PrescriptionRecordMapper prescriptionRecordMapper;
    private final DrugInfoMapper drugInfoMapper;
    private final DrugInventoryLogMapper drugInventoryLogMapper;

    public HospitalPersistenceService(SysUserMapper sysUserMapper,
                                      SysRoleMapper sysRoleMapper,
                                      SysUserRoleMapper sysUserRoleMapper,
                                      PatientMapper patientMapper,
                                      VisitRecordMapper visitRecordMapper,
                                      DiagnosisRecordMapper diagnosisRecordMapper,
                                      PrescriptionRecordMapper prescriptionRecordMapper,
                                      DrugInfoMapper drugInfoMapper,
                                      DrugInventoryLogMapper drugInventoryLogMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.patientMapper = patientMapper;
        this.visitRecordMapper = visitRecordMapper;
        this.diagnosisRecordMapper = diagnosisRecordMapper;
        this.prescriptionRecordMapper = prescriptionRecordMapper;
        this.drugInfoMapper = drugInfoMapper;
        this.drugInventoryLogMapper = drugInventoryLogMapper;
    }

    public HospitalModels.UserAccount authenticate(String username, String password) {
        SysUserEntity user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUsername, username)
                .eq(SysUserEntity::getPassword, password)
                .eq(SysUserEntity::getStatus, 1)
                .last("limit 1"));
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        List<String> roles = listRoleCodesByUserIds(List.of(user.getId())).getOrDefault(user.getId(), List.of());
        return new HospitalModels.UserAccount(user.getId(), user.getUsername(), user.getPassword(), user.getRealName(),
                user.getPhone(), user.getIdCard(), user.getAvatar(), roles, user.getDepartment(), user.getPharmacy(), user.getReceptionDesk(), user.getPatientId());
    }

    public List<HospitalModels.UserAccount> listUsers() {
        List<SysUserEntity> users = sysUserMapper.selectList(new LambdaQueryWrapper<SysUserEntity>()
                .orderByAsc(SysUserEntity::getId));
        Map<Long, List<String>> roleMap = listRoleCodesByUserIds(users.stream().map(SysUserEntity::getId).toList());
        return users.stream()
                .map(user -> new HospitalModels.UserAccount(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getRealName(),
                        user.getPhone(),
                        user.getIdCard(),
                        user.getAvatar(),
                        roleMap.getOrDefault(user.getId(), List.of()),
                        user.getDepartment(),
                        user.getPharmacy(),
                        user.getReceptionDesk(),
                        user.getPatientId()
                ))
                .toList();
    }

    public List<HospitalModels.UserAccount> listDoctorUsers() {
        return listDoctorUsers(null);
    }

    public List<HospitalModels.UserAccount> listDoctorUsers(String department) {
        return listUsers().stream()
                .filter(u -> u.roles().contains("DOCTOR"))
                .filter(u -> department == null || department.isBlank() || (u.department() != null && u.department().equals(department)))
                .toList();
    }

    public long countTodayPendingByDoctor(String doctorName) {
        if (doctorName == null || doctorName.isBlank()) return 0;
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = todayStart.plusDays(1);
        return visitRecordMapper.selectCount(new LambdaQueryWrapper<VisitRecordEntity>()
                .eq(VisitRecordEntity::getDoctorName, doctorName.trim())
                .eq(VisitRecordEntity::getStatus, "PENDING")
                .ge(VisitRecordEntity::getVisitTime, todayStart)
                .lt(VisitRecordEntity::getVisitTime, todayEnd));
    }

    public List<Map<String, Object>> listRoles() {
        return sysRoleMapper.selectList(new LambdaQueryWrapper<SysRoleEntity>()
                        .orderByAsc(SysRoleEntity::getId))
                .stream()
                .map(role -> Map.<String, Object>of(
                        "id", role.getId(),
                        "roleCode", role.getRoleCode(),
                        "roleName", role.getRoleName()
                ))
                .toList();
    }

    @Transactional
    public HospitalModels.UserAccount createUser(String username, String password, String realName, String phone, String idCard, List<String> roleCodes, String department, String pharmacy, String receptionDesk) {
        if (sysUserMapper.selectOne(new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUsername, username)
                .last("limit 1")) != null) {
            throw new BusinessException("用户名已存在");
        }
        List<String> codes = roleCodes != null && !roleCodes.isEmpty() ? roleCodes : List.of("RECEPTION");
        if (codes.contains("DOCTOR") && (department == null || department.isBlank())) {
            throw new BusinessException("医生必须选择所属科室");
        }
        if (codes.contains("PHARMACIST") && (pharmacy == null || pharmacy.isBlank())) {
            throw new BusinessException("药师必须选择所属药房");
        }
        if (codes.contains("RECEPTION") && (receptionDesk == null || receptionDesk.isBlank())) {
            throw new BusinessException("前台必须选择所属前台");
        }
        SysUserEntity entity = new SysUserEntity();
        entity.setUsername(username);
        entity.setPassword(password);
        entity.setRealName(realName);
        entity.setPhone(phone);
        entity.setIdCard(idCard != null ? idCard.trim() : "");
        entity.setDepartment(codes.contains("DOCTOR") ? department : null);
        entity.setPharmacy(codes.contains("PHARMACIST") ? pharmacy : null);
        entity.setReceptionDesk(codes.contains("RECEPTION") ? receptionDesk : null);
        entity.setStatus(1);
        sysUserMapper.insert(entity);

        List<SysRoleEntity> roles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRoleEntity>()
                .in(SysRoleEntity::getRoleCode, codes));
        for (SysRoleEntity role : roles) {
            SysUserRoleEntity ur = new SysUserRoleEntity();
            ur.setUserId(entity.getId());
            ur.setRoleId(role.getId());
            sysUserRoleMapper.insert(ur);
        }
        List<String> assignedCodes = roles.stream().map(SysRoleEntity::getRoleCode).toList();
        return new HospitalModels.UserAccount(entity.getId(), entity.getUsername(), entity.getPassword(), entity.getRealName(),
                entity.getPhone(), entity.getIdCard(), entity.getAvatar(), assignedCodes, entity.getDepartment(), entity.getPharmacy(), entity.getReceptionDesk(), entity.getPatientId());
    }

    @Transactional
    public void deleteUser(Long userId) {
        SysUserEntity user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            throw new BusinessException("admin 用户不能被删除");
        }
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRoleEntity>().eq(SysUserRoleEntity::getUserId, userId));
        sysUserMapper.deleteById(userId);
    }

    @Transactional
    public HospitalModels.UserAccount updateUser(Long userId, String realName, String phone, String idCard,
                                                  List<String> roleCodes, String department, String pharmacy, String receptionDesk) {
        SysUserEntity entity = sysUserMapper.selectById(userId);
        if (entity == null) {
            throw new BusinessException("用户不存在");
        }
        List<String> codes = roleCodes != null && !roleCodes.isEmpty() ? roleCodes : List.of();
        if (codes.contains("DOCTOR") && (department == null || department.isBlank())) {
            throw new BusinessException("医生必须选择所属科室");
        }
        if (codes.contains("PHARMACIST") && (pharmacy == null || pharmacy.isBlank())) {
            throw new BusinessException("药师必须选择所属药房");
        }
        if (codes.contains("RECEPTION") && (receptionDesk == null || receptionDesk.isBlank())) {
            throw new BusinessException("前台必须选择所属前台");
        }
        if (realName != null) entity.setRealName(realName);
        if (phone != null) entity.setPhone(phone);
        if (idCard != null) entity.setIdCard(idCard.trim());
        entity.setDepartment(codes.contains("DOCTOR") ? department : null);
        entity.setPharmacy(codes.contains("PHARMACIST") ? pharmacy : null);
        entity.setReceptionDesk(codes.contains("RECEPTION") ? receptionDesk : null);
        sysUserMapper.updateById(entity);

        // 更新角色
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRoleEntity>().eq(SysUserRoleEntity::getUserId, userId));
        if (!codes.isEmpty()) {
            List<SysRoleEntity> roles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRoleEntity>()
                    .in(SysRoleEntity::getRoleCode, codes));
            for (SysRoleEntity role : roles) {
                SysUserRoleEntity ur = new SysUserRoleEntity();
                ur.setUserId(userId);
                ur.setRoleId(role.getId());
                sysUserRoleMapper.insert(ur);
            }
        }

        List<String> assignedCodes = sysRoleMapper.selectList(
                        new LambdaQueryWrapper<SysRoleEntity>().in(SysRoleEntity::getId,
                                sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRoleEntity>()
                                        .eq(SysUserRoleEntity::getUserId, userId))
                                        .stream().map(SysUserRoleEntity::getRoleId).toList()))
                .stream().map(SysRoleEntity::getRoleCode).toList();
        return new HospitalModels.UserAccount(entity.getId(), entity.getUsername(), entity.getPassword(), entity.getRealName(),
                entity.getPhone(), entity.getIdCard(), entity.getAvatar(), assignedCodes, entity.getDepartment(), entity.getPharmacy(), entity.getReceptionDesk(), entity.getPatientId());
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        SysUserEntity user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUsername, username)
                .last("limit 1"));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new BusinessException(401, "原密码错误");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new BusinessException("新密码不能为空");
        }
        user.setPassword(newPassword.trim());
        sysUserMapper.updateById(user);
    }

    public void updateUserProfile(String username, String phone, String idCard) {
        SysUserEntity user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUsername, username)
                .last("limit 1"));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (phone != null) user.setPhone(phone.trim());
        if (idCard != null) user.setIdCard(idCard.trim());
        sysUserMapper.updateById(user);
    }

    public List<HospitalModels.Patient> listPatients() {
        return patientMapper.selectList(new LambdaQueryWrapper<PatientEntity>()
                        .orderByDesc(PatientEntity::getCreatedAt))
                .stream()
                .map(this::toPatient)
                .toList();
    }

    public Map<String, Object> searchPatients(String keyword, int page, int size) {
        String q = keyword == null ? "" : keyword.trim();
        LambdaQueryWrapper<PatientEntity> wrapper = new LambdaQueryWrapper<PatientEntity>()
                .orderByDesc(PatientEntity::getCreatedAt);
        if (!q.isEmpty()) {
            wrapper.and(w -> w
                    .like(PatientEntity::getName, q)
                    .or().like(PatientEntity::getPatientNo, q)
                    .or().like(PatientEntity::getPhone, q)
                    .or().like(PatientEntity::getIdCard, q));
        }
        Page<PatientEntity> pageParam = new Page<>(page, size);
        Page<PatientEntity> result = patientMapper.selectPage(pageParam, wrapper);
        List<HospitalModels.Patient> list = result.getRecords().stream().map(this::toPatient).toList();
        return Map.of(
                "list", list,
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize());
    }

    @Transactional
    public HospitalModels.Patient createPatient(HospitalModels.CreatePatientCommand command) {
        PatientEntity entity = new PatientEntity();
        entity.setPatientNo(generateCode("P"));
        entity.setName(command.name());
        entity.setGender(command.gender());
        entity.setAge(command.age());
        entity.setPhone(command.phone());
        entity.setIdCard(command.idCard());
        entity.setAddress(command.address());
        entity.setAllergyHistory(command.allergyHistory());
        entity.setMedicalHistory(command.medicalHistory());
        patientMapper.insert(entity);
        return toPatient(entity);
    }

    @Transactional
    public HospitalModels.Patient updatePatient(Long patientId,
                                                HospitalModels.CreatePatientCommand command,
                                                String operatorUsername) {
        HospitalModels.UserAccount operator = getUserByUsername(operatorUsername);
        assertCanEditPatient(patientId, operator);
        PatientEntity entity = patientMapper.selectById(patientId);
        if (entity == null) {
            throw new BusinessException("患者不存在");
        }
        entity.setName(command.name());
        entity.setGender(command.gender());
        entity.setAge(command.age());
        entity.setPhone(command.phone());
        entity.setIdCard(command.idCard());
        entity.setAddress(command.address());
        entity.setAllergyHistory(command.allergyHistory());
        entity.setMedicalHistory(command.medicalHistory());
        patientMapper.updateById(entity);
        return toPatient(entity);
    }

    public List<HospitalModels.VisitDetail> listPendingVisitsByDoctor(String doctorName) {
        if (doctorName == null || doctorName.isBlank()) {
            return List.of();
        }
        List<VisitRecordEntity> visits = visitRecordMapper.selectList(
                new LambdaQueryWrapper<VisitRecordEntity>()
                        .eq(VisitRecordEntity::getDoctorName, doctorName.trim())
                        .eq(VisitRecordEntity::getStatus, "PENDING")
                        .orderByAsc(VisitRecordEntity::getQueueNumber)
                        .orderByAsc(VisitRecordEntity::getId));
        return buildVisitDetails(visits);
    }

    @Transactional
    public HospitalModels.VisitDetail createRegistration(Long patientId, String doctorName) {
        ensurePatientExists(patientId);
        String doctor = doctorName != null ? doctorName.trim() : "";
        if (doctor.isEmpty()) {
            throw new BusinessException("请选择医生");
        }

        Integer nextQueue = visitRecordMapper.selectList(
                new LambdaQueryWrapper<VisitRecordEntity>()
                        .eq(VisitRecordEntity::getDoctorName, doctor)
                        .eq(VisitRecordEntity::getStatus, "PENDING"))
                .stream()
                .map(VisitRecordEntity::getQueueNumber)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .map(n -> n + 1)
                .orElse(1);

        VisitRecordEntity visit = new VisitRecordEntity();
        visit.setPatientId(patientId);
        visit.setVisitNo(generateCode("V"));
        visit.setDoctorName(doctor);
        // 根据医生所属科室设置就诊科室，找不到则默认全科门诊
        String department = listDoctorUsers().stream()
                .filter(d -> doctor.equals(d.realName()))
                .map(HospitalModels.UserAccount::department)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("全科门诊");
        visit.setDepartment(department);
        visit.setChiefComplaint("待接诊");
        visit.setVisitTime(LocalDateTime.now());
        visit.setNotes("");
        visit.setStatus("PENDING");
        visit.setQueueNumber(nextQueue);
        visitRecordMapper.insert(visit);
        return buildVisitDetails(List.of(visit)).get(0);
    }

    public List<HospitalModels.VisitDetail> listVisits(Long patientFilter) {
        List<VisitRecordEntity> visits = visitRecordMapper.selectList(new LambdaQueryWrapper<VisitRecordEntity>()
                .eq(patientFilter != null, VisitRecordEntity::getPatientId, patientFilter)
                .orderByDesc(VisitRecordEntity::getVisitTime)
                .orderByDesc(VisitRecordEntity::getId));
        return buildVisitDetails(visits);
    }

    public Map<String, Object> searchVisits(String keyword, int page, int size) {
        String q = keyword == null ? "" : keyword.trim();
        LambdaQueryWrapper<VisitRecordEntity> wrapper = new LambdaQueryWrapper<VisitRecordEntity>()
                .orderByDesc(VisitRecordEntity::getVisitTime)
                .orderByDesc(VisitRecordEntity::getId);
        if (!q.isEmpty()) {
            wrapper.and(w -> w
                    .like(VisitRecordEntity::getVisitNo, q)
                    .or().like(VisitRecordEntity::getDoctorName, q)
                    .or().like(VisitRecordEntity::getDepartment, q)
                    .or().like(VisitRecordEntity::getChiefComplaint, q));
        }
        Page<VisitRecordEntity> pageParam = new Page<>(page, size);
        Page<VisitRecordEntity> result = visitRecordMapper.selectPage(pageParam, wrapper);
        List<HospitalModels.VisitDetail> list = buildVisitDetails(result.getRecords());
        return Map.of(
                "list", list,
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize());
    }

    public List<HospitalModels.VisitDetail> listVisitsWithInjectionPrescriptions() {
        List<Long> visitIds = prescriptionRecordMapper.selectList(
                        new LambdaQueryWrapper<PrescriptionRecordEntity>()
                                .eq(PrescriptionRecordEntity::getDrugType, "INJECTION")
                                .eq(PrescriptionRecordEntity::getInjectionStatus, INJECTION_PENDING))
                .stream()
                .map(PrescriptionRecordEntity::getVisitId)
                .distinct()
                .toList();
        if (visitIds.isEmpty()) {
            return List.of();
        }
        List<VisitRecordEntity> visits = visitRecordMapper.selectList(
                new LambdaQueryWrapper<VisitRecordEntity>()
                        .in(VisitRecordEntity::getId, visitIds)
                        .orderByDesc(VisitRecordEntity::getVisitTime)
                        .orderByDesc(VisitRecordEntity::getId));
        return buildVisitDetails(visits);
    }

    @Transactional
    public HospitalModels.VisitDetail createVisit(HospitalModels.CreateVisitCommand command) {
        ensurePatientExists(command.patientId());

        VisitRecordEntity visit = new VisitRecordEntity();
        visit.setPatientId(command.patientId());
        visit.setVisitNo(generateCode("V"));
        visit.setDoctorName(command.doctorName());
        visit.setDepartment(command.department());
        visit.setChiefComplaint(command.chiefComplaint());
        visit.setVisitTime(command.visitTime());
        visit.setNotes(command.notes());
        visit.setStatus("COMPLETED");
        visitRecordMapper.insert(visit);

        applyVisitDetails(visit.getId(), command);
        return buildVisitDetails(List.of(visit)).get(0);
    }

    @Transactional
    public HospitalModels.VisitDetail updateVisit(Long visitId, HospitalModels.CreateVisitCommand command, String operatorName) {
        VisitRecordEntity existing = visitRecordMapper.selectById(visitId);
        if (existing == null) {
            throw new BusinessException("就诊记录不存在");
        }

        ensurePatientExists(command.patientId());
        rollbackVisitPrescriptions(visitId, operatorName, "编辑就诊记录回退库存");
        diagnosisRecordMapper.delete(new LambdaQueryWrapper<DiagnosisRecordEntity>().eq(DiagnosisRecordEntity::getVisitId, visitId));
        prescriptionRecordMapper.delete(new LambdaQueryWrapper<PrescriptionRecordEntity>().eq(PrescriptionRecordEntity::getVisitId, visitId));

        existing.setPatientId(command.patientId());
        existing.setDoctorName(command.doctorName());
        existing.setDepartment(command.department());
        existing.setChiefComplaint(command.chiefComplaint());
        existing.setVisitTime(command.visitTime());
        existing.setNotes(command.notes());
        existing.setStatus("COMPLETED");
        existing.setQueueNumber(null);
        visitRecordMapper.updateById(existing);

        applyVisitDetails(visitId, command);
        return buildVisitDetails(List.of(existing)).get(0);
    }

    @Transactional
    public void deleteVisit(Long visitId, String operatorName) {
        VisitRecordEntity existing = visitRecordMapper.selectById(visitId);
        if (existing == null) {
            throw new BusinessException("就诊记录不存在");
        }

        rollbackVisitPrescriptions(visitId, operatorName, "删除就诊记录回退库存");
        diagnosisRecordMapper.delete(new LambdaQueryWrapper<DiagnosisRecordEntity>().eq(DiagnosisRecordEntity::getVisitId, visitId));
        prescriptionRecordMapper.delete(new LambdaQueryWrapper<PrescriptionRecordEntity>().eq(PrescriptionRecordEntity::getVisitId, visitId));
        visitRecordMapper.deleteById(visitId);
    }

    public List<HospitalModels.Drug> listDrugs() {
        return drugInfoMapper.selectList(new LambdaQueryWrapper<DrugInfoEntity>()
                        .orderByDesc(DrugInfoEntity::getCreatedAt))
                .stream()
                .map(this::toDrug)
                .toList();
    }

    public HospitalModels.Drug findDrugById(Long drugId) {
        DrugInfoEntity entity = drugInfoMapper.selectById(drugId);
        return entity != null ? toDrug(entity) : null;
    }

    public Map<String, Object> searchDrugs(String keyword, int page, int size, String drugType) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        LambdaQueryWrapper<DrugInfoEntity> wrapper = new LambdaQueryWrapper<DrugInfoEntity>()
                .orderByDesc(DrugInfoEntity::getCreatedAt);
        if (drugType != null && !drugType.isBlank()) {
            wrapper.eq(DrugInfoEntity::getDrugType, drugType);
        }
        if (!q.isEmpty()) {
            wrapper.and(w -> w
                    .like(DrugInfoEntity::getDrugName, q)
                    .or().like(DrugInfoEntity::getDrugCode, q)
                    .or().like(DrugInfoEntity::getManufacturer, q));
        }
        Page<DrugInfoEntity> pageParam = new Page<>(page, size);
        Page<DrugInfoEntity> result = drugInfoMapper.selectPage(pageParam, wrapper);
        List<HospitalModels.Drug> list = result.getRecords().stream().map(this::toDrug).toList();
        return Map.of(
                "list", list,
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize()
        );
    }

    @Transactional
    public HospitalModels.Drug createDrug(HospitalModels.CreateDrugCommand command) {
        DrugInfoEntity entity = new DrugInfoEntity();
        entity.setDrugCode(generateCode("D"));
        entity.setDrugName(command.drugName());
        entity.setSpecification(command.specification());
        entity.setManufacturer(command.manufacturer());
        entity.setUnit(command.unit());
        entity.setStock(command.initialStock());
        entity.setReservedStock(0);
        entity.setWarningStock(command.warningStock());
        entity.setUnitPrice(command.unitPrice() != null ? command.unitPrice() : BigDecimal.ZERO);
        entity.setDrugType(command.drugType() != null ? command.drugType() : "ORAL");
        drugInfoMapper.insert(entity);

        if (command.initialStock() > 0) {
            DrugInventoryLogEntity log = new DrugInventoryLogEntity();
            log.setDrugId(entity.getId());
            log.setChangeType("IN");
            log.setQuantity(command.initialStock());
            log.setBeforeStock(0);
            log.setAfterStock(command.initialStock());
            log.setOperatorName("系统初始化");
            log.setRemark("新建药品");
            drugInventoryLogMapper.insert(log);
        }

        return toDrug(entity);
    }

    @Transactional
    public void updateDrugWarningStock(Long drugId, int warningStock) {
        DrugInfoEntity drug = drugInfoMapper.selectById(drugId);
        if (drug == null) {
            throw new BusinessException("药品不存在");
        }
        drug.setWarningStock(warningStock);
        drugInfoMapper.updateById(drug);
    }

    @Transactional
    public void deleteDrug(Long drugId) {
        DrugInfoEntity drug = drugInfoMapper.selectById(drugId);
        if (drug == null) {
            throw new BusinessException("药品不存在");
        }
        long prescriptionCount = prescriptionRecordMapper.selectCount(
                new LambdaQueryWrapper<PrescriptionRecordEntity>().eq(PrescriptionRecordEntity::getDrugId, drugId));
        if (prescriptionCount > 0) {
            throw new BusinessException("该药品已被处方使用，无法删除");
        }
        drugInventoryLogMapper.delete(new LambdaQueryWrapper<DrugInventoryLogEntity>().eq(DrugInventoryLogEntity::getDrugId, drugId));
        drugInfoMapper.deleteById(drugId);
    }

    @Transactional
    public HospitalModels.InventoryLog adjustStock(HospitalModels.AdjustStockCommand command) {
        return adjustStockInternal(command.drugId(), command.changeType(), command.quantity(), command.operatorName(), command.remark());
    }

    public List<HospitalModels.InventoryLogView> listInventoryLogs() {
        List<DrugInventoryLogEntity> logs = drugInventoryLogMapper.selectList(new LambdaQueryWrapper<DrugInventoryLogEntity>()
                .orderByDesc(DrugInventoryLogEntity::getCreatedAt)
                .orderByDesc(DrugInventoryLogEntity::getId));
        Map<Long, DrugInfoEntity> drugMap = drugInfoMapper.selectBatchIds(logs.stream().map(DrugInventoryLogEntity::getDrugId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(DrugInfoEntity::getId, item -> item));

        return logs.stream()
                .map(log -> new HospitalModels.InventoryLogView(
                        log.getId(),
                        log.getDrugId(),
                        drugMap.containsKey(log.getDrugId()) ? drugMap.get(log.getDrugId()).getDrugName() : "",
                        log.getChangeType(),
                        log.getQuantity(),
                        log.getBeforeStock(),
                        log.getAfterStock(),
                        log.getOperatorName(),
                        log.getRemark(),
                        log.getCreatedAt()
                ))
                .toList();
    }

    /**
     * @param drugIdsFilter 非 null 且非空时，仅返回这些药品的流水；为 null 时不按药品过滤
     */
    public Map<String, Object> searchInventoryLogs(int page, int size, List<Long> drugIdsFilter) {
        LambdaQueryWrapper<DrugInventoryLogEntity> wrapper = new LambdaQueryWrapper<DrugInventoryLogEntity>()
                .orderByDesc(DrugInventoryLogEntity::getCreatedAt)
                .orderByDesc(DrugInventoryLogEntity::getId);
        if (drugIdsFilter != null && !drugIdsFilter.isEmpty()) {
            wrapper.in(DrugInventoryLogEntity::getDrugId, drugIdsFilter);
        }
        Page<DrugInventoryLogEntity> pageParam = new Page<>(page, size);
        Page<DrugInventoryLogEntity> result = drugInventoryLogMapper.selectPage(pageParam, wrapper);
        Map<Long, DrugInfoEntity> drugMap = drugInfoMapper.selectBatchIds(result.getRecords().stream().map(DrugInventoryLogEntity::getDrugId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(DrugInfoEntity::getId, item -> item));
        List<HospitalModels.InventoryLogView> list = result.getRecords().stream()
                .map(log -> new HospitalModels.InventoryLogView(
                        log.getId(), log.getDrugId(),
                        drugMap.containsKey(log.getDrugId()) ? drugMap.get(log.getDrugId()).getDrugName() : "",
                        log.getChangeType(), log.getQuantity(), log.getBeforeStock(), log.getAfterStock(),
                        log.getOperatorName(), log.getRemark(), log.getCreatedAt()
                )).toList();
        return Map.of("list", list, "total", result.getTotal(), "page", result.getCurrent(), "size", result.getSize());
    }

    public List<HospitalModels.PharmacyVisitDetail> listPendingPickupVisitDetails() {
        return buildPharmacyVisitDetails(loadVisitsByPrescriptionFilter(
                new LambdaQueryWrapper<PrescriptionRecordEntity>()
                        .eq(PrescriptionRecordEntity::getPickupStatus, PICKUP_PENDING)
        ));
    }

    public List<HospitalModels.PharmacyVisitDetail> listPendingInjectionVisitDetails(String operatorUsername) {
        HospitalModels.UserAccount operator = getUserByUsername(operatorUsername);
        LambdaQueryWrapper<PrescriptionRecordEntity> wrapper = new LambdaQueryWrapper<PrescriptionRecordEntity>()
                .eq(PrescriptionRecordEntity::getDrugType, "INJECTION")
                .eq(PrescriptionRecordEntity::getInjectionStatus, INJECTION_PENDING);
        if (!operator.roles().contains("ADMIN")) {
            wrapper.eq(PrescriptionRecordEntity::getPaymentStatus, PAYMENT_PAID)
                    .eq(PrescriptionRecordEntity::getInjectionAssigneeUsername, operator.username());
        }
        return buildPharmacyVisitDetails(loadVisitsByPrescriptionFilter(wrapper));
    }

    public HospitalModels.PharmacyVisitDetail getPharmacyVisitDetail(Long visitId) {
        VisitRecordEntity visit = visitRecordMapper.selectById(visitId);
        if (visit == null) {
            throw new BusinessException("就诊记录不存在");
        }
        return buildPharmacyVisitDetails(List.of(visit)).get(0);
    }

    @Transactional
    public HospitalModels.PharmacyVisitDetail confirmPayment(Long visitId, String operatorName) {
        lockVisitRecord(visitId);
        List<PrescriptionRecordEntity> prescriptions = listPrescriptionsByVisitId(visitId);
        if (prescriptions.isEmpty()) {
            throw new BusinessException("当前就诊没有处方记录");
        }
        if (prescriptions.stream().allMatch(item -> PAYMENT_PAID.equals(item.getPaymentStatus()))) {
            return getPharmacyVisitDetail(visitId);
        }
        LocalDateTime now = LocalDateTime.now();
        for (PrescriptionRecordEntity prescription : prescriptions) {
            if (!PAYMENT_PAID.equals(prescription.getPaymentStatus())) {
                prescription.setPaymentStatus(PAYMENT_PAID);
                prescription.setPaidAt(now);
                if ("INJECTION".equals(prescription.getDrugType())) {
                    prescription.setInjectionAssigneeUsername(operatorName);
                }
                prescriptionRecordMapper.updateById(prescription);
            }
        }
        return getPharmacyVisitDetail(visitId);
    }

    @Transactional
    public HospitalModels.PharmacyVisitDetail confirmPickup(Long visitId, String operatorName) {
        lockVisitRecord(visitId);
        List<PrescriptionRecordEntity> prescriptions = listPrescriptionsByVisitId(visitId);
        if (prescriptions.isEmpty()) {
            throw new BusinessException("当前就诊没有处方记录");
        }
        boolean hasUnpaid = prescriptions.stream().anyMatch(item -> !PAYMENT_PAID.equals(item.getPaymentStatus()));
        if (hasUnpaid) {
            throw new BusinessException("请先确认收款");
        }
        if (prescriptions.stream().allMatch(item -> PICKUP_DISPENSED.equals(item.getPickupStatus()))) {
            throw new BusinessException(409, "该取药事务已被其他药师处理，请刷新页面");
        }
        LocalDateTime now = LocalDateTime.now();
        for (PrescriptionRecordEntity prescription : prescriptions) {
            if (!PICKUP_DISPENSED.equals(prescription.getPickupStatus())) {
                finalizeReservedStockOutInternal(prescription.getDrugId(), prescription.getQuantity(), operatorName, "药房确认取药");
                prescription.setPickupStatus(PICKUP_DISPENSED);
                prescription.setPickedUpAt(now);
                prescriptionRecordMapper.updateById(prescription);
            }
        }
        return getPharmacyVisitDetail(visitId);
    }

    @Transactional
    public HospitalModels.PharmacyVisitDetail completeInjection(Long visitId, String operatorName) {
        lockVisitRecord(visitId);
        HospitalModels.UserAccount operator = getUserByUsername(operatorName);
        List<PrescriptionRecordEntity> injectionPrescriptions = prescriptionRecordMapper.selectList(
                new LambdaQueryWrapper<PrescriptionRecordEntity>()
                        .eq(PrescriptionRecordEntity::getVisitId, visitId)
                        .eq(PrescriptionRecordEntity::getDrugType, "INJECTION")
                        .orderByAsc(PrescriptionRecordEntity::getId)
        );
        if (injectionPrescriptions.isEmpty()) {
            throw new BusinessException("当前就诊没有注射类药物");
        }
        boolean hasUndispensed = injectionPrescriptions.stream().anyMatch(item -> !PICKUP_DISPENSED.equals(item.getPickupStatus()));
        if (hasUndispensed) {
            throw new BusinessException("请先完成取药");
        }
        if (!operator.roles().contains("ADMIN")) {
            boolean assignedToOtherPharmacist = injectionPrescriptions.stream()
                    .anyMatch(item -> !operator.username().equals(item.getInjectionAssigneeUsername()));
            if (assignedToOtherPharmacist) {
                throw new BusinessException(403, "该注射任务已分配给其他药师处理");
            }
        }
        if (injectionPrescriptions.stream().allMatch(item -> INJECTION_COMPLETED.equals(item.getInjectionStatus()))) {
            throw new BusinessException(409, "该注射事务已被其他药师处理，请刷新页面");
        }
        LocalDateTime now = LocalDateTime.now();
        for (PrescriptionRecordEntity prescription : injectionPrescriptions) {
            if (!INJECTION_COMPLETED.equals(prescription.getInjectionStatus())) {
                prescription.setInjectionStatus(INJECTION_COMPLETED);
                prescription.setInjectionCompletedAt(now);
                prescriptionRecordMapper.updateById(prescription);
            }
        }
        return getPharmacyVisitDetail(visitId);
    }

    public Map<String, Object> search(String keyword) {
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<HospitalModels.Patient> matchedPatients = listPatients().stream()
                .filter(patient -> contains(patient.name(), normalized)
                        || contains(patient.patientNo(), normalized)
                        || contains(patient.phone(), normalized))
                .toList();
        List<HospitalModels.VisitDetail> matchedVisits = listVisits(null).stream()
                .filter(visit -> contains(visit.visit().chiefComplaint(), normalized)
                        || contains(visit.visit().department(), normalized)
                        || contains(visit.visit().doctorName(), normalized))
                .toList();
        List<HospitalModels.Drug> matchedDrugs = listDrugs().stream()
                .filter(drug -> contains(drug.drugName(), normalized)
                        || contains(drug.drugCode(), normalized)
                        || contains(drug.manufacturer(), normalized))
                .toList();

        return Map.of(
                "keyword", keyword,
                "patients", matchedPatients,
                "visits", matchedVisits,
                "drugs", matchedDrugs
        );
    }

    public Map<String, Object> buildTimeline(Long patientIdValue) {
        PatientEntity patient = patientMapper.selectById(patientIdValue);
        if (patient == null) {
            throw new BusinessException("患者不存在");
        }

        List<VisitRecordEntity> patientVisits = visitRecordMapper.selectList(new LambdaQueryWrapper<VisitRecordEntity>()
                .eq(VisitRecordEntity::getPatientId, patientIdValue)
                .orderByAsc(VisitRecordEntity::getVisitTime)
                .orderByAsc(VisitRecordEntity::getId));
        List<Long> visitIds = patientVisits.stream().map(VisitRecordEntity::getId).toList();

        Map<Long, List<DiagnosisRecordEntity>> diagnosisMap = diagnosisRecordMapper.selectList(new LambdaQueryWrapper<DiagnosisRecordEntity>()
                        .in(!visitIds.isEmpty(), DiagnosisRecordEntity::getVisitId, visitIds))
                .stream()
                .collect(Collectors.groupingBy(DiagnosisRecordEntity::getVisitId));
        Map<Long, List<PrescriptionRecordEntity>> prescriptionMap = prescriptionRecordMapper.selectList(new LambdaQueryWrapper<PrescriptionRecordEntity>()
                        .in(!visitIds.isEmpty(), PrescriptionRecordEntity::getVisitId, visitIds))
                .stream()
                .collect(Collectors.groupingBy(PrescriptionRecordEntity::getVisitId));

        List<Map<String, Object>> events = new ArrayList<>();
        List<Map<String, Object>> medicationSummary = new ArrayList<>();
        for (VisitRecordEntity visit : patientVisits) {
            events.add(Map.of(
                    "type", "VISIT",
                    "time", visit.getVisitTime(),
                    "title", visit.getDepartment() + " 就诊",
                    "description", visit.getChiefComplaint(),
                    "visitId", visit.getId()
            ));

            for (DiagnosisRecordEntity diagnosis : diagnosisMap.getOrDefault(visit.getId(), List.of())) {
                events.add(Map.of(
                        "type", "DIAGNOSIS",
                        "time", visit.getVisitTime().plusMinutes(10),
                        "title", diagnosis.getDiagnosisName(),
                        "description", diagnosis.getDescription() == null ? "" : diagnosis.getDescription(),
                        "visitId", visit.getId()
                ));
            }

            for (PrescriptionRecordEntity prescription : prescriptionMap.getOrDefault(visit.getId(), List.of())) {
                events.add(Map.of(
                        "type", "PRESCRIPTION",
                        "time", visit.getVisitTime().plusMinutes(20),
                        "title", prescription.getDrugName(),
                        "description", safe(prescription.getFrequency()) + " / " + prescription.getDays() + " 天",
                        "visitId", visit.getId()
                ));
                medicationSummary.add(Map.of(
                        "drugName", prescription.getDrugName(),
                        "dosage", prescription.getDosage(),
                        "frequency", prescription.getFrequency(),
                        "days", prescription.getDays(),
                        "quantity", prescription.getQuantity(),
                        "visitTime", visit.getVisitTime()
                ));
            }
        }

        List<Map<String, Object>> sortedEvents = events.stream()
                .sorted(Comparator.comparing(event -> (LocalDateTime) event.get("time")))
                .toList();
        List<Map<String, Object>> sortedMedications = medicationSummary.stream()
                .sorted(Comparator.comparing(item -> (LocalDateTime) item.get("visitTime"), Comparator.reverseOrder()))
                .toList();

        return Map.of(
                "patient", toPatient(patient),
                "events", sortedEvents,
                "medications", sortedMedications
        );
    }

    private void ensurePatientExists(Long patientId) {
        if (patientMapper.selectById(patientId) == null) {
            throw new BusinessException("患者不存在");
        }
    }

    private void rollbackVisitPrescriptions(Long visitId, String operatorName, String remark) {
        List<PrescriptionRecordEntity> existingPrescriptions = prescriptionRecordMapper.selectList(
                new LambdaQueryWrapper<PrescriptionRecordEntity>().eq(PrescriptionRecordEntity::getVisitId, visitId)
        );
        for (PrescriptionRecordEntity prescription : existingPrescriptions) {
            if (PICKUP_DISPENSED.equals(prescription.getPickupStatus())) {
                adjustStockInternal(prescription.getDrugId(), "IN", prescription.getQuantity(), operatorName, remark);
            } else {
                releaseReservedStockInternal(prescription.getDrugId(), prescription.getQuantity());
            }
        }
    }

    private void applyVisitDetails(Long visitId, HospitalModels.CreateVisitCommand command) {
        for (String diagnosisName : normalizeDiagnoses(command.diagnoses())) {
            DiagnosisRecordEntity diagnosis = new DiagnosisRecordEntity();
            diagnosis.setVisitId(visitId);
            diagnosis.setDiagnosisName(diagnosisName);
            diagnosis.setDiagnosisType("PRIMARY");
            diagnosis.setDescription("");
            diagnosisRecordMapper.insert(diagnosis);
        }

        for (HospitalModels.PrescriptionItemCommand item : normalizePrescriptions(command.prescriptions())) {
            DrugInfoEntity drug = drugInfoMapper.selectByIdForUpdate(item.drugId());
            if (drug == null) {
                throw new BusinessException("药品不存在");
            }
            if (item.quantity() <= 0) {
                throw new BusinessException("开药数量必须大于 0");
            }
            int availableStock = availableStock(drug);
            if (item.quantity() > availableStock) {
                throw new BusinessException(drug.getDrugName() + " 可用库存不足，当前可用库存 " + availableStock
                        + "，开药数量 " + item.quantity());
            }
            reserveStockInternal(drug, item.quantity());

            PrescriptionRecordEntity prescription = new PrescriptionRecordEntity();
            prescription.setVisitId(visitId);
            prescription.setDrugId(drug.getId());
            prescription.setDrugName(drug.getDrugName());
            prescription.setDosage(item.dosage());
            prescription.setFrequency(item.frequency());
            prescription.setDays(item.days());
            prescription.setQuantity(item.quantity());
            prescription.setUnitPrice(drug.getUnitPrice() != null ? drug.getUnitPrice() : BigDecimal.ZERO);
            prescription.setLineAmount((drug.getUnitPrice() != null ? drug.getUnitPrice() : BigDecimal.ZERO)
                    .multiply(BigDecimal.valueOf(item.quantity())));
            prescription.setDrugType(drug.getDrugType() != null ? drug.getDrugType() : "ORAL");
            prescription.setPaymentStatus(PAYMENT_UNPAID);
            prescription.setPickupStatus(PICKUP_PENDING);
            prescription.setInjectionStatus("INJECTION".equals(drug.getDrugType()) ? INJECTION_PENDING : INJECTION_NOT_REQUIRED);
            prescriptionRecordMapper.insert(prescription);
        }
    }

    private HospitalModels.InventoryLog adjustStockInternal(Long drugId,
                                                            String changeType,
                                                            int quantity,
                                                            String operatorName,
                                                            String remark) {
        if (quantity <= 0) {
            throw new BusinessException("库存数量必须大于 0");
        }

        DrugInfoEntity drug = drugInfoMapper.selectByIdForUpdate(drugId);
        if (drug == null) {
            throw new BusinessException("药品不存在");
        }

        int before = safeStock(drug);
        int reserved = safeReservedStock(drug);
        int delta = "OUT".equalsIgnoreCase(changeType) ? -quantity : quantity;
        int after = before + delta;
        if (after < 0) {
            throw new BusinessException("库存不足");
        }
        if ("OUT".equalsIgnoreCase(changeType) && after < reserved) {
            throw new BusinessException("存在已预占库存，可出库数量不足，当前实际库存 " + before + "，已预占 " + reserved
                    + "，最多可手工出库 " + Math.max(before - reserved, 0));
        }

        drug.setStock(after);
        drugInfoMapper.updateById(drug);

        DrugInventoryLogEntity log = new DrugInventoryLogEntity();
        log.setDrugId(drugId);
        log.setChangeType(changeType.toUpperCase(Locale.ROOT));
        log.setQuantity(quantity);
        log.setBeforeStock(before);
        log.setAfterStock(after);
        log.setOperatorName(operatorName);
        log.setRemark(remark);
        drugInventoryLogMapper.insert(log);

        return new HospitalModels.InventoryLog(
                log.getId(),
                log.getDrugId(),
                log.getChangeType(),
                log.getQuantity(),
                log.getBeforeStock(),
                log.getAfterStock(),
                log.getOperatorName(),
                log.getRemark(),
                log.getCreatedAt()
        );
    }

    private List<HospitalModels.VisitDetail> buildVisitDetails(List<VisitRecordEntity> visitEntities) {
        if (visitEntities.isEmpty()) {
            return List.of();
        }

        List<Long> patientIds = visitEntities.stream().map(VisitRecordEntity::getPatientId).distinct().toList();
        List<Long> visitIds = visitEntities.stream().map(VisitRecordEntity::getId).toList();

        Map<Long, HospitalModels.Patient> patientMap = patientMapper.selectBatchIds(patientIds).stream()
                .map(this::toPatient)
                .collect(Collectors.toMap(HospitalModels.Patient::id, item -> item));
        Map<Long, List<HospitalModels.DiagnosisRecord>> diagnosisMap = diagnosisRecordMapper.selectList(
                        new LambdaQueryWrapper<DiagnosisRecordEntity>()
                                .in(DiagnosisRecordEntity::getVisitId, visitIds)
                                .orderByAsc(DiagnosisRecordEntity::getId))
                .stream()
                .collect(Collectors.groupingBy(
                        DiagnosisRecordEntity::getVisitId,
                        LinkedHashMap::new,
                        Collectors.mapping(this::toDiagnosisRecord, Collectors.toList())
                ));
        Map<Long, List<HospitalModels.PrescriptionRecord>> prescriptionMap = prescriptionRecordMapper.selectList(
                        new LambdaQueryWrapper<PrescriptionRecordEntity>()
                                .in(PrescriptionRecordEntity::getVisitId, visitIds)
                                .orderByAsc(PrescriptionRecordEntity::getId))
                .stream()
                .collect(Collectors.groupingBy(
                        PrescriptionRecordEntity::getVisitId,
                        LinkedHashMap::new,
                        Collectors.mapping(this::toPrescriptionRecord, Collectors.toList())
                ));

        return visitEntities.stream()
                .map(visit -> new HospitalModels.VisitDetail(
                        toVisitRecord(visit),
                        patientMap.get(visit.getPatientId()),
                        diagnosisMap.getOrDefault(visit.getId(), List.of()),
                        prescriptionMap.getOrDefault(visit.getId(), List.of())
                ))
                .toList();
    }

    private List<VisitRecordEntity> loadVisitsByPrescriptionFilter(LambdaQueryWrapper<PrescriptionRecordEntity> wrapper) {
        List<Long> visitIds = prescriptionRecordMapper.selectList(wrapper)
                .stream()
                .map(PrescriptionRecordEntity::getVisitId)
                .distinct()
                .toList();
        if (visitIds.isEmpty()) {
            return List.of();
        }
        return visitRecordMapper.selectList(new LambdaQueryWrapper<VisitRecordEntity>()
                .in(VisitRecordEntity::getId, visitIds)
                .orderByDesc(VisitRecordEntity::getVisitTime)
                .orderByDesc(VisitRecordEntity::getId));
    }

    private List<HospitalModels.PharmacyVisitDetail> buildPharmacyVisitDetails(List<VisitRecordEntity> visitEntities) {
        return buildVisitDetails(visitEntities).stream()
                .map(this::toPharmacyVisitDetail)
                .toList();
    }

    private HospitalModels.PharmacyVisitDetail toPharmacyVisitDetail(HospitalModels.VisitDetail detail) {
        List<HospitalModels.PrescriptionRecord> prescriptions = detail.prescriptions();
        BigDecimal totalAmount = prescriptions.stream()
                .map(item -> item.lineAmount() != null ? item.lineAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        boolean hasInjection = prescriptions.stream().anyMatch(item -> "INJECTION".equals(item.drugType()));
        String paymentStatus = prescriptions.stream().allMatch(item -> PAYMENT_PAID.equals(item.paymentStatus()))
                ? PAYMENT_PAID : PAYMENT_UNPAID;
        String pickupStatus = prescriptions.stream().allMatch(item -> PICKUP_DISPENSED.equals(item.pickupStatus()))
                ? PICKUP_DISPENSED : PICKUP_PENDING;
        String injectionStatus;
        if (!hasInjection) {
            injectionStatus = INJECTION_NOT_REQUIRED;
        } else if (prescriptions.stream()
                .filter(item -> "INJECTION".equals(item.drugType()))
                .allMatch(item -> INJECTION_COMPLETED.equals(item.injectionStatus()))) {
            injectionStatus = INJECTION_COMPLETED;
        } else {
            injectionStatus = INJECTION_PENDING;
        }
        return new HospitalModels.PharmacyVisitDetail(
                detail.visit(),
                detail.patient(),
                detail.diagnoses(),
                prescriptions,
                totalAmount,
                paymentStatus,
                pickupStatus,
                injectionStatus,
                hasInjection
        );
    }

    private Map<Long, List<String>> listRoleCodesByUserIds(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        List<SysUserRoleEntity> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRoleEntity>()
                .in(SysUserRoleEntity::getUserId, userIds));
        if (userRoles.isEmpty()) {
            return Map.of();
        }

        Map<Long, String> roleCodeMap = sysRoleMapper.selectBatchIds(
                        userRoles.stream().map(SysUserRoleEntity::getRoleId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(SysRoleEntity::getId, SysRoleEntity::getRoleCode));

        Map<Long, List<String>> result = new HashMap<>();
        for (SysUserRoleEntity userRole : userRoles) {
            String roleCode = roleCodeMap.get(userRole.getRoleId());
            if (roleCode == null) {
                continue;
            }
            result.computeIfAbsent(userRole.getUserId(), key -> new ArrayList<>()).add(roleCode);
        }
        return result;
    }

    private HospitalModels.UserAccount getUserByUsername(String username) {
        SysUserEntity user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUsername, username)
                .last("limit 1"));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        List<String> roles = listRoleCodesByUserIds(List.of(user.getId())).getOrDefault(user.getId(), List.of());
        return new HospitalModels.UserAccount(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getPhone(),
                user.getIdCard(),
                user.getAvatar(),
                roles,
                user.getDepartment(),
                user.getPharmacy(),
                user.getReceptionDesk(),
                user.getPatientId()
        );
    }

    private void assertCanEditPatient(Long patientId, HospitalModels.UserAccount operator) {
        if (operator.roles().contains("ADMIN")) {
            return;
        }
        if (operator.roles().contains("RECEPTION")) {
            return;
        }
        if (!operator.roles().contains("DOCTOR")) {
            throw new BusinessException(403, "无权编辑患者信息");
        }
        String doctorName = operator.realName() != null ? operator.realName().trim() : "";
        if (doctorName.isEmpty()) {
            throw new BusinessException(403, "当前医生信息不完整，无法编辑患者");
        }
        VisitRecordEntity assignedVisit = visitRecordMapper.selectOne(new LambdaQueryWrapper<VisitRecordEntity>()
                .eq(VisitRecordEntity::getPatientId, patientId)
                .eq(VisitRecordEntity::getStatus, "PENDING")
                .eq(VisitRecordEntity::getDoctorName, doctorName)
                .orderByDesc(VisitRecordEntity::getVisitTime)
                .orderByDesc(VisitRecordEntity::getId)
                .last("limit 1"));
        if (assignedVisit == null) {
            throw new BusinessException(403, "该患者当前未分配给您，无法编辑");
        }
    }

    private VisitRecordEntity lockVisitRecord(Long visitId) {
        VisitRecordEntity visit = visitRecordMapper.selectByIdForUpdate(visitId);
        if (visit == null) {
            throw new BusinessException("就诊记录不存在");
        }
        return visit;
    }

    private List<PrescriptionRecordEntity> listPrescriptionsByVisitId(Long visitId) {
        return prescriptionRecordMapper.selectList(
                new LambdaQueryWrapper<PrescriptionRecordEntity>()
                        .eq(PrescriptionRecordEntity::getVisitId, visitId)
                        .orderByAsc(PrescriptionRecordEntity::getId)
        );
    }

    private HospitalModels.Patient toPatient(PatientEntity entity) {
        return new HospitalModels.Patient(
                entity.getId(),
                entity.getPatientNo(),
                entity.getName(),
                entity.getGender(),
                entity.getAge(),
                entity.getPhone(),
                entity.getIdCard(),
                entity.getAddress(),
                entity.getAllergyHistory(),
                entity.getMedicalHistory(),
                entity.getAvatar(),
                entity.getCreatedAt()
        );
    }

    private HospitalModels.VisitRecord toVisitRecord(VisitRecordEntity entity) {
        return new HospitalModels.VisitRecord(
                entity.getId(),
                entity.getPatientId(),
                entity.getVisitNo(),
                entity.getDoctorName(),
                entity.getDepartment(),
                entity.getChiefComplaint(),
                entity.getVisitTime(),
                entity.getNotes(),
                entity.getStatus() != null ? entity.getStatus() : "COMPLETED",
                entity.getQueueNumber()
        );
    }

    private HospitalModels.DiagnosisRecord toDiagnosisRecord(DiagnosisRecordEntity entity) {
        return new HospitalModels.DiagnosisRecord(
                entity.getId(),
                entity.getVisitId(),
                entity.getDiagnosisName(),
                entity.getDiagnosisType(),
                entity.getDescription()
        );
    }

    private HospitalModels.PrescriptionRecord toPrescriptionRecord(PrescriptionRecordEntity entity) {
        return new HospitalModels.PrescriptionRecord(
                entity.getId(),
                entity.getVisitId(),
                entity.getDrugId(),
                entity.getDrugName(),
                entity.getDosage(),
                entity.getFrequency(),
                entity.getDays(),
                entity.getQuantity(),
                entity.getUnitPrice() != null ? entity.getUnitPrice() : BigDecimal.ZERO,
                entity.getLineAmount() != null ? entity.getLineAmount() : BigDecimal.ZERO,
                entity.getDrugType() != null ? entity.getDrugType() : "ORAL",
                entity.getPaymentStatus() != null ? entity.getPaymentStatus() : PAYMENT_UNPAID,
                entity.getPickupStatus() != null ? entity.getPickupStatus() : PICKUP_PENDING,
                entity.getInjectionStatus() != null ? entity.getInjectionStatus()
                        : ("INJECTION".equals(entity.getDrugType()) ? INJECTION_PENDING : INJECTION_NOT_REQUIRED),
                entity.getPaidAt(),
                entity.getPickedUpAt(),
                entity.getInjectionCompletedAt(),
                entity.getInjectionAssigneeUsername()
        );
    }

    private HospitalModels.Drug toDrug(DrugInfoEntity entity) {
        int stock = entity.getStock() != null ? entity.getStock() : 0;
        int reservedStock = entity.getReservedStock() != null ? entity.getReservedStock() : 0;
        return new HospitalModels.Drug(
                entity.getId(),
                entity.getDrugCode(),
                entity.getDrugName(),
                entity.getSpecification(),
                entity.getManufacturer(),
                entity.getUnit(),
                stock,
                reservedStock,
                Math.max(stock - reservedStock, 0),
                entity.getWarningStock(),
                entity.getUnitPrice() != null ? entity.getUnitPrice() : BigDecimal.ZERO,
                entity.getDrugType() != null ? entity.getDrugType() : "ORAL",
                entity.getCreatedAt()
        );
    }

    private void reserveStockInternal(DrugInfoEntity drug, int quantity) {
        int stock = safeStock(drug);
        int reserved = safeReservedStock(drug);
        int available = stock - reserved;
        if (quantity > available) {
            throw new BusinessException(drug.getDrugName() + " 可用库存不足，当前可用库存 " + available
                    + "，开药数量 " + quantity);
        }
        drug.setReservedStock(reserved + quantity);
        drugInfoMapper.updateById(drug);
    }

    private void releaseReservedStockInternal(Long drugId, int quantity) {
        if (quantity <= 0) {
            return;
        }
        DrugInfoEntity drug = drugInfoMapper.selectByIdForUpdate(drugId);
        if (drug == null) {
            throw new BusinessException("药品不存在");
        }
        int reserved = safeReservedStock(drug);
        if (reserved < quantity) {
            throw new BusinessException(drug.getDrugName() + " 预占库存状态异常");
        }
        drug.setReservedStock(reserved - quantity);
        drugInfoMapper.updateById(drug);
    }

    private HospitalModels.InventoryLog finalizeReservedStockOutInternal(Long drugId,
                                                                         int quantity,
                                                                         String operatorName,
                                                                         String remark) {
        if (quantity <= 0) {
            throw new BusinessException("库存数量必须大于 0");
        }
        DrugInfoEntity drug = drugInfoMapper.selectByIdForUpdate(drugId);
        if (drug == null) {
            throw new BusinessException("药品不存在");
        }
        int before = safeStock(drug);
        int reserved = safeReservedStock(drug);
        if (reserved < quantity) {
            throw new BusinessException(drug.getDrugName() + " 预占库存不足，请刷新后重试");
        }
        if (before < quantity) {
            throw new BusinessException(drug.getDrugName() + " 实际库存不足，请先补库存");
        }
        int after = before - quantity;
        drug.setStock(after);
        drug.setReservedStock(reserved - quantity);
        drugInfoMapper.updateById(drug);

        DrugInventoryLogEntity log = new DrugInventoryLogEntity();
        log.setDrugId(drugId);
        log.setChangeType("OUT");
        log.setQuantity(quantity);
        log.setBeforeStock(before);
        log.setAfterStock(after);
        log.setOperatorName(operatorName);
        log.setRemark(remark);
        drugInventoryLogMapper.insert(log);

        return new HospitalModels.InventoryLog(
                log.getId(),
                log.getDrugId(),
                log.getChangeType(),
                log.getQuantity(),
                log.getBeforeStock(),
                log.getAfterStock(),
                log.getOperatorName(),
                log.getRemark(),
                log.getCreatedAt()
        );
    }

    private List<String> normalizeDiagnoses(List<String> diagnoses) {
        return diagnoses == null ? List.of() : diagnoses.stream().filter(Objects::nonNull).toList();
    }

    private List<HospitalModels.PrescriptionItemCommand> normalizePrescriptions(List<HospitalModels.PrescriptionItemCommand> prescriptions) {
        return prescriptions == null ? List.of() : prescriptions;
    }

    private boolean contains(String source, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        return source != null && source.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private int safeStock(DrugInfoEntity entity) {
        return entity.getStock() != null ? entity.getStock() : 0;
    }

    private int safeReservedStock(DrugInfoEntity entity) {
        return entity.getReservedStock() != null ? entity.getReservedStock() : 0;
    }

    private int availableStock(DrugInfoEntity entity) {
        return Math.max(safeStock(entity) - safeReservedStock(entity), 0);
    }

    private String generateCode(String prefix) {
        return prefix + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS", Locale.CHINA).format(LocalDateTime.now())
                + ThreadLocalRandom.current().nextInt(100, 1000);
    }

    private String generateRandomIdCard() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        return String.format("110101%04d%02d%02d%03d%01d",
                r.nextInt(1950, 2010),
                r.nextInt(1, 13),
                r.nextInt(1, 29),
                r.nextInt(0, 1000),
                r.nextInt(0, 10));
    }

    @Transactional
    public void updateUserAvatar(Long userId, String avatarPath) {
        SysUserEntity user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setAvatar(avatarPath);
        sysUserMapper.updateById(user);
    }

    @Transactional
    public void updatePatientAvatar(Long patientId, String avatarPath) {
        PatientEntity patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BusinessException("患者不存在");
        }
        patient.setAvatar(avatarPath);
        patientMapper.updateById(patient);
    }

    /**
     * 患者自助注册：创建 patient 档案 + sys_user 账号 + 绑定
     */
    @Transactional
    public HospitalModels.UserAccount registerPatient(String username, String password, String name,
                                                      String gender, int age, String phone, String idCard,
                                                      String address, String allergyHistory, String medicalHistory) {
        // 1. 检查用户名
        if (sysUserMapper.selectOne(new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUsername, username).last("limit 1")) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 创建 patient 档案
        PatientEntity patient = new PatientEntity();
        patient.setPatientNo(generateCode("P"));
        patient.setName(name);
        patient.setGender(gender);
        patient.setAge(age);
        patient.setPhone(phone);
        patient.setIdCard(idCard != null ? idCard : "");
        patient.setAddress(address != null ? address : "");
        patient.setAllergyHistory(allergyHistory != null ? allergyHistory : "");
        patient.setMedicalHistory(medicalHistory != null ? medicalHistory : "");
        patientMapper.insert(patient);

        // 3. 创建 sys_user 并绑定 patient_id
        SysUserEntity user = new SysUserEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(name);
        user.setPhone(phone);
        user.setIdCard(idCard != null ? idCard : generateRandomIdCard());
        user.setPatientId(patient.getId());
        user.setStatus(1);
        sysUserMapper.insert(user);

        // 4. 分配 PATIENT 角色
        SysRoleEntity role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRoleEntity>()
                .eq(SysRoleEntity::getRoleCode, "PATIENT").last("limit 1"));
        if (role != null) {
            SysUserRoleEntity ur = new SysUserRoleEntity();
            ur.setUserId(user.getId());
            ur.setRoleId(role.getId());
            sysUserRoleMapper.insert(ur);
        }

        List<String> roles = List.of("PATIENT");
        return new HospitalModels.UserAccount(user.getId(), user.getUsername(), user.getPassword(),
                user.getRealName(), user.getPhone(), user.getIdCard(), user.getAvatar(), roles,
                user.getDepartment(), user.getPharmacy(), user.getReceptionDesk(), patient.getId());
    }

    /**
     * 患者端：获取个人信息（包含 patient 档案详情）
     */
    public Map<String, Object> getPatientCenterProfile(String username) {
        HospitalModels.UserAccount user = getUserByUsername(username);
        if (user.patientId() == null) {
            throw new BusinessException("当前账号未绑定患者档案");
        }
        PatientEntity patient = patientMapper.selectById(user.patientId());
        if (patient == null) {
            throw new BusinessException("患者档案不存在");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", user.id());
        result.put("username", user.username());
        result.put("avatar", user.avatar() != null ? user.avatar() : "");
        result.put("patientId", patient.getId());
        result.put("patientNo", patient.getPatientNo());
        result.put("name", patient.getName());
        result.put("gender", patient.getGender());
        result.put("age", patient.getAge());
        result.put("phone", patient.getPhone() != null ? patient.getPhone() : "");
        result.put("idCard", patient.getIdCard() != null ? patient.getIdCard() : "");
        result.put("address", patient.getAddress() != null ? patient.getAddress() : "");
        result.put("allergyHistory", patient.getAllergyHistory() != null ? patient.getAllergyHistory() : "");
        result.put("medicalHistory", patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "");
        return result;
    }

    /**
     * 患者端：更新个人信息（只允许改部分字段）
     */
    @Transactional
    public void updatePatientCenterProfile(String username, String phone, String address,
                                           String allergyHistory, String medicalHistory) {
        HospitalModels.UserAccount user = getUserByUsername(username);
        if (user.patientId() == null) {
            throw new BusinessException("当前账号未绑定患者档案");
        }
        PatientEntity patient = patientMapper.selectById(user.patientId());
        if (patient == null) {
            throw new BusinessException("患者档案不存在");
        }
        if (phone != null) patient.setPhone(phone);
        if (address != null) patient.setAddress(address);
        if (allergyHistory != null) patient.setAllergyHistory(allergyHistory);
        if (medicalHistory != null) patient.setMedicalHistory(medicalHistory);
        patientMapper.updateById(patient);

        // 同步更新 sys_user 的 phone
        SysUserEntity sysUser = sysUserMapper.selectById(user.id());
        if (sysUser != null && phone != null) {
            sysUser.setPhone(phone);
            sysUserMapper.updateById(sysUser);
        }
    }

    /**
     * 患者端：自助挂号
     */
    @Transactional
    public Map<String, Object> patientRegister(String username, String department, String doctorName, String appointmentDate, String timeSlot) {
        HospitalModels.UserAccount user = getUserByUsername(username);
        if (user.patientId() == null) {
            throw new BusinessException("当前账号未绑定患者档案");
        }

        // 检查是否重复挂号（同一医生同一天同一时段）
        // 注意：patient_registration 表通过 JdbcTemplate 操作，这里简化处理

        // 调用原有的挂号逻辑
        HospitalModels.VisitDetail visit = createRegistration(user.patientId(), doctorName);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("visitNo", visit.visit().visitNo());
        result.put("doctorName", visit.visit().doctorName());
        result.put("department", visit.visit().department());
        result.put("queueNumber", visit.visit().queueNumber());
        result.put("status", visit.visit().status());
        result.put("appointmentDate", appointmentDate);
        result.put("timeSlot", timeSlot);
        return result;
    }

    @Transactional
    public void cancelRegistration(String username, Long visitId) {
        HospitalModels.UserAccount user = getUserByUsername(username);
        if (user.patientId() == null) {
            throw new BusinessException("当前账号未绑定患者");
        }
        VisitRecordEntity visit = visitRecordMapper.selectById(visitId);
        if (visit == null) {
            throw new BusinessException("挂号记录不存在");
        }
        if (!visit.getPatientId().equals(user.patientId())) {
            throw new BusinessException("只能取消自己的挂号");
        }
        if (!"PENDING".equals(visit.getStatus())) {
            throw new BusinessException("只能取消待就诊的挂号");
        }
        visit.setStatus("CANCELLED");
        visit.setChiefComplaint("已退号");
        visitRecordMapper.updateById(visit);
    }

    /**
     * 前台/管理员取消挂号
     */
    @Transactional
    public void receptionCancelRegistration(Long visitId) {
        VisitRecordEntity visit = visitRecordMapper.selectById(visitId);
        if (visit == null) {
            throw new BusinessException("挂号记录不存在");
        }
        if (!"PENDING".equals(visit.getStatus())) {
            throw new BusinessException("只能取消待就诊的挂号");
        }
        visit.setStatus("CANCELLED");
        visit.setChiefComplaint("已退号");
        visitRecordMapper.updateById(visit);
    }

    /**
     * 前台工作台数据
     */
    public Map<String, Object> getReceptionDashboard() {
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = todayStart.plusDays(1);

        // 今日所有就诊记录
        List<VisitRecordEntity> todayVisits = visitRecordMapper.selectList(new LambdaQueryWrapper<VisitRecordEntity>()
                .ge(VisitRecordEntity::getVisitTime, todayStart)
                .lt(VisitRecordEntity::getVisitTime, todayEnd));

        long totalCount = todayVisits.size();
        long pendingCount = todayVisits.stream().filter(v -> "PENDING".equals(v.getStatus())).count();
        long completedCount = todayVisits.stream().filter(v -> "COMPLETED".equals(v.getStatus())).count();

        // 候诊队列（PENDING 状态，按排队号排序）
        List<VisitRecordEntity> pendingVisits = todayVisits.stream()
                .filter(v -> "PENDING".equals(v.getStatus()))
                .sorted(Comparator.comparing(VisitRecordEntity::getQueueNumber, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        List<HospitalModels.VisitDetail> queue = buildVisitDetails(pendingVisits);

        // 各科室排队情况（按科室分组统计）
        Map<String, Map<String, Long>> departmentStats = todayVisits.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getDepartment() != null ? v.getDepartment() : "未知",
                        Collectors.collectingAndThen(Collectors.toList(), deptVisits -> {
                            Map<String, Long> stats = new LinkedHashMap<>();
                            stats.put("total", (long) deptVisits.size());
                            stats.put("pending", deptVisits.stream().filter(v -> "PENDING".equals(v.getStatus())).count());
                            stats.put("completed", deptVisits.stream().filter(v -> "COMPLETED".equals(v.getStatus())).count());
                            return stats;
                        })
                ));

        // 今日患者数（去重）
        long todayPatientCount = todayVisits.stream().map(VisitRecordEntity::getPatientId).distinct().count();

        // 最近挂号记录（今日就诊，按时间倒序，最多10条）
        List<VisitRecordEntity> recentVisits = todayVisits.stream()
                .sorted(Comparator.comparing(VisitRecordEntity::getVisitTime, Comparator.reverseOrder()))
                .limit(10)
                .toList();
        List<HospitalModels.VisitDetail> recentRegistrations = buildVisitDetails(recentVisits);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayTotal", totalCount);
        result.put("todayPending", pendingCount);
        result.put("todayCompleted", completedCount);
        result.put("todayPatients", todayPatientCount);
        result.put("queue", queue);
        result.put("departmentStats", departmentStats);
        result.put("recentRegistrations", recentRegistrations);
        return result;
    }
}
