package com.bank.system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditApplicationCreateDTO {
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotNull(message = "授信金额不能为空")
    @DecimalMin(value = "0.01", message = "授信金额必须大于0")
    private BigDecimal creditAmount;

    @NotBlank(message = "授信类型不能为空")
    private String creditType;

    @Size(max = 500, message = "用途描述不能超过500字符")
    private String purpose;
}
