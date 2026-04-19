package com.sqyy.hospital.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseMigrationRunner implements ApplicationRunner {

    private static final String INJECTION_LOG_REMARK = "注射药物期初入库";

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationRunner.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        migrateDrugInfoSchema();
        boolean prescriptionSchemaMigrated = migratePrescriptionSchema();
        seedKnownDrugPrices();
        seedInjectionDrugs();
        seedInjectionInventoryLogs();
        syncPrescriptionDerivedFields();
        if (prescriptionSchemaMigrated) {
            markHistoricalPrescriptionsHandled();
        }
        syncReservedStock();
        migratePatientPortalSchema();
    }

    private void migrateDrugInfoSchema() {
        if (!tableExists("drug_info")) {
            return;
        }
        if (!columnExists("drug_info", "drug_type")) {
            jdbcTemplate.execute("ALTER TABLE drug_info ADD COLUMN drug_type VARCHAR(20) NOT NULL DEFAULT 'ORAL'");
            log.info("Added column drug_info.drug_type");
        }
        if (!columnExists("drug_info", "unit_price")) {
            jdbcTemplate.execute("ALTER TABLE drug_info ADD COLUMN unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00");
            log.info("Added column drug_info.unit_price");
        }
        if (!columnExists("drug_info", "reserved_stock")) {
            jdbcTemplate.execute("ALTER TABLE drug_info ADD COLUMN reserved_stock INT NOT NULL DEFAULT 0");
            log.info("Added column drug_info.reserved_stock");
        }
        jdbcTemplate.update("UPDATE drug_info SET drug_type = 'ORAL' WHERE drug_type IS NULL OR drug_type = ''");
        jdbcTemplate.update("UPDATE drug_info SET reserved_stock = 0 WHERE reserved_stock IS NULL");
    }

    private boolean migratePrescriptionSchema() {
        if (!tableExists("prescription_record")) {
            return false;
        }
        boolean migrated = false;
        migrated |= addColumnIfMissing("prescription_record", "unit_price",
                "ALTER TABLE prescription_record ADD COLUMN unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00");
        migrated |= addColumnIfMissing("prescription_record", "line_amount",
                "ALTER TABLE prescription_record ADD COLUMN line_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00");
        migrated |= addColumnIfMissing("prescription_record", "drug_type",
                "ALTER TABLE prescription_record ADD COLUMN drug_type VARCHAR(20) NOT NULL DEFAULT 'ORAL'");
        migrated |= addColumnIfMissing("prescription_record", "payment_status",
                "ALTER TABLE prescription_record ADD COLUMN payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID'");
        migrated |= addColumnIfMissing("prescription_record", "pickup_status",
                "ALTER TABLE prescription_record ADD COLUMN pickup_status VARCHAR(20) NOT NULL DEFAULT 'PENDING'");
        migrated |= addColumnIfMissing("prescription_record", "injection_status",
                "ALTER TABLE prescription_record ADD COLUMN injection_status VARCHAR(20) NOT NULL DEFAULT 'NOT_REQUIRED'");
        migrated |= addColumnIfMissing("prescription_record", "paid_at",
                "ALTER TABLE prescription_record ADD COLUMN paid_at DATETIME NULL");
        migrated |= addColumnIfMissing("prescription_record", "picked_up_at",
                "ALTER TABLE prescription_record ADD COLUMN picked_up_at DATETIME NULL");
        migrated |= addColumnIfMissing("prescription_record", "injection_completed_at",
                "ALTER TABLE prescription_record ADD COLUMN injection_completed_at DATETIME NULL");
        migrated |= addColumnIfMissing("prescription_record", "injection_assignee_username",
                "ALTER TABLE prescription_record ADD COLUMN injection_assignee_username VARCHAR(64) NULL");
        return migrated;
    }

    private boolean addColumnIfMissing(String tableName, String columnName, String ddl) {
        if (columnExists(tableName, columnName)) {
            return false;
        }
        jdbcTemplate.execute(ddl);
        log.info("Added column {}.{}", tableName, columnName);
        return true;
    }

    private void seedKnownDrugPrices() {
        if (!tableExists("drug_info")) {
            return;
        }
        updateDrugMetadata("D2026001", "ORAL", "26.50");
        updateDrugMetadata("D2026002", "ORAL", "32.00");
        updateDrugMetadata("D2026003", "INJECTION", "48.00");
        updateDrugMetadata("D2026004", "INJECTION", "15.00");
        updateDrugMetadata("D2026005", "INJECTION", "12.00");
    }

    private void updateDrugMetadata(String drugCode, String drugType, String unitPrice) {
        jdbcTemplate.update(
                "UPDATE drug_info SET drug_type = ?, unit_price = ? WHERE drug_code = ?",
                drugType, unitPrice, drugCode
        );
    }

    private void seedInjectionDrugs() {
        if (!tableExists("drug_info")) {
            return;
        }
        jdbcTemplate.update("""
                INSERT IGNORE INTO drug_info
                (drug_code, drug_name, specification, manufacturer, unit, stock, warning_stock, unit_price, drug_type)
                VALUES
                ('D2026003', '注射用头孢曲松钠', '1.0g/支', '华北制药', '支', 50, 10, 48.00, 'INJECTION'),
                ('D2026004', '维生素C注射液', '5ml:1g/支', '石药集团', '支', 80, 15, 15.00, 'INJECTION'),
                ('D2026005', '地塞米松磷酸钠注射液', '1ml:5mg/支', '天津药业', '支', 60, 10, 12.00, 'INJECTION')
                """);
    }

    private void seedInjectionInventoryLogs() {
        if (!tableExists("drug_info") || !tableExists("drug_inventory_log")) {
            return;
        }
        seedInventoryLog("D2026003", 50);
        seedInventoryLog("D2026004", 80);
        seedInventoryLog("D2026005", 60);
    }

    private void seedInventoryLog(String drugCode, int quantity) {
        jdbcTemplate.update("""
                INSERT INTO drug_inventory_log
                (drug_id, change_type, quantity, before_stock, after_stock, operator_name, remark)
                SELECT d.id, 'IN', ?, 0, ?, '李药师', ?
                FROM drug_info d
                WHERE d.drug_code = ?
                  AND NOT EXISTS (
                    SELECT 1 FROM drug_inventory_log l
                    WHERE l.drug_id = d.id AND l.change_type = 'IN' AND l.remark = ?
                  )
                """,
                quantity, quantity, INJECTION_LOG_REMARK, drugCode, INJECTION_LOG_REMARK
        );
    }

    private void syncPrescriptionDerivedFields() {
        if (!tableExists("prescription_record") || !tableExists("drug_info")) {
            return;
        }
        jdbcTemplate.update("""
                UPDATE prescription_record p
                LEFT JOIN drug_info d ON d.id = p.drug_id
                SET p.unit_price = CASE
                        WHEN p.unit_price IS NULL OR p.unit_price = 0 THEN COALESCE(d.unit_price, 0)
                        ELSE p.unit_price
                    END,
                    p.line_amount = CASE
                        WHEN p.line_amount IS NULL OR p.line_amount = 0
                            THEN (CASE
                                WHEN p.unit_price IS NULL OR p.unit_price = 0 THEN COALESCE(d.unit_price, 0)
                                ELSE p.unit_price
                            END) * p.quantity
                        ELSE p.line_amount
                    END,
                    p.drug_type = CASE
                        WHEN p.drug_type IS NULL OR p.drug_type = '' THEN COALESCE(d.drug_type, 'ORAL')
                        ELSE p.drug_type
                    END,
                    p.injection_status = CASE
                        WHEN (p.injection_status IS NULL OR p.injection_status = '')
                             AND COALESCE(d.drug_type, p.drug_type, 'ORAL') = 'INJECTION' THEN 'PENDING'
                        WHEN p.injection_status IS NULL OR p.injection_status = '' THEN 'NOT_REQUIRED'
                        ELSE p.injection_status
                    END
                """);
    }

    private void syncReservedStock() {
        if (!tableExists("drug_info") || !tableExists("prescription_record") || !columnExists("drug_info", "reserved_stock")) {
            return;
        }
        jdbcTemplate.update("""
                UPDATE drug_info d
                LEFT JOIN (
                    SELECT drug_id, COALESCE(SUM(quantity), 0) AS reserved_quantity
                    FROM prescription_record
                    WHERE pickup_status = 'PENDING'
                    GROUP BY drug_id
                ) p ON p.drug_id = d.id
                SET d.reserved_stock = COALESCE(p.reserved_quantity, 0)
                """);
    }

    private void markHistoricalPrescriptionsHandled() {
        if (!tableExists("prescription_record") || !tableExists("drug_info")) {
            return;
        }
        jdbcTemplate.update("""
                UPDATE prescription_record p
                LEFT JOIN drug_info d ON d.id = p.drug_id
                SET p.payment_status = 'PAID',
                    p.pickup_status = 'DISPENSED',
                    p.injection_status = CASE
                        WHEN COALESCE(d.drug_type, p.drug_type, 'ORAL') = 'INJECTION' THEN 'COMPLETED'
                        ELSE 'NOT_REQUIRED'
                    END,
                    p.paid_at = COALESCE(p.paid_at, p.created_at),
                    p.picked_up_at = COALESCE(p.picked_up_at, p.created_at),
                    p.injection_completed_at = CASE
                        WHEN COALESCE(d.drug_type, p.drug_type, 'ORAL') = 'INJECTION'
                            THEN COALESCE(p.injection_completed_at, p.created_at)
                        ELSE p.injection_completed_at
                    END
                WHERE p.payment_status = 'UNPAID' AND p.pickup_status = 'PENDING'
                """);
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                Integer.class,
                tableName,
                columnName
        );
        return count != null && count > 0;
    }

    private void migratePatientPortalSchema() {
        // 1. 给 sys_user 添加 patient_id 字段
        if (tableExists("sys_user") && !columnExists("sys_user", "patient_id")) {
            jdbcTemplate.execute("ALTER TABLE sys_user ADD COLUMN patient_id BIGINT NULL");
            log.info("Added column sys_user.patient_id");
        }

        // 2. 添加 PATIENT 角色（如果不存在）
        if (tableExists("sys_role")) {
            jdbcTemplate.execute("INSERT IGNORE INTO sys_role (role_code, role_name) VALUES ('PATIENT', '患者')");
            log.info("Ensured PATIENT role exists");
        }

        // 3. 创建患者自助挂号表
        if (!tableExists("patient_registration")) {
            jdbcTemplate.execute("""
                    CREATE TABLE patient_registration (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        patient_id BIGINT NOT NULL,
                        department VARCHAR(50) NOT NULL,
                        doctor_name VARCHAR(50) NOT NULL,
                        appointment_date DATE NOT NULL,
                        time_slot VARCHAR(20) NOT NULL,
                        queue_number INT DEFAULT 0,
                        status VARCHAR(20) DEFAULT 'PENDING',
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        INDEX idx_patient_id (patient_id),
                        INDEX idx_appointment (appointment_date, department, doctor_name)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                    """);
            log.info("Created table patient_registration");
        }
    }
}
