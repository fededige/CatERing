package businesslogic.recipe;

import businesslogic.KitchenException;
import businesslogic.kitchenTask.CookingJob;
import businesslogic.kitchenTask.Task;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Preparation extends KitchenProcedure{
    private int id;
    private String name;


    public Preparation(String name){
        this.name = name;
    }

    public static ArrayList<Preparation> loadPreparationsByRecipeId(int recipe_id) {
        ArrayList<Preparation> preps = new ArrayList<>();
        String query = "SELECT * " +
                "FROM Preparations WHERE recipe_id = " + recipe_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String name = rs.getString("nome");
                Preparation prep = new Preparation(name);
                prep.id = rs.getInt("id");
                preps.add(prep);
            }
        });
        return  preps;
    }

    public static Preparation loadPreparationById(int id){
        ArrayList<Preparation> preps = new ArrayList<>();
        String query = "SELECT * " +
                "FROM Preparations WHERE id = " + id;
        System.out.println(query);
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String name = rs.getString("nome");
                Preparation prep = new Preparation(name);
                prep.id = rs.getInt("id");
                preps.add(prep);
            }
        });
        return  (preps.size() > 0) ? preps.get(0) : null;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "name: " + this.name;
    }
}
