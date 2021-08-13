package com.fiveamazon.erp.service;


import com.fiveamazon.erp.common.CommonTable;
import com.fiveamazon.erp.dto.search.TransactionSearchDTO;
import com.fiveamazon.erp.entity.TransactionPO;

import java.util.Date;
import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface TransactionService {
    TransactionPO getById(Integer id);

    TransactionPO save(TransactionPO transactionPO);

    void createByExcel(Integer excelId);

    CommonTable search(TransactionSearchDTO searchDTO);

    List<TransactionPO> findByDate(Date dateFrom, Date dateTo, Integer storeId);

}
