package com.bank.system.controller;

import com.bank.system.common.Result;
import com.bank.system.dto.ContractCreateDTO;
import com.bank.system.dto.ContractQueryDTO;
import com.bank.system.dto.ContractUpdateDTO;
import com.bank.system.service.ContractService;
import com.bank.system.vo.ContractVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    @PreAuthorize("hasAuthority('contract:list')")
    public Result<Page<ContractVO>> list(ContractQueryDTO queryDTO) {
        return Result.success(contractService.queryPage(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('contract:list')")
    public Result<ContractVO> detail(@PathVariable Long id) {
        return Result.success(contractService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('contract:add')")
    public Result<Void> create(@Valid @RequestBody ContractCreateDTO dto) {
        contractService.create(dto);
        return Result.success("签订成功", null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('contract:add')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ContractUpdateDTO dto) {
        contractService.update(id, dto);
        return Result.success("变更成功", null);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('contract:add')")
    public Result<Void> cancel(@PathVariable Long id) {
        contractService.cancel(id);
        return Result.success("合同已作废", null);
    }
}
