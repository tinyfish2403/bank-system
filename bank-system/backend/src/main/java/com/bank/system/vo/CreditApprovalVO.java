package com.bank.system.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreditApprovalVO {
    private Long id;
    private String approvalNo;
    private Long applicationId;
    private String applicationNoVal;
    private Long customerId;
    private String customerName;
    private BigDecimal approvedAmount;
    private BigDecimal creditLimit;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;
    private LocalDateTime approvalTime;
    private String remark;
    private LocalDateTime createTime;
}
