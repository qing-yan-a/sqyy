package com.sqyy.hospital.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class HospitalModels {

    private HospitalModels() {
    }

    public record UserAccount(Long id, String username, String password, String realName, String phone, String idCard, String avatar, List<String> roles, String department, String pharmacy, String receptionDesk, Long patientId) {
    }

    public record Patient(Long id, String patientNo, String name, String gender, int age, String phone,
                          String idCard, String address, String allergyHistory, String medicalHistory,
                          String avatar, LocalDateTime createdAt) {
    }

    public record Drug(Long id, String drugCode, String drugName, String specification, String manufacturer,
                       String unit, int stock, int reservedStock, int availableStock, int warningStock,
                       BigDecimal unitPrice, String drugType, LocalDateTime createdAt) {
    }

    public record VisitRecord(Long id, Long patientId, String visitNo, String doctorName, String department,
                              String chiefComplaint, LocalDateTime visitTime, String notes, String status, Integer queueNumber) {
    }

    public record DiagnosisRecord(Long id, Long visitId, String diagnosisName, String diagnosisType, String description) {
    }

    public record PrescriptionRecord(Long id, Long visitId, Long drugId, String drugName, String dosage,
                                     String frequency, int days, int quantity, BigDecimal unitPrice,
                                     BigDecimal lineAmount, String drugType, String paymentStatus,
                                     String pickupStatus, String injectionStatus, LocalDateTime paidAt,
                                     LocalDateTime pickedUpAt, LocalDateTime injectionCompletedAt,
                                     String injectionAssigneeUsername) {
    }

    public record InventoryLog(Long id, Long drugId, String changeType, int quantity, int beforeStock,
                               int afterStock, String operatorName, String remark, LocalDateTime createdAt) {
    }

    public record InventoryLogView(Long id, Long drugId, String drugName, String changeType, int quantity,
                                   int beforeStock, int afterStock, String operatorName, String remark,
                                   LocalDateTime createdAt) {
    }

    public record VisitDetail(VisitRecord visit, Patient patient, List<DiagnosisRecord> diagnoses,
                              List<PrescriptionRecord> prescriptions) {
    }

    public record PharmacyVisitDetail(VisitRecord visit, Patient patient, List<DiagnosisRecord> diagnoses,
                                      List<PrescriptionRecord> prescriptions, BigDecimal totalAmount,
                                      String paymentStatus, String pickupStatus, String injectionStatus,
                                      boolean hasInjection) {
    }

    public record CreatePatientCommand(String name, String gender, int age, String phone, String idCard,
                                       String address, String allergyHistory, String medicalHistory) {
    }

    public record PrescriptionItemCommand(Long drugId, String dosage, String frequency, int days, int quantity) {
    }

    public record CreateVisitCommand(Long patientId, String doctorName, String department, String chiefComplaint,
                                     LocalDateTime visitTime, String notes, List<String> diagnoses,
                                     List<PrescriptionItemCommand> prescriptions) {
    }

    public record CreateDrugCommand(String drugName, String specification, String manufacturer, String unit,
                                    int initialStock, int warningStock, BigDecimal unitPrice, String drugType) {
        public CreateDrugCommand(String drugName, String specification, String manufacturer, String unit,
                                int initialStock, int warningStock) {
            this(drugName, specification, manufacturer, unit, initialStock, warningStock, BigDecimal.ZERO, "ORAL");
        }
    }

    public record AdjustStockCommand(Long drugId, String changeType, int quantity, String operatorName, String remark) {
    }
}
