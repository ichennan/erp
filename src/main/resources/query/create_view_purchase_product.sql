create or replace view view_purchase_product as(
select pd.*, p.excel_date, p.book_date, p.delivery_date, p.received_date, p.supplier from tbl_purchase_detail pd
left join tbl_purchase p
on pd.purchase_id = p.id
);