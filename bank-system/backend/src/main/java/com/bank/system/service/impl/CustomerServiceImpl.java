package com.bank.system.service.impl;

import com.bank.system.common.BusinessException;
import com.bank.system.dto.CustomerCreateDTO;
import com.bank.system.dto.CustomerQueryDTO;
import com.bank.system.dto.CustomerUpdateDTO;
import com.bank.system.entity.Customer;
import com.bank.system.mapper.CustomerMapper;
import com.bank.system.service.CustomerService;
import com.bank.system.vo.CustomerVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;

    @Override
    public Page<CustomerVO> queryPage(CustomerQueryDTO queryDTO) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(Customer::getName, queryDTO.getKeyword())
                    .or()
                    .like(Customer::getPhone, queryDTO.getKeyword())
                    .or()
                    .like(Customer::getIdCard, queryDTO.getKeyword()));
        }
        wrapper.orderByDesc(Customer::getCreateTime);

        Page<Customer> page = Page.of(queryDTO.getPage(), queryDTO.getSize());
        Page<Customer> result = customerMapper.selectPage(page, wrapper);

        Page<CustomerVO> voPage = new Page<>();
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        voPage.setTotal(result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toVO).toList());
        return voPage;
    }

    @Override
    public CustomerVO getById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(404, "客户不存在");
        }
        return toVO(customer);
    }

    @Override
    public void create(CustomerCreateDTO dto) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getIdCard, dto.getIdCard());
        if (customerMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(409, "身份证号已存在");
        }
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        customer.setStatus(1);
        customerMapper.insert(customer);
    }

    @Override
    public void update(CustomerUpdateDTO dto) {
        Customer customer = customerMapper.selectById(dto.getId());
        if (customer == null) {
            throw new BusinessException(404, "客户不存在");
        }
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getIdCard, dto.getIdCard());
        wrapper.ne(Customer::getId, dto.getId());
        if (customerMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(409, "身份证号已被其他客户使用");
        }
        BeanUtils.copyProperties(dto, customer);
        customerMapper.updateById(customer);
    }

    @Override
    public void delete(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(404, "客户不存在");
        }
        customerMapper.deleteById(id);
    }

    private CustomerVO toVO(Customer customer) {
        CustomerVO vo = new CustomerVO();
        BeanUtils.copyProperties(customer, vo);
        return vo;
    }
}
