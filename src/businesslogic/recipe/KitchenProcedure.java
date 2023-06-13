package businesslogic.recipe;

public abstract class KitchenProcedure {
    private String name;
    private int id;

    public String testString() {
        return this.toString() + "\n";
    }


    public abstract int getId();

    public abstract String getName();
}
