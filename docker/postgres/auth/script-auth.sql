CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE public.users (
	id serial4 NOT NULL,
	username varchar(50) NULL,
	"password" varchar(100) NULL,
	"role" varchar(20) NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id),
	CONSTRAINT users_username_key UNIQUE (username)
);

INSERT INTO public.users
(id, username, "password", "role")
VALUES(1, 'admin', crypt('1234', gen_salt('bf')), 'ROLE_ADMIN');
INSERT INTO public.users
(id, username, "password", "role")
VALUES(2, 'client', crypt('1234', gen_salt('bf')), 'ROLE_CLIENT');
