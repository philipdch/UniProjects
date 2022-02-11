/* Create table Price from Listings' fields */
CREATE TABLE "Price"
AS SELECT id AS listing_id,
		  price,
		  weekly_price,
		  monthly_price,
		  security_deposit,
		  cleaning_fee,
		  guests_included,
		  extra_people,
		  minimum_nights,
		  maximum_nights,
		  minimum_minimum_nights,
		  maximum_minimum_nights,
		  minimum_maximum_nights,
		  maximum_maximum_nights,
		  minimum_nights_avg_ntm,
		  maximum_nights_avg_ntm
	FROM "Listing";
	
/* Drop columns from Listings */
ALTER TABLE "Listing"
	DROP COLUMN IF EXISTS price,
	DROP COLUMN IF EXISTS weekly_price,
	DROP COLUMN IF EXISTS monthly_price,
	DROP COLUMN IF EXISTS security_deposit,
	DROP COLUMN IF EXISTS cleaning_fee,
	DROP COLUMN IF EXISTS guests_included,
	DROP COLUMN IF EXISTS extra_people,
	DROP COLUMN IF EXISTS minimum_nights,
	DROP COLUMN IF EXISTS maximum_nights,
	DROP COLUMN IF EXISTS minimum_minimum_nights,
	DROP COLUMN IF EXISTS maximum_minimum_nights,
	DROP COLUMN IF EXISTS minimum_maximum_nights,
	DROP COLUMN IF EXISTS maximum_maximum_nights,
	DROP COLUMN IF EXISTS minimum_nights_avg_ntm,
	DROP COLUMN IF EXISTS maximum_nights_avg_ntm;
	
/* Add foreign key to Room referencing "Listings".id */
ALTER TABLE "Price" 
ADD CONSTRAINT "fk_Price_Listing" FOREIGN KEY(listing_id) REFERENCING "Listing" (id);

/*convert types*/
ALTER TABLE "Price" 
ALTER COLUMN price TYPE  NUMERIC(15,2) USING(REGEXP_REPLACE(price,'[$,]','','g')::NUMERIC(15,2));

ALTER TABLE "Price" 
ALTER COLUMN monthly_price TYPE  NUMERIC(15,2) USING (REGEXP_REPLACE(monthly_price,'[$,]','','g')::NUMERIC(15,2));


ALTER TABLE "Price" 
ALTER COLUMN weekly_price TYPE NUMERIC(15,2) USING (REGEXP_REPLACE(weekly_price,'[$,]','','g')::NUMERIC(15,2));

ALTER TABLE "Price" 
ALTER COLUMN security_deposit TYPE NUMERIC(15,2) USING (REGEXP_REPLACE(security_deposit,'[$,]','','g')::NUMERIC(15,2));

ALTER TABLE "Price" 
ALTER COLUMN cleaning_fee TYPE NUMERIC(15,2) USING (REGEXP_REPLACE(cleaning_fee,'[$,]','','g')::NUMERIC(15,2));


ALTER TABLE "Price" 
ALTER COLUMN extra_people TYPE NUMERIC(15,2) USING (REGEXP_REPLACE(extra_people,'[$,]','','g')::NUMERIC(15,2));



