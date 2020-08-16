CREATE EVENT IF NOT EXISTS event_snapshot_inventory
ON SCHEDULE EVERY 1 DAY
STARTS '2020-08-13 23:50:00'
ON COMPLETION PRESERVE
COMMENT 'Inventory Snapshot Every Mid-Night'
DO
insert into tbl_inventory_snapshot(
snapshot_date,
product_id,
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
id,
all_purchase_quantity,
all_stocktaking_quantity,
all_packet_quantity,
all_shipment_quantity,
inventory_quantity,
ontheway_shipment_quantity,
ontheway_purchase_quantity
from view_inventory