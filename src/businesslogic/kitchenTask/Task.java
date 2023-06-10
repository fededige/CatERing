package businesslogic.kitchenTask;

import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Recipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private int amount;
    private float estimatedTime;
    private boolean toDo;
    private int summarySheetId;
    private KitchenProcedure procedure;
    private ArrayList<CookingJob> jobs;
    private int id;

    public Task(KitchenProcedure kProc){
        amount = 0;
        estimatedTime = 0;
        toDo = true;
        procedure = kProc;
        jobs = new ArrayList<>();
    }

    public static void saveAllNewTasks(int summarysheet_id, List<Task> tasks) {
        String taskInsert = "INSERT INTO catering.Tasks (summarysheet_id, procedure_id, amount, estimatedTime, toDo) VALUES (?, ?, ?, ?, ?);";
        PersistenceManager.executeBatchUpdate(taskInsert, tasks.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, summarysheet_id);
                ps.setInt(2, tasks.get(batchCount).procedure.getId());
                ps.setInt(3, tasks.get(batchCount).amount);
                ps.setFloat(4, tasks.get(batchCount).estimatedTime);
                ps.setBoolean(5, tasks.get(batchCount).toDo);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                tasks.get(count).id = rs.getInt(1);
            }
        });
    }

    public static ObservableList<Task> loadTasksBySheetId(int summarysheet_id) {
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        String query = "SELECT * " +
                "FROM Tasks WHERE summarysheet_id = " + summarysheet_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int recipe_id = rs.getInt("procedure_id");
                Recipe recipe = Recipe.loadRecipeById(recipe_id);
                Task t = new Task(recipe);
                t.id = rs.getInt("id");
                t.summarySheetId = rs.getInt("summarysheet_id");
                t.amount = rs.getInt("amount");
                t.estimatedTime = rs.getFloat("estimatedTime");
                t.toDo = rs.getBoolean("toDo");
                t.jobs = CookingJob.loadCookingJobByTaskId(t.id);
                tasks.add(t);
            }
        });
        return tasks;
    }

    public String testString() {
        String result = this.toString() + "\n";

        result += procedure.testString();
        result += "\n";

        return result;
    }

    public String toString() {
        return "Task: " + id + " sheetId: " + summarySheetId + " recipe: " + procedure + " quantità: " + amount + "tempo stimato: " + estimatedTime + (toDo ? " da " : "da non ") +
                "farsi";
    }

    public KitchenProcedure getProcedure() {
        return procedure;
    }

    public void addJob(CookingJob newJob) {
        this.jobs.add(newJob);
    }

    public int getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Task)
            return ((Task) obj).getId() == this.id;
        return false;
    }

    public boolean hasJob(CookingJob oldJob) {
        return jobs.contains(oldJob);
    }

    public void deleteCookingJob(CookingJob oldJob) {
        oldJob.getkShift().freeTime(oldJob.getEstimatedTime());
        this.jobs.remove(oldJob);
    }

    public ArrayList<CookingJob> getJobs() {
        return this.jobs;
    }
}
