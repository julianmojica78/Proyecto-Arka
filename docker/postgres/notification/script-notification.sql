CREATE TABLE public.notifications (
    id bigserial NOT NULL,
    order_id int8 NULL,
    cart_id int8 NULL,
    customer_id varchar(100) NULL,
    type varchar(40) NOT NULL,
    message text NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT notifications_pkey PRIMARY KEY (id)
);
