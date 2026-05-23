package com.bank.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerVO {
    private Long id;
    private String name;
    private String idCard;
    private String phone;
    private String email;
    private String address;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
