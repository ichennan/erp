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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_product_stock as(
select p.*
       , ifnull(inb.inbound_product_quantity, 0) as inbound_quantity
       , ifnull(outb.outbound_product_quantity, 0) as outbound_quantity
       , (ifnull(inb.inbound_product_quantity, 0) - ifnull(outb.outbound_product_quantity, 0)) as stock_quantity
       , ifnull(tivv.max_date_received, '') as latest_date_received
from tbl_product p

left join
(select product_id, sum(product_quantity) as inbound_product_quantity from tbl_inbound_detail
where is_delete is null or is_delete <> 1
group by product_id) as inb
on p.id = inb.product_id

left join
(select product_id, sum(product_quantity) as outbound_product_quantity from tbl_outbound_detail
where is_delete is null or is_delete <> 1
group by product_id) as outb
on p.id = outb.product_id

left join
(select max(date_received) as max_date_received, product_id from
(select ti.date_received, tid.product_id, tid.product_quantity from tbl_inbound_detail tid
left join tbl_inbound ti
on tid.inbound_id = ti.id) as tiv
group by product_id) as tivv
on p.id = tivv.product_id
);

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_oversea as

select `p`.*,
       `t`.`product_id_group` AS `product_id_group`
from
    (`tbl_oversea` as `p`
        left join
    (select group_concat(concat(`tbl_oversea_detail`.`product_id`, '|', `tbl_oversea_detail`.`quantity`) separator ',')
                                              AS `product_id_group`,
            `tbl_oversea_detail`.`oversea_id` AS `oversea_id`
     from `tbl_oversea_detail`
     group by `tbl_oversea_detail`.`oversea_id`
    ) as `t`
    on (`p`.`id` = `t`.`oversea_id`)
);

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_purchase as

select `p`.*,
       `t`.`product_id_group` AS `product_id_group`
from
    (`tbl_purchase` as `p`
        left join
    (select group_concat(concat(`tbl_purchase_detail`.`product_id`, '|', `tbl_purchase_detail`.`received_quantity`) separator ',')
                                                AS `product_id_group`,
            `tbl_purchase_detail`.`purchase_id` AS `purchase_id`
     from `tbl_purchase_detail`
     group by `tbl_purchase_detail`.`purchase_id`
    ) as `t`
    on (`p`.`id` = `t`.`purchase_id`)
);

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_shipment as

select `p`.*,
       `t`.`product_id_group` AS `product_id_group`
from
    (`tbl_shipment` as `p`
        left join
    (select group_concat(concat(`tbl_shipment_detail`.`product_id`, '|', `tbl_shipment_detail`.`quantity`) separator ',')
                                                AS `product_id_group`,
            `tbl_shipment_detail`.`shipment_id` AS `shipment_id`
     from `tbl_shipment_detail` where box != 'Plan'
     group by `tbl_shipment_detail`.`shipment_id`
    ) as `t`
    on (`p`.`id` = `t`.`shipment_id`)
);

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_sku_product as(
SELECT
if(s.combine_count > 1, concat(s.sku, '组合', s.combine_id, '/', s.combine_count), s.sku) as sku_desc,
s.*, p.name, p.sn, p.color, p.size, p.purchase_price, p.subject
from tbl_sku_info as s
left join tbl_product p
on s.product_id = p.id)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_stocktaking as

select `p`.*,
       `t`.`product_id_group` AS `product_id_group`
from
    (`tbl_stocktaking` as `p`
        left join
    (select group_concat(concat(`tbl_stocktaking_detail`.`product_id`, '|', `tbl_stocktaking_detail`.`adjustment_quantity`) separator ',')
                                                      AS `product_id_group`,
            `tbl_stocktaking_detail`.`stocktaking_id` AS `stocktaking_id`
     from `tbl_stocktaking_detail`
     group by `tbl_stocktaking_detail`.`stocktaking_id`
    ) as `t`
    on (`p`.`id` = `t`.`stocktaking_id`)
);

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_purchase_product as(
select pd.*, p.excel_date, p.book_date, p.delivery_date, p.received_date, p.supplier from tbl_purchase_detail pd
left join tbl_purchase p
on pd.purchase_id = p.id
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

create or replace view view_sku_stock as(
select sp.*
       , ifnull(inb.inbound_product_quantity, 0) as inbound_quantity
       , ifnull(outb.outbound_product_quantity, 0) as outbound_quantity
       , (ifnull(inb.inbound_product_quantity, 0) - ifnull(outb.outbound_product_quantity, 0)) as stock_quantity
       , ifnull(outb_sku.outbound_product_quantity, 0) as outbound_sku_quantity
       , ifnull(tivv.max_date_received, '') as latest_date_received
       , ifnull(tovv.max_date_sent, '') as latest_date_sent
from view_sku_product sp

left join
(select product_id, sum(product_quantity) as inbound_product_quantity from tbl_inbound_detail
where is_delete is null or is_delete <> 1
group by product_id) as inb
on sp.product_id = inb.product_id

left join
(select product_id, sum(product_quantity) as outbound_product_quantity from tbl_outbound_detail
where is_delete is null or is_delete <> 1
group by product_id) as outb
on sp.product_id = outb.product_id

left join
(select sum(product_quantity) as outbound_product_quantity, sku_id from tbl_outbound_detail
where is_delete is null or is_delete <> 1
group by sku_id) as outb_sku
on sp.id = outb_sku.sku_id

left join
(select max(date_received) as max_date_received, product_id from
(select ti.date_received, tid.product_id, tid.product_quantity from tbl_inbound_detail tid
left join tbl_inbound ti
on tid.inbound_id = ti.id) as tiv
group by product_id) as tivv
on sp.product_id = tivv.product_id

left join
(select max(date_sent) as max_date_sent, sku_id from
(select too.date_sent, tod.product_quantity, tod.sku_id from tbl_outbound_detail tod
left join tbl_outbound too
on tod.outbound_id = too.id) as tiv
group by sku_id) as tovv
on sp.id = tovv.sku_id
);

;;;;;;;;;;;;;;;;;;;;;;;;

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


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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
(select sku_id, sum(quantity) as sumSkuOverseaQuantity from tbl_oversea_detail where fba_no = '' group by sku_id)
as t_oversea
on p.id = t_oversea.sku_id

;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

--
-- based on view view_inventory_product
--

create or replace view view_product as
select p.*,
       ifnull(s.sku_count, 0) as sku_count,
       ifnull(vip.product_inventory_quantity, 0) as inventory_quantity
from tbl_product p
left join
    (select product_id, count(1) as sku_count from tbl_sku_info where sku is not null group by product_id) as s
on p.id = s.product_id
left join view_inventory_product vip
on p.id = vip.id

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
vp.sum_product_oversea_quantity,
vp.sum_product_stocktaking_quantity,
vp.product_inventory_quantity,
vs.sum_sku_shipment_quantity,
vs.sum_sku_shipment_ontheway_quantity,
vs.sum_sku_shipment_arrived_quantity,
vs.sum_sku_oversea_quantity

from `tbl_sku_info` as `s`

left join `view_inventory_product` as `vp`
on (`s`.`product_id` = `vp`.`id`)

left join `view_inventory_sku` as `vs`
on (`s`.`id` = `vs`.`id`)

;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

-- DROP EVENT event_snapshot_sku_inventory;
-- CREATE EVENT event_snapshot_sku_inventory
-- ON SCHEDULE EVERY 1 DAY
-- STARTS '2021-07-28 23:45:00'
-- ON COMPLETION PRESERVE
-- COMMENT 'Sku Snapshot Every Mid-Night 2345'
-- DO
--
-- insert into snapshot_sku(
-- snapshot_date
--
-- ,sku_id
-- ,product_id
-- ,sum_product_purchase_quantity
-- ,sum_product_shipment_quantity
-- ,sum_product_shipment_ontheway_quantity
-- ,sum_product_packet_quantity
-- ,sum_product_shipment_arrived_quantity
-- ,sum_product_oversea_quantity
-- ,sum_product_stocktaking_quantity
-- ,product_inventory_quantity
-- ,sum_sku_shipment_quantity
-- ,sum_sku_shipment_ontheway_quantity
-- ,sum_sku_shipment_arrived_quantity
-- ,sum_sku_oversea_quantity
-- )
--
-- select
-- DATE_FORMAT(NOW(),'%Y%m%d')
--
-- ,id
-- ,product_id
-- ,sum_product_purchase_quantity
-- ,sum_product_shipment_quantity
-- ,sum_product_shipment_ontheway_quantity
-- ,sum_product_packet_quantity
-- ,sum_product_shipment_arrived_quantity
-- ,sum_product_oversea_quantity
-- ,sum_product_stocktaking_quantity
-- ,product_inventory_quantity
-- ,sum_sku_shipment_quantity
-- ,sum_sku_shipment_ontheway_quantity
-- ,sum_sku_shipment_arrived_quantity
-- ,sum_sku_oversea_quantity
-- from view_sku_info
--
-- ;
