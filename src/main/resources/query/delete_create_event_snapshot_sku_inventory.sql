--
-- delete on 20200817 00:53:00
--

CREATE EVENT event_snapshot_sku_inventory
ON SCHEDULE EVERY 1 DAY
STARTS '2020-08-13 23:55:00'
ON COMPLETION PRESERVE
COMMENT 'Sku Inventory Snapshot Every Mid-Night'
DO

insert into snapshot_sku_inventory(
snapshot_date,

product_id,
sku_desc,
sku_id,
combine_id,
combine_count,
store_id,
sku,
fnsku,
asin,

all_purchase_quantity,
all_stocktaking_quantity,
all_packet_quantity,
all_shipment_quantity,
inventory_quantity,
ontheway_shipment_quantity,
ontheway_purchase_quantity
)

select
DATE_FORMAT(NOW(),'%Y%m%d'),

product_id,
sku_desc,
sku_id,
combine_id,
combine_count,
store_id,
sku,
fnsku,
asin,
all_purchase_quantity,
all_stocktaking_quantity,
all_packet_quantity,
all_shipment_quantity,
inventory_quantity,
ontheway_shipment_quantity,
ontheway_purchase_quantity
from view_sku