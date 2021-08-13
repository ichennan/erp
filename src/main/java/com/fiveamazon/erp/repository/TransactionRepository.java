package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.TransactionPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionPO, Integer> {
    TransactionPO getById(Integer id);
    void deleteByStoreIdAndTransactionTimeBetween(Integer storeId, Date dateFrom, Date dateTo);

    Page<TransactionPO> findAll(Specification<TransactionPO> spc, Pageable pageable);
    Long count(Specification<TransactionPO> specification);

    List<TransactionPO> findByTransactionTimeBetweenAndStoreIdOrderByStoreIdAscTransactionTimeAsc(Date dateFrom, Date dateTo, Integer storeId);

}
