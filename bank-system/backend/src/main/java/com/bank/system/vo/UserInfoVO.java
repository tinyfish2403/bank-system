package com.bank.system.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoVO {
    private Long userId;
    private String username;
    private String nickname;
    private List<String> roles;
    private List<MenuVO> menus;
}
