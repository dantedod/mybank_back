CREATE TABLE users(

    id BIGSERIAL not null,
    name varchar(255) not null,
    email varchar(100) not null unique,
    password varchar(100) not null,
    phone varchar(100) not null,
    cpf varchar(11) not null unique,

    primary key(id)
);