create or replace view view_shipment_product as(
select tsd.*,
ts.excel_date, ts.delivery_date, ts.signed_date, ts.payment_date, ts.store_id, ts.store, ts.carrier, ts.fba_no,
ts.route, ts.tracking_number, ts.status_delivery, ts.excel_id,
tsi.asin, tsi.sku, tsi.fnsku
from tbl_shipment_detail tsd
left join tbl_shipment ts
on tsd.shipment_id = ts.id
left join tbl_sku_info tsi
on tsd.sku_id = tsi.id
);