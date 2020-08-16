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