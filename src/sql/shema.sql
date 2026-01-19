create table ingredient(
    id serial primary key,
    name varchar(150),
    price numeric(10,2) not null check ( price > 0 ),
    category category_enum,
    id_dish int not null,
    constraint fk_dish foreign key (id_dish) references dish(id)
);

create type category_enum as enum(
    'vegetable',
    'animal',
    'marine',
    'dairy',
    'other'
);

create table dish(
    id serial primary key,
    name varchar(150) ,
    dish_type dish_type
);

create type dish_type as enum(
    'start',
    'main',
    'dessert'
);