/*convet price and adjusted_price to numeric*/
ALTER "Calendar"
ALTER COLUMN price TYPE  NUMERIC(15,2) USING(REGEXP_REPLACE(price,'[$,]','','g')::NUMERIC(15,2));

ALTER "Calendar"
ALTER COLUMN adjusted_price TYPE  NUMERIC(15,2) USING(REGEXP_REPLACE(adjusted_price,'[$,]','','g')::NUMERIC(15,2));
