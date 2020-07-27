package com.fiveamazon.erp.security.service.impl;

import com.fiveamazon.erp.security.entity.SimpleUser;
import com.fiveamazon.erp.security.repository.SimpleUserRepository;
import com.fiveamazon.erp.security.service.SimpleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleUserServiceImpl implements SimpleUserService {
    @Autowired
    private SimpleUserRepository simpleUserRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Long countAll() {
        return simpleUserRepository.count();
    }

    @Override
    public SimpleUser getById(Integer id) {
        return simpleUserRepository.getOne(id);
    }

    @Override
    public SimpleUser getByUserName(String userName){
        return simpleUserRepository.getByUserName(userName);
    }

    @Override
    public void setPassword(String userName, String password) {
        SimpleUser simpleUser = getByUserName(userName);
        simpleUser.setPassword(bCryptPasswordEncoder.encode(password));
        simpleUserRepository.save(simpleUser);
    }

    @Override
    public List<SimpleUser> findAll() {
        return simpleUserRepository.findAll();
    }

    @Override
    public SimpleUser save(SimpleUser simpleUser) {
        return simpleUserRepository.save(simpleUser);
    }
}
