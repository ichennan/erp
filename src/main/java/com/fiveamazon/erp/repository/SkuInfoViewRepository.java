package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.SkuInfoVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface SkuInfoViewRepository extends JpaRepository<SkuInfoVO, Integer> {
    List<SkuInfoVO> findBySkuIsNotNull();

    @Query(nativeQuery = true,
            value = "select sku_id, delivery_date, sum(quantity) as shipment_quantity from " +
                    "(select sku_id, delivery_date, quantity " +
                    "   from tbl_shipment_detail sd " +
                    "   left join tbl_shipment s " +
                    "   on sd.shipment_id = s.id " +
                    "   where box <> 'Plan' and sku_id = ?1" +
                    ") as ssd " +
                    "where length(delivery_date) > 0 " +
                    "group by sku_id, delivery_date ")
    List<Object> findSkuShipment(Integer skuId);

    @Query(nativeQuery = true,
            value = "select sku_id, delivery_date, sum(quantity) as shipment_quantity from " +
                    "(select sku_id, delivery_date, quantity " +
                    "   from tbl_shipment_detail sd " +
                    "   left join tbl_shipment s " +
                    "   on sd.shipment_id = s.id " +
                    "   where box <> 'Plan' and product_id = ?1 and sku_id <> ?2" +
                    ") as ssd " +
                    "where length(delivery_date) > 0 " +
                    "group by sku_id, delivery_date ")
    List<Object> findSkuElseShipment(Integer productId, Integer skuId);

    @Query(nativeQuery = true,
            value = "select product_id, received_date, sum(received_quantity) as purchase_quantity from " +
                    "(select product_id, received_date, received_quantity " +
                    "   from tbl_purchase_detail pd " +
                    "   left join tbl_purchase p " +
                    "   on pd.purchase_id = p.id " +
                    "   where product_id = ?1" +
                    ") as ppd " +
                    "where length(received_date) > 0 " +
                    "group by product_id, received_date ")
    List<Object> findProductPurchase(Integer productId);
}
