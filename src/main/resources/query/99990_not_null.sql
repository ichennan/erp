update tbl_oversea set amount = 0 where amount is null;
update tbl_oversea set unit_price = 0 where unit_price is null;
update tbl_oversea set weight = 0 where weight is null;
update tbl_oversea set charge_weight = 0 where charge_weight is null;
update tbl_oversea set oversea_amount = 0 where oversea_amount is null;

update tbl_oversea_detail set quantity = 0 where quantity is null;
update tbl_oversea_detail set weight = 0 where weight is null;

update tbl_shipment set amount = 0 where amount is null;
update tbl_shipment set unit_price = 0 where unit_price is null;
update tbl_shipment set weight = 0 where weight is null;
update tbl_shipment set charge_weight = 0 where charge_weight is null;

update tbl_shipment_detail set weight = 0 where weight is null;

update tbl_purchase set amount = 0 where amount is null;
update tbl_purchase set freight = 0 where freight is null;

update tbl_purchase_detail set unit_price = 0 where unit_price is null;
update tbl_purchase_detail set received_quantity = 0 where received_quantity is null;