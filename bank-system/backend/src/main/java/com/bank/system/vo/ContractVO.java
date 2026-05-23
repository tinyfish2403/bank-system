package com.bank.system.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ContractVO {
    private Long id;
    private String contractNo;
    private Long approvalId;
    private String approvalNo;
    private Long customerId;
    private String customerName;
    private BigDecimal contractAmount;
    private String contractType;
    private LocalDate signDate;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
