package businesslogic.menu;

import businesslogic.recipe.Recipe;

public class MenuItem {
    private String description;
    private Recipe itemRecipe;

    public MenuItem(Recipe rec) {
        this(rec, rec.getName());
    }

    public MenuItem(Recipe rec, String desc) {
        itemRecipe = rec;
        description = desc;
    }

    public String toString() {
        return description;
    }
}
