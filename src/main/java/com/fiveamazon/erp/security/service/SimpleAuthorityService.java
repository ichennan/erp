package com.fiveamazon.erp.security.service;


import com.fiveamazon.erp.security.entity.SimpleAuthority;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface SimpleAuthorityService {
    List<SimpleAuthority> findAll();
}
