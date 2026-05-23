package com.bank.system.dto;

import lombok.Data;

@Data
public class CustomerQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
}
