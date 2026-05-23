package com.bank.system.controller;

import com.bank.system.common.Result;
import com.bank.system.dto.*;
import com.bank.system.service.CreditService;
import com.bank.system.vo.CreditApplicationVO;
import com.bank.system.vo.CreditApprovalVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @GetMapping("/applications")
    @PreAuthorize("hasAuthority('credit:application')")
    public Result<Page<CreditApplicationVO>> listApplications(CreditApplicationQueryDTO queryDTO) {
        return Result.success(creditService.queryApplications(queryDTO));
    }

    @GetMapping("/applications/{id}")
    @PreAuthorize("hasAuthority('credit:application')")
    public Result<CreditApplicationVO> getApplication(@PathVariable Long id) {
        return Result.success(creditService.getApplicationById(id));
    }

    @PostMapping("/applications")
    @PreAuthorize("hasAuthority('credit:application')")
    public Result<Void> createApplication(@Valid @RequestBody CreditApplicationCreateDTO dto) {
        creditService.createApplication(dto);
        return Result.success("新增成功", null);
    }

    @PutMapping("/applications/{id}")
    @PreAuthorize("hasAuthority('credit:application')")
    public Result<Void> updateApplication(@PathVariable Long id, @Valid @RequestBody CreditApplicationCreateDTO dto) {
        creditService.updateApplication(id, dto);
        return Result.success("更新成功", null);
    }

    @PostMapping("/applications/{id}/approve")
    @PreAuthorize("hasAuthority('credit:approval')")
    public Result<Void> approveApplication(@PathVariable Long id, @Valid @RequestBody CreditApprovalDTO dto) {
        creditService.approveApplication(id, dto);
        return Result.success("审批成功", null);
    }

    @GetMapping("/approvals")
    @PreAuthorize("hasAuthority('credit:approval')")
    public Result<Page<CreditApprovalVO>> listApprovals(CreditApprovalQueryDTO queryDTO) {
        return Result.success(creditService.queryApprovals(queryDTO));
    }

    @GetMapping("/approvals/{id}")
    @PreAuthorize("hasAuthority('credit:approval')")
    public Result<CreditApprovalVO> getApproval(@PathVariable Long id) {
        return Result.success(creditService.getApprovalById(id));
    }
}
