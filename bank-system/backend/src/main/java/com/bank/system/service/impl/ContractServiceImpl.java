package com.bank.system.service.impl;

import com.bank.system.common.BusinessException;
import com.bank.system.dto.ContractCreateDTO;
import com.bank.system.dto.ContractQueryDTO;
import com.bank.system.dto.ContractUpdateDTO;
import com.bank.system.entity.Contract;
import com.bank.system.entity.CreditApproval;
import com.bank.system.entity.Customer;
import com.bank.system.mapper.ContractMapper;
import com.bank.system.mapper.CreditApprovalMapper;
import com.bank.system.mapper.CustomerMapper;
import com.bank.system.service.ContractService;
import com.bank.system.vo.ContractVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;
    private final CreditApprovalMapper approvalMapper;
    private final CustomerMapper customerMapper;

    @Override
    public Page<ContractVO> queryPage(ContractQueryDTO queryDTO) {
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(Contract::getContractNo, queryDTO.getKeyword()));
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Contract::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByDesc(Contract::getCreateTime);

        Page<Contract> page = Page.of(queryDTO.getPage(), queryDTO.getSize());
        Page<Contract> result = contractMapper.selectPage(page, wrapper);

        Page<ContractVO> voPage = new Page<>();
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        voPage.setTotal(result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toVO).toList());
        return voPage;
    }

    @Override
    public ContractVO getById(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(404, "合同不存在");
        }
        return toVO(contract);
    }

    @Override
    @Transactional
    public void create(ContractCreateDTO dto) {
        CreditApproval approval = approvalMapper.selectById(dto.getApprovalId());
        if (approval == null) {
            throw new BusinessException(404, "授信批复不存在");
        }
        if (approval.getStatus() != 1) {
            throw new BusinessException("授信批复已失效，无法签订合同");
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(approval.getStartDate()) || today.isAfter(approval.getEndDate())) {
            throw new BusinessException("授信批复不在有效期内");
        }
        if (dto.getContractAmount().compareTo(approval.getCreditLimit()) > 0) {
            throw new BusinessException("合同金额不能超过批复额度");
        }

        // Check no existing active contract for this approval
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getApprovalId, dto.getApprovalId());
        wrapper.in(Contract::getStatus, 1, 2);
        if (contractMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该批复已有关联的有效合同");
        }

        Contract contract = new Contract();
        BeanUtils.copyProperties(dto, contract);
        contract.setContractNo(generateContractNo());
        contract.setCustomerId(approval.getCustomerId());
        contract.setStatus(1);
        contractMapper.insert(contract);
    }

    @Override
    @Transactional
    public void update(Long id, ContractUpdateDTO dto) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(404, "合同不存在");
        }
        if (contract.getStatus() != 1) {
            throw new BusinessException("仅有效状态的合同可以变更");
        }

        CreditApproval approval = approvalMapper.selectById(contract.getApprovalId());
        if (approval != null && dto.getContractAmount().compareTo(approval.getCreditLimit()) > 0) {
            throw new BusinessException("合同金额不能超过批复额度");
        }

        BeanUtils.copyProperties(dto, contract);
        contract.setId(id);
        contract.setStatus(2); // 已变更
        contractMapper.updateById(contract);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(404, "合同不存在");
        }
        if (contract.getStatus() != 1) {
            throw new BusinessException("仅有效状态的合同可以作废");
        }
        contract.setStatus(3); // 已作废
        contractMapper.updateById(contract);
    }

    private ContractVO toVO(Contract contract) {
        ContractVO vo = new ContractVO();
        BeanUtils.copyProperties(contract, vo);
        CreditApproval approval = approvalMapper.selectById(contract.getApprovalId());
        if (approval != null) {
            vo.setApprovalNo(approval.getApprovalNo());
        }
        Customer customer = customerMapper.selectById(contract.getCustomerId());
        if (customer != null) {
            vo.setCustomerName(customer.getName());
        }
        return vo;
    }

    private String generateContractNo() {
        String prefix = "CT" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Contract::getContractNo, prefix);
        long count = contractMapper.selectCount(wrapper);
        return prefix + String.format("%04d", count + 1);
    }
}
