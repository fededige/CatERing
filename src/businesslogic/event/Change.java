package businesslogic.event;

import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.recipe.KitchenProcedure;

public class Change {
    private boolean isAddition;
    private KitchenProcedure kitchenProcedure;
    private MenuItem menuItem;

    public Change(KitchenProcedure kitchenProcedure){
        isAddition = true;
        this.kitchenProcedure = kitchenProcedure;
    }
    public Change(MenuItem menuItem){
        isAddition = false;
        this.menuItem = menuItem;
    }

    public MenuItem getMenuItem(){
        return this.menuItem;
    }

    public boolean isAddition() {
        return isAddition;
    }
}
