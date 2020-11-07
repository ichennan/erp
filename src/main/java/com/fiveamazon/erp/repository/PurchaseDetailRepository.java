package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.PurchaseDetailPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface PurchaseDetailRepository extends JpaRepository<PurchaseDetailPO, Integer> {
    void deleteByPurchaseId(Integer purchaseId);

    List<PurchaseDetailPO> findAllByPurchaseId(Integer purchaseId);


//    @Query("select new com.fiveamazon.erp.dto.PurchaseDetailViewDTO(pd, p) from PurchaseDetailPO pd left join PurchasePO p on  pd.purchaseId = p.id where pd.productId = :productId")
//    List<PurchaseDetailViewDTO> findByProductId(Integer productId);
}
