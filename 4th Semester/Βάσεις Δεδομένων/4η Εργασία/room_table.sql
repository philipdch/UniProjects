/* Create table Room from Listings' fields */
CREATE TABLE "Room"
AS SELECT id AS listing_id,
		  accommodates,
		  bathrooms,
		  bedrooms,
		  beds,
		  bed_type,
		  amenities,
		  square_feet,
		  price,
		  weekly_price,
		  monthly_price,
		  security_deposit
	FROM "Listing";
	
/* Drop columns from Listings */
ALTER TABLE "Listing"
	DROP COLUMN IF EXISTS accommodates,
	DROP COLUMN IF EXISTS bathrooms,
	DROP COLUMN IF EXISTS bedrooms,
	DROP COLUMN IF EXISTS beds,
	DROP COLUMN IF EXISTS bed_type,
	DROP COLUMN IF EXISTS amenities,
	DROP COLUMN IF EXISTS square_feet,
	DROP COLUMN IF EXISTS price, 
	DROP COLUMN IF EXISTS weekly_price,
	DROP COLUMN IF EXISTS monthly_price,
	DROP COLUMN IF EXISTS security_deposit;
	
/* Add foreign key to Room referencing "Listings".id */
ALTER TABLE "Room" 
ADD CONSTRAINT "fk_Room_Listing" FOREIGN KEY(listing_id) REFERENCES "Listing" (id);