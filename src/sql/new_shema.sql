CREATE TYPE unit_type AS ENUM ('PCS', 'KG', 'L');

CREATE TABLE DishIngredient (
                                 id SERIAL PRIMARY KEY,
                                 id_dish INT NOT NULL REFERENCES dish(id) ON DELETE CASCADE,
                                 id_ingredient INT NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
                                 quantity_required NUMERIC(10,3) NOT NULL,
                                 unit unit_type NOT NULL,
                                 UNIQUE(id_dish, id_ingredient)
);
