package com.fiveamazon.erp.repository;

import com.fiveamazon.erp.entity.PaymentPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentPO, Integer> {
    List<PaymentPO> findAllByOrderByTransactionDateDescCreateDateDesc();
}
