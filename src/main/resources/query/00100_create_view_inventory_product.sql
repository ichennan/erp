create or replace view view_inventory_product as

select p.id as id,
       ifnull(sumPurchaseQuantity, 0) as sum_product_purchase_quantity,
       ifnull(sumShipmentQuantity, 0) as sum_product_shipment_quantity,
       ifnull(sumShipmentOnthewayQuantity, 0) as sum_product_shipment_ontheway_quantity,
       ifnull(sumPacketQuantity, 0) as sum_product_packet_quantity,
       ifnull(sumOverseaQuantity, 0) as sum_product_oversea_quantity,
       ifnull(sumStocktakingQuantity, 0) as sum_product_stocktaking_quantity,
       (ifnull(sumShipmentQuantity, 0) - ifnull(sumShipmentOnthewayQuantity, 0)) as sum_product_shipment_arrived_quantity,
       (ifnull(sumPurchaseQuantity, 0) + ifnull(sumStocktakingQuantity, 0) - ifnull(sumShipmentQuantity, 0) - ifnull(sumPacketQuantity, 0) - ifnull(sumOverseaQuantity, 0)) as product_inventory_quantity
       from tbl_product p

left join
(select product_id, sum(received_quantity) as sumPurchaseQuantity from tbl_purchase_detail group by product_id)
as t_purchase
on p.id = t_purchase.product_id

left join
    (select product_id, sum(quantity) as sumShipmentQuantity from tbl_shipment_detail where box <> 'Plan' group by product_id)
as t_shipment
on p.id = t_shipment.product_id

left join
    (select product_id, sum(quantity) as sumShipmentOnthewayQuantity from tbl_shipment_detail
        left join tbl_shipment
        on tbl_shipment_detail.shipment_id = tbl_shipment.id
        where box <> 'Plan' and length(tbl_shipment.signed_date) <= 0
        group by product_id)
as t_shipment_ontheway
on p.id = t_shipment_ontheway.product_id

left join
(select product_id, ifnull(sum(quantity), 0) as sumPacketQuantity from tbl_packet_detail group by product_id)
as t_packet
on p.id = t_packet.product_id

left join
(select product_id, ifnull(sum(quantity), 0) as sumOverseaQuantity from tbl_oversea_detail where fba_no = '' group by product_id)
as t_oversea
on p.id = t_oversea.product_id

left join
(select product_id, ifnull(sum(adjustment_quantity), 0) as sumStocktakingQuantity from tbl_stocktaking_detail group by product_id)
as t_stocktaking
on p.id = t_stocktaking.product_id

;
