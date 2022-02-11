CREATE TABLE "logging"(
    username character varying(25) NOT NULL,
    login_attempt_time timestamp NOT NULL,
    success boolean,
    CONSTRAINT logging_pkey PRIMARY KEY (username, login_attempt_time)
);
