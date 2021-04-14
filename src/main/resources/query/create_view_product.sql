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