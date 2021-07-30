package com.fiveamazon.erp.repository;


import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.entity.PurchaseProductViewPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface PurchaseProductViewRepository extends JpaRepository<PurchaseProductViewPO, Integer> {
    Page<PurchaseProductViewPO> findAll(Specification<PurchaseProductViewPO> spc, Pageable pageable);

    @Query("select ppv.deliveryDate as deliveryDate, " +
            "ppv.purchaseId as purchaseId, " +
            "ppv.supplier as supplier, " +
            "ppv.unitPrice as unitPrice, " +
            "ppv.receivedQuantity as receivedQuantity, " +
            "p.name as name, " +
            "p.sn as sn, " +
            "p.color as color" +
            " from PurchaseProductViewPO ppv " +
            " left join ProductPO p " +
            " on ppv.productId = p.id " +
            " where (:supplier = '' or ppv.supplier = :supplier) " +
            " and (:deliveryDateTo = '' or ppv.deliveryDate <= :deliveryDateTo)" +
            " and (:deliveryDateFrom = '' or ppv.deliveryDate >= :deliveryDateFrom)" +
            " order by ppv.deliveryDate, ppv.purchaseId")
    List<JSONObject> donwload(String supplier, String deliveryDateFrom, String deliveryDateTo);
}
