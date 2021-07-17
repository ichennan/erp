create or replace view view_oversea as

select `p`.*,
       `t`.`product_id_group` AS `product_id_group`
from
    (`tbl_oversea` as `p`
        left join
        (select group_concat(concat(`tbl_oversea_detail`.`product_id`, '-', `tbl_oversea_detail`.`quantity`) separator ',')
            AS `product_id_group`,
            `tbl_oversea_detail`.`oversea_id` AS `oversea_id`
        from `tbl_oversea_detail`
        group by `tbl_oversea_detail`.`oversea_id`
    ) as `t`
    on (`p`.`id` = `t`.`oversea_id`)
);