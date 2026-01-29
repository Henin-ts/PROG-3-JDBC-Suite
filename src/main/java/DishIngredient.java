public class DishIngredient {
    private int id;
    private Dish dish;
    private Ingredient ingredient;
    private double quantity;
    private UnitType unit;

    public DishIngredient(int id, Dish dish, Ingredient ingredient, double quantity, UnitType unit) {
        this.id = id;
        this.dish = dish;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }
}

