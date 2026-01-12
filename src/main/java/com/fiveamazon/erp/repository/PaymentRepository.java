package com.fiveamazon.erp.repository;

import com.fiveamazon.erp.entity.PaymentPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentPO, Integer> {
    List<PaymentPO> findAllByOrderByTransactionDateDescCreateDateDesc();

    Page<PaymentPO> findAll(Specification<PaymentPO> spc, Pageable pageable);
    Long count(Specification<PaymentPO> specification);
}
