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