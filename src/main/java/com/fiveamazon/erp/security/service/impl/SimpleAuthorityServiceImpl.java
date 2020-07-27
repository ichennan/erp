package com.fiveamazon.erp.security.service.impl;

import com.fiveamazon.erp.security.entity.SimpleAuthority;
import com.fiveamazon.erp.security.repository.SimpleAuthorityRepository;
import com.fiveamazon.erp.security.service.SimpleAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleAuthorityServiceImpl implements SimpleAuthorityService {
    @Autowired
    private SimpleAuthorityRepository simpleAuthorityRepository;

    @Override
    public List<SimpleAuthority> findAll() {
        return simpleAuthorityRepository.findAll();
    }
}
