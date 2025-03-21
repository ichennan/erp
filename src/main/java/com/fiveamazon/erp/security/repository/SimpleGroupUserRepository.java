package com.fiveamazon.erp.security.repository;


import com.fiveamazon.erp.security.entity.SimpleGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface SimpleGroupUserRepository extends JpaRepository<SimpleGroupUser, Integer> {
    List<SimpleGroupUser> findByUserId(Integer userId);

    void deleteByUserId(Integer userId);
}
