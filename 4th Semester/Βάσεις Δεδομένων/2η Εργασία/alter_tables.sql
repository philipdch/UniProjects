ALTER TABLE "Calendar"
ADD CONSTRAINT "FK_Calendar_Listings" FOREIGN KEY (listing_id) REFERENCES "Listings" (id);

ALTER TABLE "Geolocation"
ADD CONSTRAINT "FK_Geolocation_Neighbourhoods" FOREIGN KEY (properties_neighbourhood) REFERENCES "Neighbourhoods" (neighbourhood);

ALTER TABLE "Listings"
ADD CONSTRAINT "FK_Listings_Neighbourhoods" FOREIGN KEY (neighbourhood_cleansed) REFERENCES "Neighbourhoods" (neighbourhood);

ALTER TABLE "Listings-Summary"
ADD CONSTRAINT "FK_Listings-Summary_Listings" FOREIGN KEY (id) REFERENCES "Listings" (id);

ALTER TABLE "Listings-Summary"
ADD CONSTRAINT "FK_Listings-Summary_Neighbourhoods" FOREIGN KEY (neighbourhood) REFERENCES "Neighbourhoods" (neighbourhood);

ALTER TABLE "Reviews"
ADD CONSTRAINT "FK_Reviews_Listings" FOREIGN KEY (listing_id) REFERENCES "Listings" (id);

ALTER TABLE "Reviews-Summary"
ADD CONSTRAINT "FK_Reviews-Summary_Listings" FOREIGN KEY (listing_id) REFERENCES "Listings" (id);