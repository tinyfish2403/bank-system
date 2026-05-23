package com.bank.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("credit_approval")
public class CreditApproval {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String approvalNo;
    private Long applicationId;
    private Long customerId;
    private BigDecimal approvedAmount;
    private BigDecimal creditLimit;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;
    private LocalDateTime approvalTime;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
