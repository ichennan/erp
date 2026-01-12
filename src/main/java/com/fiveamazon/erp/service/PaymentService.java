package com.fiveamazon.erp.service;

import com.fiveamazon.erp.common.CommonTable;
import com.fiveamazon.erp.dto.PaymentDTO;
import com.fiveamazon.erp.dto.search.PaymentSearchDTO;
import com.fiveamazon.erp.entity.PaymentPO;

import java.util.List;

public interface PaymentService {
    Long countAll();

    PaymentPO getById(Integer id);

    List<PaymentPO> findAll();

    PaymentPO save(PaymentDTO dto);

    CommonTable search(PaymentSearchDTO searchDTO);
}
