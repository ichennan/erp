CREATE EVENT event_snapshot_sku_inventory
ON SCHEDULE EVERY 1 DAY
STARTS '2020-08-17 23:40:00'
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
,product_inventory_quantity
,sum_sku_shipment_quantity
,sum_sku_shipment_ontheway_quantity
,sum_sku_shipment_arrived_quantity
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
,product_inventory_quantity
,sum_sku_shipment_quantity
,sum_sku_shipment_ontheway_quantity
,sum_sku_shipment_arrived_quantity
from view_sku_info

;;;;;;;;;;;;;;;;;;;;;;;;

DROP TRIGGER trigger_purchase_price;

CREATE TRIGGER trigger_purchase_price
AFTER update ON tbl_product
FOR EACH ROW
    IF NEW.purchase_price != OLD.purchase_price Then
        INSERT INTO log_purchase_price
        SET product_id = NEW.id,
            purchase_price = NEW.purchase_price,
            create_date = NOW();
        END IF

;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_inventory_product as

select p.id as id,
       ifnull(sumPurchaseQuantity, 0) as sum_product_purchase_quantity,
       ifnull(sumShipmentQuantity, 0) as sum_product_shipment_quantity,
       ifnull(sumShipmentOnthewayQuantity, 0) as sum_product_shipment_ontheway_quantity,
       ifnull(sumPacketQuantity, 0) as sum_product_packet_quantity,
       (ifnull(sumShipmentQuantity, 0) - ifnull(sumShipmentOnthewayQuantity, 0)) as sum_product_shipment_arrived_quantity,
       (ifnull(sumPurchaseQuantity, 0) - ifnull(sumShipmentQuantity, 0) - ifnull(sumPacketQuantity, 0)) as product_inventory_quantity
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

;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_inventory_sku as

select p.id as id,
       p.product_id as product_id,
       ifnull(sumSkuShipmentQuantity, 0) as sum_sku_shipment_quantity,
       ifnull(sumSkuShipmentOnthewayQuantity, 0) as sum_sku_shipment_ontheway_quantity,
       (ifnull(sumSkuShipmentQuantity, 0) - ifnull(sumSkuShipmentOnthewayQuantity, 0)) as sum_sku_shipment_arrived_quantity
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

;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_purchase as

select `p`.*,
       `t`.`product_id_group` AS `product_id_group`
from
(   `tbl_purchase` as `p`
    left join
    (   select group_concat(`tbl_purchase_detail`.`product_id` separator ',') AS `product_id_group`,
               `tbl_purchase_detail`.`purchase_id` AS `purchase_id`
        from `tbl_purchase_detail`
        group by `tbl_purchase_detail`.`purchase_id`
    ) as `t`
    on (`p`.`id` = `t`.`purchase_id`)
);

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_purchase_product as(
select pd.*, p.excel_date, p.book_date, p.delivery_date, p.received_date, p.supplier from tbl_purchase_detail pd
left join tbl_purchase p
on pd.purchase_id = p.id
);

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_shipment as

select `p`.*,
       `t`.`product_id_group` AS `product_id_group`
from
(   `tbl_shipment` as `p`
    left join
    (   select group_concat(`tbl_shipment_detail`.`product_id` separator ',') AS `product_id_group`,
               `tbl_shipment_detail`.`shipment_id` AS `shipment_id`
        from `tbl_shipment_detail`
        group by `tbl_shipment_detail`.`shipment_id`
    ) as `t`
    on (`p`.`id` = `t`.`shipment_id`)
);

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_shipment_product as(
select tsd.*,
ts.excel_date, ts.delivery_date, ts.signed_date, ts.payment_date, ts.store_id, ts.store, ts.carrier, ts.fba_no,
ts.route, ts.tracking_number, ts.status_delivery, ts.excel_id,
tsi.asin, tsi.fnsku
from tbl_shipment_detail tsd
left join tbl_shipment ts
on tsd.shipment_id = ts.id
left join tbl_sku_info tsi
on tsd.sku_id = tsi.id
where tsd.box != 'Plan'
);

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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