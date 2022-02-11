/* Create table Neighbourhood from Listings' fields */
CREATE TABLE "Location"
AS SELECT id AS listing_id,
		   street,
		   neighbourhood,
		   neighbourhood_cleansed,
		   city,
		   state,
		   zipcode,
		   market,
		   smart_location,
		   country_code,
		   country,
		   latitude,
		   longitude,
		   is_location_exact
	FROM "Listing";
	
/* Drop columns from Listings */
ALTER TABLE "Listing"
	DROP COLUMN IF EXISTS street,
	DROP COLUMN IF EXISTS neighbourhood,
	DROP COLUMN IF EXISTS neighbourhood_cleansed,
	DROP COLUMN IF EXISTS city,
	DROP COLUMN IF EXISTS state,
	DROP COLUMN IF EXISTS zipcode,
	DROP COLUMN IF EXISTS market,
	DROP COLUMN IF EXISTS smart_location,
	DROP COLUMN IF EXISTS country_code,
	DROP COLUMN IF EXISTS country,
	DROP COLUMN IF EXISTS latitude,
	DROP COLUMN IF EXISTS longitude,
	DROP COLUMN IF EXISTS is_location_exact;
	
/* Add foreign key to Location referencing "Listings".id */
ALTER TABLE "Location" 
ADD CONSTRAINT "fk_Location_Listing" FOREIGN KEY(listing_id) REFERENCES "Listing" (id);

/*Remove connection between listing and neighbourhoods */
ALTER TABLE "Listing"
DROP CONSTRAINT "FK_Listings_Neighbourhoods" ;

/* Add foreign key to Location referencing "Neighbourhoods".neighbourhood */
ALTER TABLE "Location"
ADD CONSTRAINT "fk_Location_Neighbourhood" FOREIGN KEY (neighbourhood_cleansed) REFERENCES "Neighbourhood" (neighbourhood);

