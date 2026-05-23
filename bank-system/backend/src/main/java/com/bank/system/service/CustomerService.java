package com.bank.system.service;

import com.bank.system.dto.CustomerCreateDTO;
import com.bank.system.dto.CustomerQueryDTO;
import com.bank.system.dto.CustomerUpdateDTO;
import com.bank.system.vo.CustomerVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface CustomerService {
    Page<CustomerVO> queryPage(CustomerQueryDTO queryDTO);
    CustomerVO getById(Long id);
    void create(CustomerCreateDTO dto);
    void update(CustomerUpdateDTO dto);
    void delete(Long id);
}
