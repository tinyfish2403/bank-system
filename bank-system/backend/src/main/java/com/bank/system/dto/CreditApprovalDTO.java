package com.bank.system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreditApprovalDTO {
    @NotNull(message = "批复金额不能为空")
    @DecimalMin(value = "0.01", message = "批复金额必须大于0")
    private BigDecimal approvedAmount;

    @NotNull(message = "授信额度不能为空")
    @DecimalMin(value = "0.01", message = "授信额度必须大于0")
    private BigDecimal creditLimit;

    @NotNull(message = "有效期开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "有效期结束日期不能为空")
    private LocalDate endDate;

    @Size(max = 500, message = "批复意见不能超过500字符")
    private String remark;
}
