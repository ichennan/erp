create or replace view view_shipment as
	select `p`.`id`               AS `id`,
       `p`.`delivery_date`    AS `delivery_date`,
       `p`.`create_date`      AS `create_date`,
       `p`.`update_date`      AS `update_date`,
       `p`.`create_user`      AS `create_user`,
       `p`.`update_user`      AS `update_user`,
       `p`.`remark`           AS `remark`,
       `p`.`carrier`          AS `carrier`,
       `p`.`route`            AS `route`,
       `p`.`fba_no`           AS `fba_no`,
       `p`.`unit_price`       AS `unit_price`,
       `p`.`weight`           AS `weight`,
       `p`.`charge_weight`    AS `charge_weight`,
       `p`.`amount`           AS `amount`,
       `p`.`payment_date`     AS `payment_date`,
       `p`.`signed_date`      AS `signed_date`,
       `p`.`store`            AS `store`,
       `p`.`status_delivery`  AS `status_delivery`,
       `p`.`box_count`        AS `box_count`,
       `p`.`tracking_number`  AS `tracking_number`,
       `p`.`store_id`  AS `store_id`,
       `t`.`product_id_group` AS `product_id_group`
from (`tbl_shipment` `p`
         left join (select group_concat(`tbl_shipment_detail`.`product_id` separator
                                        ',')                                 AS `product_id_group`,
                           `tbl_shipment_detail`.`shipment_id` AS `shipment_id`
                    from `tbl_shipment_detail`
                    group by `tbl_shipment_detail`.`shipment_id`) `t`
                   on ((`p`.`id` = `t`.`shipment_id`)));