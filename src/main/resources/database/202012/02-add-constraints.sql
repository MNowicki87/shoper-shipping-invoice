-- liquibase formatted sql
-- changeset mnowicki:2

alter table delivery_invoices
    add foreign key (order_id) references orders (id);
alter table delivery_invoices
    add foreign key (payment_id) references incoming_payments (id);
alter table order_items
    add foreign key (order_id) references orders (id);
alter table order_items
    add foreign key (product_id) references products (id);
alter table orders
    add foreign key (billing_address_id) references addresses (id);
alter table orders
    add foreign key (delivery_address_id) references addresses (id);
alter table orders
    add foreign key (payment_id) references incoming_payments (id);
alter table orders
    add foreign key (status_id) references statuses (id);
