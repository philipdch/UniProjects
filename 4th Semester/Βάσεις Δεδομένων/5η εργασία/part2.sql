/* Find the average price we have to pay for listings with a given number of bedrooms
Output: 9 rows 
*/
select r.bedrooms, avg(extra.extra_price)::numeric(15,2)
from (select listing_id, ABS(p.guests_included - 10) * p.extra_people AS extra_price
	 from "Price" as p) as extra
join "Room" as r
on r.listing_id=extra.listing_id
where r.bedrooms>2
group by r.bedrooms
order by r.bedrooms asc;

/* Find percentage of each property type out of all listings along with average price for this type 
Output: 26 rows
*/
SELECT property_type, to_char((COUNT(property_type)::float / q1.total_listings * 100::float), 'fm990D99') as type_percentage, to_char(AVG(p.price), 'fm9999D00') as average_price
FROM (SELECT COUNT(id)::float as total_listings FROM "Listing") q1, "Listing"
JOIN "Price" p ON listing_id = id 
GROUP BY property_type, q1.total_listings;

/*Find for every host who has more than 2 listings, the minimum and maximum price 
Output: 1931 rows
 */
SELECT h_l.host_id, MIN(p.price), MAX(p.price) 
FROM (SELECT h.id as host_id, l.id as listing_id
	FROM "Host" h 
	JOIN "Listing" l ON l.host_id = h.id) h_l
JOIN "Price" p on h_l.listing_id = p.listing_id
GROUP BY h_l.host_id
HAVING COUNT(h_l.listing_id) >= 2
ORDER BY h_l.host_id;

/*Find for every listing whose price is less than 100 the total number of its provided amenities
Output: 2500 rows
*/
select q.id ,q.amenity_count, max(price)::numeric(15,2) 
from (select distinct l.id ,count(a_r.amenity_id) as amenity_count
	from "Listing" as l
	left outer join "Amenity-Room" as a_r
	on l.id=a_r.listing_id
	group by l.id) as q
join "Price" as p
on q.id=p.listing_id
group by q.amenity_count , q.id
HAVING max(price)::numeric(15,2) < 100
order by q.amenity_count DESC
LIMIT 2500;

/* Find density of listings which require a security deposit of less than 50$ in each neighbourhood
Output: 45 rows
*/
select g.properties_neighbourhood ,count(temp.listing_id) 
from ( SELECT l.listing_id, l.neighbourhood_cleansed
	   FROM "Location" l 
	   JOIN "Room" as r on r.listing_id = l.listing_id
	   WHERE security_deposit <= '$50.00')  temp 
	   right join "Geolocation" g on temp.neighbourhood_cleansed = g.properties_neighbourhood
group by g.properties_neighbourhood;