package com.bank.system.controller;

import com.bank.system.common.Result;
import com.bank.system.dto.LoginDTO;
import com.bank.system.entity.SysMenu;
import com.bank.system.entity.SysRole;
import com.bank.system.entity.SysUser;
import com.bank.system.mapper.SysRoleMapper;
import com.bank.system.mapper.SysRoleMenuMapper;
import com.bank.system.mapper.SysUserMapper;
import com.bank.system.mapper.SysUserRoleMapper;
import com.bank.system.security.JwtTokenProvider;
import com.bank.system.vo.LoginVO;
import com.bank.system.vo.MenuVO;
import com.bank.system.vo.UserInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(loginDTO.getUsername());
        LoginVO loginVO = new LoginVO(token, jwtTokenProvider.getTokenHead(), jwtTokenProvider.getExpiration());
        return Result.success("登录成功", loginVO);
    }

    @GetMapping("/info")
    public Result<UserInfoVO> getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));

        UserInfoVO info = new UserInfoVO();
        info.setUserId(user.getId());
        info.setUsername(user.getUsername());
        info.setNickname(user.getNickname());

        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(user.getId());
        List<String> roleCodes = new ArrayList<>();
        if (!roleIds.isEmpty()) {
            List<SysRole> sysRoles = sysRoleMapper.selectList(
                    new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds));
            roleCodes = sysRoles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());
        }
        info.setRoles(roleCodes);

        List<SysMenu> menus = sysRoleMenuMapper.selectMenusByUserId(user.getId());
        info.setMenus(buildMenuTree(menus, 0L));

        return Result.success(info);
    }

    private List<MenuVO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<MenuVO> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                MenuVO vo = new MenuVO();
                vo.setId(menu.getId());
                vo.setParentId(menu.getParentId());
                vo.setMenuName(menu.getMenuName());
                vo.setPath(menu.getPath());
                vo.setComponent(menu.getComponent());
                vo.setIcon(menu.getIcon());
                vo.setChildren(buildMenuTree(menus, menu.getId()));
                tree.add(vo);
            }
        }
        return tree;
    }
}
