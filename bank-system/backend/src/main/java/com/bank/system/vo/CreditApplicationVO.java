package com.bank.system.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreditApplicationVO {
    private Long id;
    private String applicationNo;
    private Long customerId;
    private String customerName;
    private BigDecimal creditAmount;
    private String creditType;
    private String purpose;
    private Integer status;
    private LocalDateTime applyTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
