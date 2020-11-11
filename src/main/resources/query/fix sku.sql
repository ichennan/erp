select distinct sku, id from tbl_sku_info where sku in(
select merchant_sku from excel_fba_pack_list where sku_id in(
select distinct(sku_id) from tbl_shipment_detail where sku_id not in
(select id from tbl_sku_info)));

select distinct(sku_id) from tbl_shipment_detail where sku_id not in
(select id from tbl_sku_info);

select distinct sku_id, merchant_sku from excel_fba_pack_list where sku_id in(
select distinct(sku_id) from tbl_shipment_detail where sku_id not in
(select id from tbl_sku_info));

insert into tbl_shipment_detail_bk select * from tbl_shipment_detail;

update tbl_shipment_detail set sku_id = 230116 where sku_id = 230084;
update tbl_shipment_detail set sku_id = 230076 where sku_id = 230024;
update tbl_shipment_detail set sku_id = 230119 where sku_id = 230087;
update tbl_shipment_detail set sku_id = 230066 where sku_id = 210008;
update tbl_shipment_detail set sku_id = 230068 where sku_id = 210007;
update tbl_shipment_detail set sku_id = 230095 where sku_id = 210001;
update tbl_shipment_detail set sku_id = 230097 where sku_id = 210003;
update tbl_shipment_detail set sku_id = 230093 where sku_id = 210002;
update tbl_shipment_detail set sku_id = 230127 where sku_id = 230102;
update tbl_shipment_detail set sku_id = 230125 where sku_id = 230103;
update tbl_shipment_detail set sku_id = 230072 where sku_id = 210011;
update tbl_shipment_detail set sku_id = 230074 where sku_id = 210013;
update tbl_shipment_detail set sku_id = 230082 where sku_id = 230019;


update tbl_shipment_detail set sku_id = 230117 where sku_id = 230085;
update tbl_shipment_detail set sku_id = 230117 where sku_id = 230045;

select * from excel_fba_pack_list where sku_id = 230045;
// 230045 100119 2PACK-hx03Nsc08

select * from excel_fba_pack_list where sku_id = 230085;
// 230085 100119 2PACK-hx03Nsc08

select * from excel_fba_pack_list where merchant_sku = '2PACK-hx03Nsc08';
230085 230044 230117
100119 100063 100119

select * from tbl_product where id in (100119, 100063);

select * from tbl_sku_info where combine_count = 2;

///////

