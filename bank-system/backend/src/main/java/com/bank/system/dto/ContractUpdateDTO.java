package com.bank.system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractUpdateDTO {
    @NotNull(message = "合同金额不能为空")
    @DecimalMin(value = "0.01", message = "合同金额必须大于0")
    private BigDecimal contractAmount;

    @NotBlank(message = "合同类型不能为空")
    private String contractType;

    @Size(max = 500, message = "备注不能超过500字符")
    private String remark;
}
