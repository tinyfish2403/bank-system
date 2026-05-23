package com.bank.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("credit_application")
public class CreditApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String applicationNo;
    private Long customerId;
    private BigDecimal creditAmount;
    private String creditType;
    private String purpose;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime applyTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
