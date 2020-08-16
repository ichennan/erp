-- drop on 2020-08-16 12:51:00
--
-- delete on 2020-08-16 12:51:00
--

create or replace view view_shipment_detail_by_product as

select `sd`.`product_id` AS `product_id`,
       `sd`.`shipment_id` AS `shipment_id`,
       sum(`sd`.`quantity`) AS `quantity`
from `tbl_shipment_detail` `sd`
group by `sd`.`product_id`, `sd`.`shipment_id`;