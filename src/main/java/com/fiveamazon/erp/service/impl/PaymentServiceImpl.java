package com.fiveamazon.erp.service.impl;

import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.PaymentDTO;
import com.fiveamazon.erp.entity.PaymentPO;
import com.fiveamazon.erp.repository.PaymentRepository;
import com.fiveamazon.erp.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository theRepository;

    private PaymentPO save(PaymentPO item) {
        return theRepository.save(item);
    }

    @Override
    public Long countAll() {
        return theRepository.count();
    }

    @Override
    public PaymentPO getById(Integer id) {
        return theRepository.findById(id).orElse(null);
    }

    @Override
    public List<PaymentPO> findAll() {
        return theRepository.findAllByOrderByTransactionDateDescCreateDateDesc();
    }

    @Override
    public PaymentPO save(PaymentDTO dto) {
        Date today = new Date();
        Integer id = dto.getId();
        if (SimpleConstant.ACTION_DELETE.equalsIgnoreCase(dto.getAction())) {
            theRepository.deleteById(id);
            return new PaymentPO();
        }
        PaymentPO item;
        if (id == null || id == 0) {
            item = new PaymentPO();
            item.setCreateDate(today);
            item.setCreateUser(dto.getUsername());
        } else {
            item = getById(id);
            if (item == null) {
                item = new PaymentPO();
                item.setCreateDate(today);
                item.setCreateUser(dto.getUsername());
            } else {
                item.setUpdateDate(today);
                item.setUpdateUser(dto.getUsername());
            }
        }
        BeanUtils.copyProperties(dto, item, "id");
        return save(item);
    }
}
