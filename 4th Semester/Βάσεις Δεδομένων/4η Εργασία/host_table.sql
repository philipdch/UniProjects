/* Create Host table from Listings */
CREATE TABLE "Host"
AS SELECT DISTINCT host_id AS id, 
		  host_url AS url,
		  host_name AS name, 
		  host_since AS since, 
		  host_location AS location ,
		  host_about AS about, 
		  host_response_time AS response_time, 
		  host_response_rate AS response_rate,
		  host_acceptance_rate AS acceptance_rate, 
		  host_is_superhost AS is_superhost, 
		  host_thumbnail_url AS thumbnail_url,
		  host_picture_url AS picture_url, 
		  host_neighbourhood AS neighbourhood,
		  host_listings_count AS listings_count, 
		  host_total_listings_count AS total_listings_count, 
		  host_verifications AS verifications,
		  host_has_profile_pic AS has_profile_pic,
		  host_identity_verified AS identity_verified,
		  calculated_host_listings_count AS calculated_listings_count
	FROM "Listings";

/* Drop copied columns from Listings */
ALTER TABLE "Listings"
	DROP COLUMN IF EXISTS host_url,
	DROP COLUMN IF EXISTS host_name,
	DROP COLUMN IF EXISTS host_since,
	DROP COLUMN IF EXISTS host_location,
	DROP COLUMN IF EXISTS host_about,
	DROP COLUMN IF EXISTS host_response_time,
	DROP COLUMN IF EXISTS host_response_rate,
	DROP COLUMN IF EXISTS host_acceptance_rate,
	DROP COLUMN IF EXISTS host_is_superhost,
	DROP COLUMN IF EXISTS host_thumbnail_url,
	DROP COLUMN IF EXISTS host_picture_url,
	DROP COLUMN IF EXISTS host_neighbourhood,
	DROP COLUMN IF EXISTS host_listings_count,
	DROP COLUMN IF EXISTS host_total_listings_count,
	DROP COLUMN IF EXISTS host_verifications,
	DROP COLUMN IF EXISTS host_has_profile_pic,
	DROP COLUMN IF EXISTS host_identity_verified,
	DROP COLUMN IF EXISTS calculated_host_listings_count;

/* Add primary key to Host */
ALTER TABLE "Host"
ADD PRIMARY KEY(id);

/* add foreign key to Listings referencing host id */
ALTER TABLE "Listing"
ADD CONSTRAINT "fk_Listing_Host" FOREIGN KEY(host_id) REFERENCES "Host" (id);

