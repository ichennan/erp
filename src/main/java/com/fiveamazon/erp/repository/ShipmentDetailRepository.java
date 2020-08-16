package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.ShipmentDetailPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface ShipmentDetailRepository extends JpaRepository<ShipmentDetailPO, Integer> {
    List<ShipmentDetailPO> findAllByShipmentIdOrderByBox(Integer shipmentId);
    void deleteByShipmentIdEqualsAndBoxNotLike(Integer shipmentId, String plan);

//    @Query("select new com.fiveamazon.erp.dto.ShipmentDetailViewDTO(sd, s) from ShipmentDetailPO sd left join ShipmentPO s on  sd.shipmentId = s.id where sd.box <> 'Plan' and sd.productId = :productId")
//    List<ShipmentDetailViewDTO> findByProductId(Integer productId);


}
