package com.bank.system.vo;

import lombok.Data;

import java.util.List;

@Data
public class MenuVO {
    private Long id;
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String icon;
    private List<MenuVO> children;
}
