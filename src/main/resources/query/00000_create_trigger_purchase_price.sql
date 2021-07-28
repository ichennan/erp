DROP TRIGGER trigger_purchase_price;

CREATE TRIGGER trigger_purchase_price
AFTER update ON tbl_product
FOR EACH ROW
    IF NEW.purchase_price != OLD.purchase_price Then
        INSERT INTO log_purchase_price
        SET product_id = NEW.id,
            purchase_price = NEW.purchase_price,
            create_date = NOW();
        END IF