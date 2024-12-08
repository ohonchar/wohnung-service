CREATE TABLE if not exists employee (
id serial primary key,
first_name varchar(250),
last_name varchar(250),
age integer,
designation varchar(250),
email varchar(250),
phone_number varchar(250),
joined_on date,
address varchar(250),
date_of_birth date,
created_at timestamp,
updated_at timestamp
);