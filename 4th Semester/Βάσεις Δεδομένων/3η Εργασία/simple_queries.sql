/* 1)Find total number of listings in Exarcheia which allow smoking, along with the average and maximum
		prices one can pay for these amenities
Output: 1 row 
*/
SELECT to_char(AVG(regexp_replace(l.price, '[$,]', '', 'g')::numeric(15,2)), '99999D99') AS average_price ,to_char(MAX(regexp_replace(l.price, '[$,]', '', 'g')::numeric(15,2)), '99999D99') max_price, COUNT(l.id) total_listings
FROM   "Listings" l
WHERE  l.amenities LIKE '%Smoking allowed%' AND l.neighbourhood = 'Exarcheia'

/* 2)Selects the last review written for each listing 
Output: 9003 rows 
*/
SELECT l.id, l.name, l.host_name AS host, r.reviewer_name AS reviewer, r.comments
FROM "Listings" l
INNER JOIN "Reviews" r ON listing_id = l.id
WHERE date = DATE(last_review)
LIMIT 2000

/* 3)Find total price of every listing which provides breakfast for dates 23-3-2020 to 28-3-2020 
Output: 1017 rows 
*/
SELECT l.id, l.listing_url, SUM(regexp_replace(c.price, '[$,]', '', 'g')::numeric(15,2))
FROM "Listings" l
INNER JOIN "Calendar" c ON l.id = listing_id
WHERE date BETWEEN '2020-03-23' AND '2020-03-28' 
		AND available = 't' 
		AND l.amenities LIKE '%Breakfast%'
GROUP BY l.id
ORDER BY id

/* 4)Finds for every neighbourhood the average price of all the listings located there
Output: 45 rows
*/
SELECT n.neighbourhood, to_char(AVG(regexp_replace(l.price, '[$,]', '', 'g')::numeric(15,2)), '99999999D99') AS average_price
FROM "Neighbourhoods" n
INNER JOIN "Listings" l ON neighbourhood_cleansed = n.neighbourhood
GROUP BY n.neighbourhood
ORDER BY average_price DESC

/* 5)Find total number of listings in each geographical area (If there are any)
Output: 45 rows
*/
SELECT  view1.count, g.*
FROM (SELECT COUNT(l.id), n.neighbourhood
	  FROM "Listings" l
	  RIGHT OUTER JOIN "Neighbourhoods" n ON n.neighbourhood = l.neighbourhood_cleansed
	  GROUP BY n.neighbourhood) view1
INNER JOIN "Geolocation" g ON g.properties_neighbourhood = view1.neighbourhood
ORDER BY count DESC

/* 6)Find all listings without any reviews 
Output: 2559 rows
*/
SELECT r.comments, l.id AS listing_id FROM "Reviews" r
RIGHT OUTER JOIN "Listings" l ON r.listing_id = l.id
WHERE r.comments IS NULL 

/* 7)Show for each neighbourhood all the reviews (if there are any) written for all listings located there
Output: 5000 rows
*/
SELECT q1.neighbourhood, r.comments
FROM (SELECT l.id, n.neighbourhood
	  FROM "Listings" l
	  INNER JOIN "Neighbourhoods" n ON l.neighbourhood_cleansed = n.neighbourhood) q1
LEFT OUTER JOIN "Reviews" r ON r.listing_id = q1.id 
LIMIT 5000

/* 8)Shows for each host the number of listings they have registered
Output: 6363 rows 
*/
SELECT DISTINCT host_name, host_id, calculated_host_listings_count AS listings_count
FROM "Listings"
ORDER BY calculated_host_listings_count DESC

/* 9)Select all reviews for listings in Kerameikos whose price per night is between 20-50$*/
SELECT r.id, r.reviewer_name, l.price  
FROM   "Reviews" r
INNER JOIN   "Listings" l
ON     r.listing_id=l.id
WHERE  l.neighbourhood='Kerameikos' AND l.price BETWEEN '20$' AND '50$'

/* 10)Select the maximum number of nights one can stay in a listing in Athens between '2019-01-01' and '2020-09-01'
OUTPUT: 1376 rows
*/
SELECT DISTINCT l.id,c.maximum_nights 
FROM   "Calendar" c
INNER JOIN   "Listings" l
ON     c.listing_id=l.id
WHERE  l.city='Athens' AND c.available='t' AND DATE(c.date) BETWEEN DATE('2019-01-01') AND DATE('2020-09-01')
ORDER BY c.maximum_nights

/* 11)Shows the id, url and comments for the listing with the most reviews 
 Output: 708 rows
 */
SELECT l.id, l.listing_url, r.comments
FROM "Reviews" r
INNER JOIN "Listings" l
ON l.id=r.listing_id
WHERE l.number_of_reviews = (SELECT MAX(l1.number_of_reviews)
	   FROM "Listings" l1)

/* 12)Find total number of reviews for which the listing's price is less than 50
Output: 1 row 
*/
SELECT COUNT(r.id)
FROM "Reviews" r
INNER JOIN "Listings" l
ON l.id=r.listing_id
WHERE regexp_replace(l.price, '[$,]', '', 'g')::numeric(15,2) < 50