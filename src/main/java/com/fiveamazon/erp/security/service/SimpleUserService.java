package com.fiveamazon.erp.security.service;


import com.fiveamazon.erp.security.entity.SimpleUser;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface SimpleUserService {
    Long countAll();

    SimpleUser getById(Integer id);

    SimpleUser getByUserName(String userName);

    void setPassword(String userName, String password);

    List<SimpleUser> findAll();

    SimpleUser save(SimpleUser simpleUser);
}
