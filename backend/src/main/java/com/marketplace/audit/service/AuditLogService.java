package com.marketplace.audit.service;

import com.marketplace.audit.model.AuditLog;
import com.marketplace.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

/** Ghi nhật ký thao tác của Admin. Dùng chung cho các UC Admin khác (UC18, UC20, UC21). */
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(Long adminId, String action, AuditLog.TargetType targetType, Long targetId, String reason) {
        auditLogRepository.save(new AuditLog(adminId, action, targetType, targetId, reason));
    }
}
