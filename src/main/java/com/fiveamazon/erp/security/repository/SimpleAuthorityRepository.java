package com.fiveamazon.erp.security.repository;

import com.fiveamazon.erp.security.entity.SimpleAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface SimpleAuthorityRepository extends JpaRepository<SimpleAuthority, Integer> {
}
