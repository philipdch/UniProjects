/*create Credits table*/
create table "Credits"(
   "cast" text,
   crew text,
   id int
);

/*create Keywords table*/
create table "Keywords"(
   id int,
   keywords text
);

/*create Links table*/
create table "Links"(
   movieId int,
   imdbId int,
   tmdbId int
);

/*create Movies_Metadata table*/
create table "Movies_Metadata"(
   adult varchar(10),
   belongs_to_collection varchar(190),
   budget int,
   genres varchar(270),
   homepage varchar(250),
   id int,
   imdb_id varchar(10),
   original_language varchar(10),
   original_title varchar(110),
   overview varchar(1000),
   popularity varchar(10),
   poster_path varchar(40),
   production_companies varchar(1260),
   production_countries varchar(1040),
   release_date date,
   revenue bigint,
   runtime varchar(10),
   spoken_languages varchar(770),
   status varchar(20),
   tagline varchar(300),
   title varchar(110),
   video varchar(10),
   vote_average varchar(10),
   vote_count int
);


/*create Ratings table*/
create table "Ratings"(
   userId int,
   movieId int,
   rating double precision,
   timestamp int
);

/*create Ratings_Small table*/
create table "Ratings_Small"(
   userId int,
   movieId int,
   rating double precision,
   timestamp int
);


/*Convert Movies_Metadata field "genres" to JSON field, so that it's values are easily accessible*/
UPDATE "Movies_Metadata"
SET "genres" = REPLACE("genres", E'\'', E'\"');
ALTER TABLE "Movies_Metadata"
ALTER COLUMN genres TYPE JSON USING genres::json;


/*delete duplicate from Credits table*/
delete from "Credits" as a using (select min(ctid) as ctid,m2.id,m2.crew,m2."cast" from "Credits" as m2
								group by m2.id,m2."cast",m2.crew
								having count(*)>1) as b
where a.ctid<>b.ctid and a.id=b.id and a."cast"=b."cast" and a.crew=b.crew


/*delete duplicate from Keywords table*/
delete from "Keywords" as a using (select min(ctid) as ctid,m2.id,m2.keywords from "Keywords" as m2
								group by m2.id,m2.keywords
								having count(*)>1) as b
where a.ctid<>b.ctid and a.id=b.id and a.keywords=b.keywords


/*delete all movie ids that are in Ratings table but not in Movies_Metadata*/
delete from "Links" where tmdbid not in (select id from "Movies_Metadata")
delete from "Links" where tmdbid is null


/*delete all movie ids that are in Ratings_Small table but not in Movies_Metadata */
DELETE FROM "Ratings_Small" r 
WHERE r.movieid NOT IN (
	SELECT id 
	FROM "Movies_Metadata");

/*delete all movie ids that are in Ratings table but not in Movies_Metadata */
DELETE FROM "Ratings" r 
WHERE r.movieid NOT IN (
	SELECT id 
	FROM "Movies_Metadata");

/*Add primary key to movies_metadata */
ALTER TABLE "Movies_Metadata"
ADD PRIMARY KEY(id);

/*Add foreign key to Credits */
ALTER TABLE "Credits" 
ADD CONSTRAINT "FK_Credits" FOREIGN KEY (id) REFERENCES "Movies_Metadata"(id);

/*Add foreign key to Keywords */
ALTER TABLE "Keywords" 
ADD CONSTRAINT "FK_Keyword" FOREIGN KEY (id) REFERENCES "Movies_Metadata"(id);

/*Add foreign key to Links */
ALTER TABLE "Links" 
ADD CONSTRAINT "FK_Links" FOREIGN KEY (tmdbid) REFERENCES "Movies_Metadata"(id);

/*Add foreign key to Ratings */
ALTER TABLE "Ratings" 
ADD CONSTRAINT "FK_Ratings" FOREIGN KEY (movieid) REFERENCES "Movies_Metadata"(id);

/*Add foreign key to Ratings Small */
ALTER TABLE "Ratings_Small" 
ADD CONSTRAINT "FK_Ratings_Small" FOREIGN KEY (movieid) REFERENCES "Movies_Metadata"(id);


