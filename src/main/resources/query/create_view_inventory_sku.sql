create or replace view view_inventory_sku as

select p.id as id,
       p.product_id as product_id,
       ifnull(sumSkuShipmentQuantity, 0) as sum_sku_shipment_quantity,
       ifnull(sumSkuShipmentOnthewayQuantity, 0) as sum_sku_shipment_ontheway_quantity,
       (ifnull(sumSkuShipmentQuantity, 0) - ifnull(sumSkuShipmentOnthewayQuantity, 0)) as sum_sku_shipment_arrived_quantity,
       ifnull(sumSkuOverseaQuantity, 0) as sum_sku_oversea_quantity
from tbl_sku_info p

left join
    (select sku_id, sum(quantity) as sumSkuShipmentQuantity from tbl_shipment_detail where box <> 'Plan' group by sku_id)
as t_shipment
on p.id = t_shipment.sku_id

left join
    (select sku_id, sum(quantity) as sumSkuShipmentOnthewayQuantity from tbl_shipment_detail
        left join tbl_shipment
        on tbl_shipment_detail.shipment_id = tbl_shipment.id
        where box <> 'Plan' and length(tbl_shipment.signed_date) <= 0
        group by sku_id)
as t_shipment_ontheway
on p.id = t_shipment_ontheway.sku_id

left join
(select sku_id, sum(quantity) as sumSkuOverseaQuantity from tbl_oversea_detail where fba_no <> '' group by sku_id)
as t_oversea
on p.id = t_oversea.sku_id

;
