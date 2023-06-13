package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Recipe extends KitchenProcedure{
    private static Map<Integer, Recipe> all = new HashMap<>();

    private int id;
    private String name;
    private ArrayList<Preparation> subProcedures;

    private Recipe(){

    }

    public Recipe(String name) {
        id = 0;
        this.name = name;
        subProcedures = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return "name: " + name;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<Recipe> loadAllRecipes() {
        String query = "SELECT * FROM Recipes";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                if (all.containsKey(id)) {
                    Recipe rec = all.get(id);
                    rec.name = rs.getString("name");
                    rec.subProcedures.addAll(Preparation.loadPreparationsByRecipeId(id));
                } else {
                    Recipe rec = new Recipe(rs.getString("name"));
                    rec.id = id;
                    rec.subProcedures = Preparation.loadPreparationsByRecipeId(id);
                    all.put(rec.id, rec);
                }
            }
        });
        ObservableList<Recipe> ret =  FXCollections.observableArrayList(all.values());
        Collections.sort(ret, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe o1, Recipe o2) {
                return (o1.getName().compareTo(o2.getName()));
            }
        });
        return ret;
    }

    public static ObservableList<Recipe> getAllRecipes() {
        return FXCollections.observableArrayList(all.values());
    }

    public static Recipe loadRecipeById(int id) {
        if (all.containsKey(id)) return all.get(id);
        Recipe rec = new Recipe();
        String query = "SELECT * FROM Recipes WHERE id = " + id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                    rec.name = rs.getString("name");
                    rec.id = id;
                    all.put(rec.id, rec);
            }
        });
        return rec;
    }

    public static void saveNewKitchenProcedure(Recipe kProc){
        String kProcInsert = "INSERT INTO catering.Recipes (name) VALUES (?);";

        int[] result = PersistenceManager.executeBatchUpdate(kProcInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setString(1, kProc.getName());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                kProc.id = rs.getInt(1);
            }
        });
    }

    public static void deleteKProcedure(KitchenProcedure oldProc) {
        String delMenuItems = "DELETE FROM MenuItems WHERE recipe_id = " + oldProc.getId();
        PersistenceManager.executeUpdate(delMenuItems);
        String delRecipe = "DELETE FROM Recipes WHERE id = " + oldProc.getId();
        PersistenceManager.executeUpdate(delRecipe);
    }

    public ArrayList<Preparation> getProcedures() {
        return this.subProcedures;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Recipe){
            return ((Recipe) obj).getId() == this.id;
        }
        return false;
    }
}
