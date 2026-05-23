package com.bank.system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractCreateDTO {
    @NotNull(message = "授信批复ID不能为空")
    private Long approvalId;

    @NotNull(message = "合同金额不能为空")
    @DecimalMin(value = "0.01", message = "合同金额必须大于0")
    private BigDecimal contractAmount;

    @NotBlank(message = "合同类型不能为空")
    private String contractType;

    @NotNull(message = "签订日期不能为空")
    private LocalDate signDate;

    @Size(max = 500, message = "备注不能超过500字符")
    private String remark;
}
