CREATE OR REPLACE FUNCTION update_rows() RETURNS TRIGGER AS $update_count_trigger$
BEGIN 
	IF TG_OP ='INSERT' THEN  
		IF EXISTS (SELECT 1 FROM "Host_Copy" AS h WHERE h.id = NEW.host_id) THEN 
			UPDATE "Host_Copy" 
			SET listings_count = listings_count+1
			WHERE "Host_Copy".id = NEW.host_id;
			RETURN NEW;
		else
			INSERT INTO "Host_Copy" (id,listings_count)
			VALUES(NEW.host_id, 1);
			RETURN NEW;
		END IF;
	ELSIF TG_OP='DELETE' THEN  
				UPDATE "Host_Copy" 
				SET listings_count = listings_count-1
				WHERE "Host_Copy".id = OLD.host_id;
		RETURN new;
	END IF;
	RETURN new;
END; 
$update_count_trigger$
LANGUAGE plpgsql;

CREATE TRIGGER "update_count_trigger" 
BEFORE INSERT OR DELETE ON "Listing_Copy"
FOR EACH ROW
EXECUTE PROCEDURE update_rows();

/* Trigger that automatically updates Room table's price field when Price table's price field is updated in order for the data to match
*/
CREATE OR REPLACE FUNCTION update_price() RETURNS TRIGGER AS $update_price_trigger$
BEGIN
		UPDATE "Room_Copy" 
		SET  price = CAST(NEW.price AS varchar)
		WHERE listing_id = NEW.listing_id;
		RETURN NEW;
END;
$update_price_trigger$
LANGUAGE plpgsql;

CREATE TRIGGER "update_price_trigger"
AFTER UPDATE 
ON "Price_Copy"
FOR EACH ROW
EXECUTE PROCEDURE update_price();

