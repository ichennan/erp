package com.fiveamazon.erp.security.service.impl;

import com.fiveamazon.erp.common.SimpleCommonException;
import com.fiveamazon.erp.security.entity.*;
import com.fiveamazon.erp.security.repository.SimpleAuthorityRepository;
import com.fiveamazon.erp.security.repository.SimpleGroupAuthorityRepository;
import com.fiveamazon.erp.security.repository.SimpleGroupRepository;
import com.fiveamazon.erp.security.repository.SimpleGroupUserRepository;
import com.fiveamazon.erp.security.service.SimpleGroupService;
import com.fiveamazon.erp.security.service.SimpleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimpleGroupServiceImpl implements SimpleGroupService {
    @Autowired
    private SimpleUserService simpleUserService;
    @Autowired
    private SimpleGroupRepository simpleGroupRepository;
    @Autowired
    private SimpleAuthorityRepository simpleAuthorityRepository;
    @Autowired
    private SimpleGroupUserRepository simpleGroupUserRepository;
    @Autowired
    private SimpleGroupAuthorityRepository simpleGroupAuthorityRepository;

    @Override
    public SimpleGroup getById(Integer id) {
        return simpleGroupRepository.getById(id);
    }

    @Override
    public List<SimpleGroup> findAll() {
        return simpleGroupRepository.findAll();
    }

    @Override
    public List<SimpleGroupAuthority> findSimpleGroupAuthorityListByGroupId(Integer groupId) {
        return simpleGroupAuthorityRepository.findByGroupId(groupId);
    }

    @Override
    public List<String> findSimpleAuthoriyListByUserName(String userName){
        List<String> list = new ArrayList<>();
        SimpleUser simpleUser = simpleUserService.getByUserName(userName);
        if(simpleUser == null){
            throw new SimpleCommonException("该用户名不存在: " + userName);
        }
        Integer userId = simpleUser.getId();
        List<SimpleGroupUser> simpleGroupUserList = simpleGroupUserRepository.findByUserId(userId);

        for(SimpleGroupUser simpleGroupUser : simpleGroupUserList){
            Integer groupId = simpleGroupUser.getGroupId();
            List<SimpleGroupAuthority> simpleGroupAuthorityList = simpleGroupAuthorityRepository.findByGroupId(groupId);
            for(SimpleGroupAuthority simpleGroupAuthority : simpleGroupAuthorityList){
                Integer authorityId = simpleGroupAuthority.getAuthorityId();
                SimpleAuthority simpleAuthority = simpleAuthorityRepository.getOne(authorityId);
                String authorityName = simpleAuthority.getAuthorityName();
                list.add(authorityName);
            }
        }

        return list;
    }

    @Override
    public void deleteGroupUserByUserId(Integer userId) {
        simpleGroupUserRepository.deleteByUserId(userId);
    }

    @Override
    public SimpleGroupUser saveGroupUser(Integer groupId, Integer userId) {
        SimpleGroupUser simpleGroupUser = new SimpleGroupUser();
        simpleGroupUser.setGroupId(groupId);
        simpleGroupUser.setUserId(userId);
        simpleGroupUser.setStatus("1");
        return simpleGroupUserRepository.save(simpleGroupUser);
    }

    @Override
    public SimpleGroup save(SimpleGroup simpleGroup) {
        return simpleGroupRepository.save(simpleGroup);
    }

    @Override
    public void deleteGroupAuthorityByGroupId(Integer groupId) {
        simpleGroupAuthorityRepository.deleteByGroupId(groupId);
    }

    @Override
    public SimpleGroupAuthority saveGroupAuthority(Integer groupId, Integer authorityId) {
        SimpleGroupAuthority simpleGroupAuthority = new SimpleGroupAuthority();
        simpleGroupAuthority.setGroupId(groupId);
        simpleGroupAuthority.setAuthorityId(authorityId);
        simpleGroupAuthority.setStatus("1");
        return simpleGroupAuthorityRepository.save(simpleGroupAuthority);
    }
}
