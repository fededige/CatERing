package businesslogic.recipe;

import java.util.ArrayList;

public abstract class KitchenProcedure {
    private String name;
    private String author;
    private String instruction;
    private int quantity;

    public abstract ArrayList<KitchenProcedure> getProcedures();

    public String testString() {
        return this.toString() + "\n";
    }

    public String toString() {
        return "Procedura di cucina: " + name + "autore: " + author + "instruction: " + instruction + "quantità: " + quantity;
    }
}
