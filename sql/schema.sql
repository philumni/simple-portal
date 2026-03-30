create database if not exists balance_portal;
use balance_portal;

create table if not exists customers (
    id bigint primary key auto_increment,
    email varchar(255) not null unique,
    full_name varchar(120) not null,
    password_hash varchar(255) not null,
    created_at timestamp not null default current_timestamp
);

create table if not exists customer_transactions (
    id bigint primary key auto_increment,
    customer_id bigint not null,
    entry_type enum('CHARGE', 'PAYMENT', 'ADJUSTMENT') not null,
    description varchar(255) not null,
    amount decimal(10, 2) not null,
    posted_at timestamp not null default current_timestamp,
    constraint fk_customer_transactions_customer
        foreign key (customer_id) references customers (id)
        on delete cascade
);
