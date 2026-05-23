package com.bank.system.service;

import com.bank.system.dto.ContractCreateDTO;
import com.bank.system.dto.ContractQueryDTO;
import com.bank.system.dto.ContractUpdateDTO;
import com.bank.system.vo.ContractVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ContractService {
    Page<ContractVO> queryPage(ContractQueryDTO queryDTO);
    ContractVO getById(Long id);
    void create(ContractCreateDTO dto);
    void update(Long id, ContractUpdateDTO dto);
    void cancel(Long id);
}
