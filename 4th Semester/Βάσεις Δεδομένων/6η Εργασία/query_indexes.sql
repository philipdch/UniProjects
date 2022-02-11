/* Indices for table Price */
/*1 */
CREATE INDEX price_guests_people ON "Price" (guests_included,extra_people);

/*2 */
CREATE INDEX price_price ON "Price" (price);

/*3 */
CREATE INDEX price_guests_people_1 ON "Price" (guests_included,extra_people,listing_id);

/*4 */
CREATE INDEX price_listing_id ON "Price" (listing_id);

/*5 */
CREATE INDEX Price_price_guests_included ON "Price" (price, guests_included)
WHERE guests_included > 5 AND price > 40;

/*6 */
CREATE INDEX price_price_1 ON "Price" (price,listing_id);

/* Indices for table Room */
/*7 */
CREATE INDEX room_bedrooms ON "Room" (bedrooms)
WHERE bedrooms>2;

/*8 */
CREATE INDEX room_bedrooms_2 ON "Room" (bedrooms,listing_id)
WHERE bedrooms>2;

/*9 */
CREATE INDEX room_security_deposit ON "Room" (security_deposit)
WHERE security_deposit BETWEEN '$0' AND'$50';

/*10 */
CREATE INDEX room_security_deposit_1 ON "Room" (security_deposit,listing_id)
WHERE security_deposit BETWEEN '$0' AND '$50';

/*11 */
CREATE INDEX room_listing_id ON "Room" (listing_id);

/*Index for table Location*/
/*12 */
CREATE INDEX location_listing_id ON "Location" (listing_id);
 
/* Index for table Geolocation */
/*13 */
CREATE INDEX geolocation_properties_neighbourhood ON "Geolocation" (properties_neighbourhood);

/* Indices for table Amenity-Room */
/*14 */
CREATE INDEX a_r_listing_id_amenity_id ON "Amenity-Room" (listing_id,amenity_id);

/*15 */
CREATE INDEX a_r_amenity_id ON "Amenity-Room" (amenity_id); 

/*16 */
CREATE INDEX a_r_listing_id ON "Amenity-Room" (listing_id); 

/*Indices for table Listing */
/*17 */
CREATE INDEX Listing_host_id ON "Listing" (host_id);

/*18 */
CREATE INDEX Listing_host_id_id ON "Listing" (host_id,id);

/*19 */
CREATE INDEX listing_property_type_1 ON "Listing" (property_type,id);

/*20 */
CREATE INDEX listing_property_type ON "Listing" (property_type);