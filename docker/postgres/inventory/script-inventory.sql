CREATE TABLE public.products (
	id serial4 NOT NULL,
	"name" varchar(100) NOT NULL,
	description text NULL,
	price numeric(10, 2) NOT NULL,
	stock int4 NOT NULL,
	category varchar(80) NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	active bool DEFAULT true NULL,
	CONSTRAINT products_pkey PRIMARY KEY (id)
);

CREATE TABLE public.stock_history (
	id bigserial NOT NULL,
	product_id int8 NOT NULL,
	previous_stock int4 NOT NULL,
	new_stock int4 NOT NULL,
	reason varchar(120) NOT NULL,
	changed_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT stock_history_pkey PRIMARY KEY (id),
	CONSTRAINT stock_history_product_fk FOREIGN KEY (product_id) REFERENCES public.products(id)
);
