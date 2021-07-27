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