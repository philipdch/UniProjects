/*Query 1*/
SELECT extract(year from release_date),count(id) FROM "Movies_Metadata"
GROUP BY extract(year from release_date)

/*Query 2*/
SELECT json_array_elements(genres)->>'name' as genre, COUNT(id) as total_movies 
FROM "Movies_Metadata"
GROUP BY genre;

/*Query 3*/
SELECT json_array_elements(genres)->>'name' as genre, extract(year from release_date) as year, COUNT(id) as total_movies 
FROM "Movies_Metadata"
GROUP BY genre, year
ORDER BY year; 

/*Query 4*/
SELECT json_array_elements(genres)->>'name' as genre, to_char(AVG(rating), 'fm999D00') as average_rating_per_genre
FROM "Movies_Metadata" m
JOIN "Ratings" r ON r.movieid = m.id
GROUP BY genre

/*Query 5*/
SELECT userid, COUNT(userid) as user_reatings_count
FROM "Ratings"
GROUP BY userid;

/*Query 6*/
SELECT userid, to_char(AVG(rating), 'fm999D00') as average_rating
FROM "Ratings"
GROUP BY userid

/*Create view*/
create view "User_Ratings" as (SELECT userid, to_char(AVG(rating), 'fm999D00')::float as average_rating ,count (userid)
								FROM "Ratings" 
								GROUP BY userid)