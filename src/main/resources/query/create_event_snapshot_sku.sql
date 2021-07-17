DROP EVENT event_snapshot_sku_inventory;
CREATE EVENT event_snapshot_sku_inventory
ON SCHEDULE EVERY 1 DAY
STARTS '2020-08-17 23:55:00'
ON COMPLETION PRESERVE
COMMENT 'Sku Snapshot Every Mid-Night'
DO

insert into snapshot_sku(
snapshot_date

,sku_id
,product_id
,sum_product_purchase_quantity
,sum_product_shipment_quantity
,sum_product_shipment_ontheway_quantity
,sum_product_packet_quantity
,sum_product_shipment_arrived_quantity
,sum_product_oversea_quantity
,product_inventory_quantity
,sum_sku_shipment_quantity
,sum_sku_shipment_ontheway_quantity
,sum_sku_shipment_arrived_quantity
,sum_sku_oversea_quantity
)

select
DATE_FORMAT(NOW(),'%Y%m%d')

,id
,product_id
,sum_product_purchase_quantity
,sum_product_shipment_quantity
,sum_product_shipment_ontheway_quantity
,sum_product_packet_quantity
,sum_product_shipment_arrived_quantity
,sum_product_oversea_quantity
,product_inventory_quantity
,sum_sku_shipment_quantity
,sum_sku_shipment_ontheway_quantity
,sum_sku_shipment_arrived_quantity
,sum_sku_oversea_quantity
from view_sku_info

;