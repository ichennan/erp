package com.fiveamazon.erp.security.repository;


import com.fiveamazon.erp.security.entity.SimpleGroupAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface SimpleGroupAuthorityRepository extends JpaRepository<SimpleGroupAuthority, Integer> {
    List<SimpleGroupAuthority> findByGroupId(Integer groupId);

    void deleteByGroupId(Integer groupId);
}
