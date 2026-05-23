package com.bank.system.service;

import com.bank.system.dto.*;
import com.bank.system.vo.CreditApplicationVO;
import com.bank.system.vo.CreditApprovalVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface CreditService {
    Page<CreditApplicationVO> queryApplications(CreditApplicationQueryDTO queryDTO);
    CreditApplicationVO getApplicationById(Long id);
    void createApplication(CreditApplicationCreateDTO dto);
    void updateApplication(Long id, CreditApplicationCreateDTO dto);
    void approveApplication(Long id, CreditApprovalDTO dto);
    Page<CreditApprovalVO> queryApprovals(CreditApprovalQueryDTO queryDTO);
    CreditApprovalVO getApprovalById(Long id);
}
