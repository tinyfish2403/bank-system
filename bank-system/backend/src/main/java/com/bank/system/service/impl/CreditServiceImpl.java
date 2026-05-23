package com.bank.system.service.impl;

import com.bank.system.common.BusinessException;
import com.bank.system.dto.*;
import com.bank.system.entity.CreditApplication;
import com.bank.system.entity.CreditApproval;
import com.bank.system.entity.Customer;
import com.bank.system.mapper.CreditApplicationMapper;
import com.bank.system.mapper.CreditApprovalMapper;
import com.bank.system.mapper.CustomerMapper;
import com.bank.system.service.CreditService;
import com.bank.system.vo.CreditApplicationVO;
import com.bank.system.vo.CreditApprovalVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final CreditApplicationMapper applicationMapper;
    private final CreditApprovalMapper approvalMapper;
    private final CustomerMapper customerMapper;

    @Override
    public Page<CreditApplicationVO> queryApplications(CreditApplicationQueryDTO queryDTO) {
        LambdaQueryWrapper<CreditApplication> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(CreditApplication::getApplicationNo, queryDTO.getKeyword())
                    .or()
                    .like(CreditApplication::getPurpose, queryDTO.getKeyword()));
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(CreditApplication::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByDesc(CreditApplication::getCreateTime);

        Page<CreditApplication> page = Page.of(queryDTO.getPage(), queryDTO.getSize());
        Page<CreditApplication> result = applicationMapper.selectPage(page, wrapper);

        Page<CreditApplicationVO> voPage = new Page<>();
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        voPage.setTotal(result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toAppVO).toList());
        return voPage;
    }

    @Override
    public CreditApplicationVO getApplicationById(Long id) {
        CreditApplication app = applicationMapper.selectById(id);
        if (app == null) {
            throw new BusinessException(404, "授信申请不存在");
        }
        return toAppVO(app);
    }

    @Override
    @Transactional
    public void createApplication(CreditApplicationCreateDTO dto) {
        CreditApplication app = new CreditApplication();
        BeanUtils.copyProperties(dto, app);
        app.setApplicationNo(generateApplicationNo());
        app.setStatus(1);
        app.setApplyTime(LocalDateTime.now());
        applicationMapper.insert(app);
    }

    @Override
    @Transactional
    public void updateApplication(Long id, CreditApplicationCreateDTO dto) {
        CreditApplication app = applicationMapper.selectById(id);
        if (app == null) {
            throw new BusinessException(404, "授信申请不存在");
        }
        if (app.getStatus() != 1) {
            throw new BusinessException("仅待审批状态的申请可以编辑");
        }
        BeanUtils.copyProperties(dto, app);
        app.setId(id);
        applicationMapper.updateById(app);
    }

    @Override
    @Transactional
    public void approveApplication(Long id, CreditApprovalDTO dto) {
        CreditApplication app = applicationMapper.selectById(id);
        if (app == null) {
            throw new BusinessException(404, "授信申请不存在");
        }
        if (app.getStatus() != 1) {
            throw new BusinessException("仅待审批状态的申请可以审批");
        }
        if (dto.getApprovedAmount().compareTo(app.getCreditAmount()) > 0) {
            throw new BusinessException("批复金额不能超过申请金额");
        }
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new BusinessException("有效期开始日期不能晚于结束日期");
        }

        // Update application status
        app.setStatus(2);
        applicationMapper.updateById(app);

        // Create approval record
        CreditApproval approval = new CreditApproval();
        approval.setApprovalNo(generateApprovalNo());
        approval.setApplicationId(id);
        approval.setCustomerId(app.getCustomerId());
        BeanUtils.copyProperties(dto, approval);
        approval.setStatus(1);
        approval.setApprovalTime(LocalDateTime.now());
        approvalMapper.insert(approval);
    }

    @Override
    public Page<CreditApprovalVO> queryApprovals(CreditApprovalQueryDTO queryDTO) {
        LambdaQueryWrapper<CreditApproval> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(CreditApproval::getApprovalNo, queryDTO.getKeyword()));
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(CreditApproval::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByDesc(CreditApproval::getCreateTime);

        Page<CreditApproval> page = Page.of(queryDTO.getPage(), queryDTO.getSize());
        Page<CreditApproval> result = approvalMapper.selectPage(page, wrapper);

        Page<CreditApprovalVO> voPage = new Page<>();
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        voPage.setTotal(result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toApprovalVO).toList());
        return voPage;
    }

    @Override
    public CreditApprovalVO getApprovalById(Long id) {
        CreditApproval approval = approvalMapper.selectById(id);
        if (approval == null) {
            throw new BusinessException(404, "授信批复不存在");
        }
        return toApprovalVO(approval);
    }

    private CreditApplicationVO toAppVO(CreditApplication app) {
        CreditApplicationVO vo = new CreditApplicationVO();
        BeanUtils.copyProperties(app, vo);
        Customer customer = customerMapper.selectById(app.getCustomerId());
        if (customer != null) {
            vo.setCustomerName(customer.getName());
        }
        return vo;
    }

    private CreditApprovalVO toApprovalVO(CreditApproval approval) {
        CreditApprovalVO vo = new CreditApprovalVO();
        BeanUtils.copyProperties(approval, vo);
        vo.setApplicationNoVal(null);
        CreditApplication app = applicationMapper.selectById(approval.getApplicationId());
        if (app != null) {
            vo.setApplicationNoVal(app.getApplicationNo());
        }
        Customer customer = customerMapper.selectById(approval.getCustomerId());
        if (customer != null) {
            vo.setCustomerName(customer.getName());
        }
        return vo;
    }

    private String generateApplicationNo() {
        String prefix = "CA" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<CreditApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(CreditApplication::getApplicationNo, prefix);
        long count = applicationMapper.selectCount(wrapper);
        return prefix + String.format("%04d", count + 1);
    }

    private String generateApprovalNo() {
        String prefix = "AP" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<CreditApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(CreditApproval::getApprovalNo, prefix);
        long count = approvalMapper.selectCount(wrapper);
        return prefix + String.format("%04d", count + 1);
    }
}
