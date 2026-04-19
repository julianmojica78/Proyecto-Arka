CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE public.orders (
    id bigserial NOT NULL,
    customer_id varchar(100) NULL,
    status varchar(20) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    correlation_id varchar(100) NULL,
    updated_at timestamp NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (id)
);

CREATE TABLE public.order_items (
    id bigserial NOT NULL,
    order_id int8 NOT NULL,
    product_id int8 NOT NULL,
    quantity int4 NOT NULL,
    CONSTRAINT order_items_pkey PRIMARY KEY (id),
    CONSTRAINT order_items_order_fk FOREIGN KEY (order_id) REFERENCES public.orders(id)
);

CREATE TABLE public.outbox (
    event_id varchar DEFAULT gen_random_uuid() NOT NULL,
    aggregate_id varchar NULL,
    aggregate_type varchar NULL,
    event_type varchar NULL,
    payload text NULL,
    status varchar NULL,
    correlation_id varchar NULL,
    created_at timestamp NULL,
    retry_count int4 NULL,
    last_attempt timestamp NULL,
    CONSTRAINT outbox_pkey PRIMARY KEY (event_id)
);

CREATE TABLE public.processed_events (
    event_id varchar(255) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT processed_events_pkey PRIMARY KEY (event_id)
);

CREATE TABLE public.carts (
    id bigserial NOT NULL,
    customer_id varchar(100) NOT NULL,
    status varchar(20) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT carts_pkey PRIMARY KEY (id)
);

CREATE TABLE public.cart_items (
    id bigserial NOT NULL,
    cart_id int8 NOT NULL,
    product_id int8 NOT NULL,
    quantity int4 NOT NULL,
    CONSTRAINT cart_items_pkey PRIMARY KEY (id),
    CONSTRAINT cart_items_cart_fk FOREIGN KEY (cart_id) REFERENCES public.carts(id)
);
