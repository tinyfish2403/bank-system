package com.bank.system.controller;

import com.bank.system.common.Result;
import com.bank.system.dto.CustomerCreateDTO;
import com.bank.system.dto.CustomerQueryDTO;
import com.bank.system.dto.CustomerUpdateDTO;
import com.bank.system.service.CustomerService;
import com.bank.system.vo.CustomerVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAuthority('customer:list')")
    public Result<Page<CustomerVO>> list(CustomerQueryDTO queryDTO) {
        return Result.success(customerService.queryPage(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:list')")
    public Result<CustomerVO> detail(@PathVariable Long id) {
        return Result.success(customerService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('customer:add')")
    public Result<Void> create(@Valid @RequestBody CustomerCreateDTO dto) {
        customerService.create(dto);
        return Result.success("新增成功", null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:add')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateDTO dto) {
        dto.setId(id);
        customerService.update(dto);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:add')")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return Result.success("删除成功", null);
    }
}
