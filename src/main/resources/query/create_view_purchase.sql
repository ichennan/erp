create or replace view view_purchase as
	select `p`.`id`                  AS `id`,
       `p`.`book_date`           AS `book_date`,
       `p`.`delivery_date`       AS `delivery_date`,
       `p`.`received_date`       AS `received_date`,
       `p`.`create_date`         AS `create_date`,
       `p`.`update_date`         AS `update_date`,
       `p`.`create_user`         AS `create_user`,
       `p`.`update_user`         AS `update_user`,
       `p`.`freight`             AS `freight`,
       `p`.`supplier`            AS `supplier`,
       `p`.`status_received_all` AS `status_received_all`,
       `p`.`remark`              AS `remark`,
       `p`.`excel_date`          AS `excel_date`,
       `p`.`amount`              AS `amount`,
       `t`.`product_id_group`    AS `product_id_group`
from (`tbl_purchase` `p`
         left join (select group_concat(`tbl_purchase_detail`.`product_id` separator
                                        ',')                                 AS `product_id_group`,
                           `tbl_purchase_detail`.`purchase_id` AS `purchase_id`
                    from `tbl_purchase_detail`
                    group by `tbl_purchase_detail`.`purchase_id`) `t`
                   on ((`p`.`id` = `t`.`purchase_id`)));