create table "Amenity"(
	amenity_id serial,
	amenity_name varchar(100) UNIQUE
	PRIMARY KEY(amenity_id)
);

Insert into "Amenity" (amenity_name)  (
	SELECT * 
	FROM ( SELECT DISTINCT regexp_split_to_table(amenities, '[{},"]') as amenity 
		   FROM "Room" ) AS q
	WHERE q.amenity != ''
);

CREATE TABLE "Amenity-Room" AS 
	(SELECT q.listing_id, a.amenity_id FROM "Amenity" a
	JOIN (SELECT listing_id, regexp_split_to_table(amenities, '[{},"]') as amenity FROM "Room") q
	ON q.amenity = a.amenity_name
	ORDER BY listing_id, amenity_id);
	
ALTER TABLE "Amenity-Room"
ADD CONSTRAINT "fk_Amenity-Room_Amenity" FOREIGN KEY( amenity_id) REFERENCES "Amenity"(amenity_id),
ADD CONSTRAINT "fk_Amenity-Room_Listing" FOREIGN KEY(listing_id) REFERENCES "Listing"(id)
DROP COLUMN IF EXISTS amenities;
