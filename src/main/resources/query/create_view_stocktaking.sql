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