import java.util.List;

public class Dish {

    private int id;
    private String name;
    private DishTypeEnum dish_type;
    private List<Ingredient> ingredients;

    public Dish(int id, String name, DishTypeEnum dish_type, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dish_type = dish_type;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDish_type() {
        return dish_type;
    }

    public void setDish_type(DishTypeEnum dish_type) {
        this.dish_type = dish_type;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Double getPrice() {
        return ingredients.getFirst().getPrice();
    }

}
