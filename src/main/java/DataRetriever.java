import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DataRetriever {

    DBConnection db = new DBConnection();

    public Dish findDishById(int id) {

        List<Dish> dishes = new ArrayList<>();

        String sql = """
            SELECT 
                dish.id AS dish_id,
                dish.name AS dish_name,
                dish.dish_type,
                ingredient.id AS ingredient_id,
                ingredient.name AS ingredient_name,
                ingredient.price,
                ingredient.category
            FROM dish
            INNER JOIN ingredient ON dish.id = ingredient.id_dish
            WHERE dish.id = ?
        """;

        try (Connection connection = db.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();

            Dish dish = null;

            while (rs.next()) {

                if (dish == null) {
                    dish = new Dish(
                            rs.getInt("dish_id"),
                            rs.getString("dish_name"),
                            DishTypeEnum.valueOf(rs.getString("dish_type")),
                            new ArrayList<>()
                    );
                    dishes.add(dish);
                }

                Ingredient ingredient = new Ingredient(
                        rs.getInt("ingredient_id"),
                        rs.getString("ingredient_name"),
                        rs.getDouble("price"),
                        category_enum.valueOf(rs.getString("category")),
                        dish
                );

                dish.addIngredient(ingredient);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dishes.isEmpty() ? null : dishes.get(0);
    }

    public List<Ingredient> findIngredients(int page, int size) {

        List<Ingredient> ingredients = new ArrayList<>();

        int offset = (page - 1) * size;

        String sql = """
        SELECT id, name, price, category
        FROM ingredient
        ORDER BY id
        LIMIT ? OFFSET ?
    """;

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, size);
            statement.setInt(2, offset);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        category_enum.valueOf(rs.getString("category")),
                        null
                );
                ingredients.add(ingredient);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ingredients;
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {

        String checkSql = "SELECT 1 FROM ingredient WHERE name = ?";
        String insertSql = "INSERT INTO ingredient (name, price, category, id_dish) VALUES (?, ?, ?::category_enum, ?)";

        try (Connection connection = db.getConnection()) {

            // 🔐 Début transaction
            connection.setAutoCommit(false);

            try (
                    PreparedStatement checkStmt = connection.prepareStatement(checkSql);
                    PreparedStatement insertStmt = connection.prepareStatement(insertSql)
            ) {

                for (Ingredient ingredient : newIngredients) {

                    checkStmt.setString(1, ingredient.getName());
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        continue;
                    }

                    insertStmt.setString(1, ingredient.getName());
                    insertStmt.setDouble(2, ingredient.getPrice());
                    insertStmt.setString(3, ingredient.getCategory().name());
                    insertStmt.setInt(4, ingredient.getDish().getId());
                    insertStmt.executeUpdate();
                }

                connection.commit();

                return newIngredients;

            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("Transaction annulée", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dish saveDish(Dish toSave) {

        String upsertDishSql = """
        INSERT INTO dish (id, name, dish_type)
        VALUES (?, ?, ?::dish_type)
        ON CONFLICT (id) DO UPDATE
        SET name = EXCLUDED.name,
            dish_type = EXCLUDED.dish_type
        RETURNING id
    """;

        String attachIngredientSql =
                "UPDATE ingredient SET id_dish = ? WHERE id = ?";

        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);

            int dishId;

            /* 1️⃣ INSERT ou UPDATE DISH */
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {

                if (toSave.getId() > 0) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setNull(1, Types.INTEGER);
                }

                ps.setString(2, toSave.getName());
                ps.setString(3, toSave.getDish_type().name());

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                    toSave.setId(dishId);
                }
            }

            /* 2️⃣ ASSOCIER ingrédients (SANS NULL) */
            try (PreparedStatement ps = conn.prepareStatement(attachIngredientSql)) {

                for (Ingredient ingredient : toSave.getIngredients()) {

                    ps.setInt(1, dishId);
                    ps.setInt(2, ingredient.getId());
                    ps.executeUpdate();

                    ingredient.setDish(toSave);
                }
            }

            conn.commit();
            return toSave;

        } catch (Exception e) {
            throw new RuntimeException("Transaction annulée", e);
        }
    }

    public List<Dish> findDishsByIngredientName(String ingredientName) {

        String sql = """
        SELECT d.id as dish_id, d.name as dish_name, d.dish_type
        FROM dish d
        JOIN ingredient i ON i.id_dish = d.id
        WHERE i.name ILIKE ?
        GROUP BY d.id, d.name, d.dish_type
    """;

        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + ingredientName + "%"); // recherche partielle

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("dish_id");
                    String name = rs.getString("dish_name");
                    DishTypeEnum type = DishTypeEnum.valueOf(rs.getString("dish_type"));

                    Dish dish = new Dish(id, name, type, new ArrayList<>());
                    dishes.add(dish);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dishes;
    }

    public List<Ingredient> findIngredientsByCriteria(
            String ingredientName,
            category_enum category,
            String dishName,
            int page,
            int size
    ) {

        List<Ingredient> ingredients = new ArrayList<>();

        // Base SQL
        StringBuilder sql = new StringBuilder("""
        SELECT i.id AS ingredient_id, i.name AS ingredient_name,
               i.price, i.category, i.id_dish,
               d.name AS dish_name, d.dish_type
        FROM ingredient i
        JOIN dish d ON i.id_dish = d.id
        WHERE 1=1
    """);

        // Paramètres dynamiques
        List<Object> params = new ArrayList<>();

        if (ingredientName != null && !ingredientName.isEmpty()) {
            sql.append(" AND i.name ILIKE ?");
            params.add("%" + ingredientName + "%");
        }

        if (category != null) {
            sql.append(" AND i.category = ?::category_enum");
            params.add(category.name());
        }

        if (dishName != null && !dishName.isEmpty()) {
            sql.append(" AND d.name ILIKE ?");
            params.add("%" + dishName + "%");
        }

        // Pagination
        sql.append(" ORDER BY i.id ASC LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page - 1) * size);

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Injection des paramètres
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                }
            }

            // Exécution
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("ingredient_id");
                    String name = rs.getString("ingredient_name");
                    double price = rs.getDouble("price");
                    category_enum cat = category_enum.valueOf(rs.getString("category"));

                    // Création du Dish minimal pour associer
                    Dish dish = new Dish(
                            rs.getInt("id_dish"),
                            rs.getString("dish_name"),
                            DishTypeEnum.valueOf(rs.getString("dish_type")),
                            new ArrayList<>()
                    );

                    Ingredient ingredient = new Ingredient(id, name, price, cat, dish);
                    ingredients.add(ingredient);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ingredients;
    }


}
