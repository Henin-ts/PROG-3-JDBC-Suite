import java.util.ArrayList;
import java.util.List;

public class TestConnexion {

    public static void main(String[] args) {

        DataRetriever dataRetriever = new DataRetriever();
        Dish dish = dataRetriever.findDishById(2);

        DBConnection db = new DBConnection();
        System.out.println(db.getConnection());
        System.out.println(dataRetriever.findDishById(2));
        System.out.println(dataRetriever.createIngredients( List.of(
                new Ingredient(10, "Tomate", 1.2, category_enum.vegetable, dish),
                new Ingredient(11, "Fromage", 3.5, category_enum.dairy, dish)
        )));
        System.out.println(dataRetriever.createIngredients(List.of()));

        dish.setIngredients(new ArrayList<>());

        Ingredient tomate = new Ingredient(
                1,
                "Tomate",
                1.20,
                category_enum.vegetable,
                dish
        );

        Ingredient oignon = new Ingredient(
                2,
                "Oignon",
                0.80,
                category_enum.vegetable,
                dish
        );

        dish.addIngredient(tomate);
        dish.addIngredient(oignon);

        dataRetriever.saveDish(dish);

        System.out.println("Plat sauvegardé avec succès !");

        // On recherche tous les plats qui contiennent "Tomate"
        String ingredientToSearch = "Tomate";

        List<Dish> dishes = dataRetriever.findDishsByIngredientName(ingredientToSearch);

        System.out.println("Plats contenant l'ingrédient '" + ingredientToSearch + "' :");

        for (Dish dishe : dishes) {
            System.out.println("- " + dishe.getName() + " (Type: " + dishe.getDish_type() + ")");
        }

        if (dishes.isEmpty()) {
            System.out.println("Aucun plat trouvé pour cet ingrédient.");
        }

        List<Ingredient> ingredients = dataRetriever.findIngredientsByCriteria(
                "Tomate",              // nom partiel
                category_enum.vegetable, // catégorie
                null,                   // nom du plat ignoré
                1,                      // page 1
                10                      // 10 résultats par page
        );

        for (Ingredient ing : ingredients) {
            System.out.println(ing.getName() + " | " + ing.getCategory() +
                    " | Plat: " + ing.getDishName());
        }
    }
    }

