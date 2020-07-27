package com.fiveamazon.erp.security.service;

import com.fiveamazon.erp.security.entity.SimpleGroup;
import com.fiveamazon.erp.security.entity.SimpleGroupAuthority;
import com.fiveamazon.erp.security.entity.SimpleGroupUser;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface SimpleGroupService {
	SimpleGroup getById(Integer id);
	List<String> findSimpleAuthoriyListByUserName(String userName);
	List<SimpleGroupAuthority> findSimpleGroupAuthorityListByGroupId(Integer groupId);
	void deleteGroupUserByUserId(Integer userId);
	SimpleGroupUser saveGroupUser(Integer groupId, Integer userId);
	List<SimpleGroup> findAll();
	SimpleGroup save(SimpleGroup simpleGroup);
	void deleteGroupAuthorityByGroupId(Integer groupId);
	SimpleGroupAuthority saveGroupAuthority(Integer groupId, Integer authorityId);
}
