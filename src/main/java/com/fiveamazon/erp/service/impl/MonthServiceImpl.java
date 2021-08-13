package com.fiveamazon.erp.service.impl;

import com.fiveamazon.erp.entity.MonthPO;
import com.fiveamazon.erp.repository.MonthRepository;
import com.fiveamazon.erp.service.MonthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class MonthServiceImpl implements MonthService {
    @Autowired
    private MonthRepository theRepository;

    @Override
    public MonthPO getById(Integer id) {
        return theRepository.getById(id);
    }

    @Override
    public MonthPO save(MonthPO item) {
        return theRepository.save(item);
    }
}
