INSERT INTO users
VALUES ('p3170045', crypt('991015', gen_salt('bf')), 'This is my user'),
	   ('admin', crypt('t34EmtIF12', gen_salt('bf')), 'Privileged user');