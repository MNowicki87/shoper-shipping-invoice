-- liquibase formatted sql
-- changeset mnowicki:1

create table addresses
(
    id           integer      not null primary key,
    city         varchar(255) not null,
    company      varchar(255),
    first_name   varchar(255),
    last_name    varchar(255),
    nip          varchar(255),
    pesel        varchar(255),
    phone_number varchar(255),
    postcode     varchar(255),
    street1      varchar(255),
    street2      varchar(255)
);

create table delivery_invoice_init_values
(
    id    integer not null auto_increment primary key,
    value integer not null,
    year  integer not null unique
);

create table delivery_invoices
(
    id             bigint  not null auto_increment primary key,
    gross_amount   double  not null,
    invoice_number integer not null,
    issue_date     date    not null,
    year           integer not null,
    order_id       bigint  not null,
    payment_id     bigint  not null,
    unique (invoice_number, year)
);

create table incoming_payments
(
    id           bigint not null auto_increment primary key,
    amount       double,
    payment_date date,
    payment_type varchar(255),
    sender       varchar(255),
    title        varchar(255)
);

create table order_items
(
    id                  bigint  not null primary key,
    discount_percentage double,
    quantity            integer not null check (quantity >= 1),
    tax                 double,
    weight              double,
    order_id            bigint  not null,
    product_id          bigint
);

create table orders
(
    id                  bigint not null primary key,
    admin_private_notes varchar(500),
    admin_public_notes  varchar(500),
    client_notes        varchar(500),
    created_at          timestamp,
    email               varchar(255),
    shipping_paid       boolean,
    paid                double,
    promo_code          varchar(255),
    shipping_cost       double,
    order_source        varchar(255),
    total               double,
    updated_at          timestamp,
    billing_address_id  integer,
    delivery_address_id integer,
    payment_id          bigint,
    status_id           integer
);

create table products
(
    id   bigint not null primary key,
    code varchar(255)
);

create table statuses
(
    id   integer not null primary key,
    name varchar(255),
    type varchar(255)
);


