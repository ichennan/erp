--
-- based on view view_inventory_product and view_inventory_sku
--

create or replace view view_sku_info as

SELECT
if(`s`.`combine_count` > 1, concat(`s`.`sku`,'组合',`s`.`combine_id`,'/',`s`.`combine_count`), `s`.`sku`) as `sku_desc`,
`s`.*,
vp.sum_product_purchase_quantity,
vp.sum_product_shipment_quantity,
vp.sum_product_shipment_ontheway_quantity,
vp.sum_product_packet_quantity,
vp.sum_product_shipment_arrived_quantity,
vp.product_inventory_quantity,
vs.sum_sku_shipment_quantity,
vs.sum_sku_shipment_ontheway_quantity,
vs.sum_sku_shipment_arrived_quantity

from `tbl_sku_info` as `s`

left join `view_inventory_product` as `vp`
on (`s`.`product_id` = `vp`.`id`)

left join `view_inventory_sku` as `vs`
on (`s`.`id` = `vs`.`id`)