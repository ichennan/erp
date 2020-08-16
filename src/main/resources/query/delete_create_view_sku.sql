--
-- delete on 20200817 00:53:00
--

create or replace view view_sku as(
SELECT

if(`s`.`combine_count` > 1, concat(`s`.`sku`,'-',`s`.`combine_id`,'/',`s`.`combine_count`), `s`.`sku`) as `sku_desc`,
`s`.`id` AS `sku_id`,
`s`.`combine_id` AS `combine_id`,
`s`.`combine_count` AS `combine_count`,
`s`.`store_id` AS `store_id`,
`s`.`product_id` AS `product_id`,
`s`.`sku` AS `sku`,
`s`.`fnsku` AS `fnsku`,
`s`.`asin` AS `asin`,
vi.*

from (`tbl_sku_info` `s` left join `view_inventory` `vi` on((`s`.`product_id` = `vi`.`id`)))

)