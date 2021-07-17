package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.OverseaDetailPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface OverseaDetailRepository extends JpaRepository<OverseaDetailPO, Integer> {
    OverseaDetailPO getById(Integer id);
    List<OverseaDetailPO> findByOverseaIdOrderByBox(Integer overseaId);
    void deleteByOverseaId(Integer overseaId);
}
