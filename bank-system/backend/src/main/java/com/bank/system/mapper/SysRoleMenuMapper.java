package com.bank.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMenuMapper extends BaseMapper<Object> {
    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT sm.* FROM sys_menu sm INNER JOIN sys_role_menu srm ON sm.id = srm.menu_id WHERE srm.role_id IN (SELECT role_id FROM sys_user_role WHERE user_id = #{userId}) AND sm.status = 1 AND sm.deleted = 0 ORDER BY sm.sort_order")
    List<com.bank.system.entity.SysMenu> selectMenusByUserId(@Param("userId") Long userId);
}
