import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    DBConnection db = new DBConnection();

    public  Dish findDishById(int id){

        List<Dish> dishes = new ArrayList<>();
        String sql = "Select dish.id, dish.name , ingredient.name from dish inner join ingredient on dish.id = ingredient.id_dish where dish.id = ?";

        try(Connection connection = db.getConnection()){

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();

            while(rs.next()){


                Dish dish = new Dish(
                         rs.getInt("id"),
                        rs.getString("name"),
                        DishTypeEnum.valueOf(rs.getString("dish_type")),
                        

                );


                Ingredient ingredient = new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        category_enum.valueOf(rs.getString("category")),
                        dish
                );

                dishes.add(dish);


            }


    } catch (SQLException e) {
        throw new RuntimeException(e);
        }
     return dishes.get(0);}
}
