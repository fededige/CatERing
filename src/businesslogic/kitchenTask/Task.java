package businesslogic.kitchenTask;

import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Preparation;
import businesslogic.recipe.Recipe;
import businesslogic.shift.KitchenShift;
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
    private int position;

    public Task(KitchenProcedure kProc, int pos){
        amount = 0;
        estimatedTime = 0;
        toDo = true;
        procedure = kProc;
        jobs = new ArrayList<>();
        position = pos;
    }

    public static void saveAllNewTasks(int summarysheet_id, List<Task> tasks) {
        String taskInsert = "INSERT INTO catering.Tasks (summarysheet_id, recipe_id, preparation_id, amount, estimatedTime, toDo, position) VALUES (?, ?, ?, ?, ?, ?, ?);";
        PersistenceManager.executeBatchUpdate(taskInsert, tasks.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, summarysheet_id);
                if(tasks.get(batchCount).procedure instanceof Recipe){
                    ps.setInt(2, tasks.get(batchCount).procedure.getId());
                    ps.setInt(3, 0);
                } else if (tasks.get(batchCount).procedure instanceof Preparation){
                    ps.setInt(3, tasks.get(batchCount).procedure.getId());
                    ps.setInt(2, 0);
                }
                ps.setInt(4, tasks.get(batchCount).amount);
                ps.setFloat(5, tasks.get(batchCount).estimatedTime);
                ps.setBoolean(6, tasks.get(batchCount).toDo);
                ps.setInt(7, tasks.get(batchCount).position);
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
                KitchenProcedure kProc = null;
                if(rs.getInt("recipe_id") != 0){
                    kProc = Recipe.loadRecipeById(rs.getInt("recipe_id"));
                }
                else if(rs.getInt("preparation_id") != 0){
                    kProc = Preparation.loadPreparationById(rs.getInt("preparation_id"));
                }
                int position = rs.getInt("position");

                Task t = new Task(kProc, position);
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

    public static void updateTask(Task t) {
        String upd = "UPDATE tasks SET amount = " + t.getAmount() +
                " WHERE id = " + t.getId();
        PersistenceManager.executeUpdate(upd);
        upd = "UPDATE tasks SET estimatedTime = " + t.getEstimatedTime() +
                " WHERE id = " + t.getId();
        PersistenceManager.executeUpdate(upd);
        upd = "UPDATE tasks SET position = " + t.getPosition() +
                " WHERE id = " + t.getId();
        PersistenceManager.executeUpdate(upd);
    }

    public static void deleteTask(Task t) {
        String delTask = "DELETE FROM Tasks WHERE id = " + t.getId();
        PersistenceManager.executeUpdate(delTask);
    }

    public static Task loadTaskById(int id) {
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        String query = "SELECT * " +
                "FROM Tasks WHERE id = " + id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int recipe_id = rs.getInt("recipe_id");
                if(recipe_id == 0){
                    recipe_id = rs.getInt("preparation_id");
                }
                int position = rs.getInt("position");
                KitchenProcedure kProc = Recipe.loadRecipeById(recipe_id);
                Task t = new Task(kProc, position);
                t.id = rs.getInt("id");
                t.summarySheetId = rs.getInt("summarysheet_id");
                t.amount = rs.getInt("amount");
                t.estimatedTime = rs.getFloat("estimatedTime");
                t.toDo = rs.getBoolean("toDo");
                t.jobs = CookingJob.loadCookingJobByTaskId(t.id);
                tasks.add(t);
            }
        });
        return tasks.get(0);
    }

    public static ArrayList<Integer> loadTaskId(int summarysheet_id) {
        ArrayList<Integer> ids = new ArrayList<>();
        String query = "SELECT id " +
                "FROM Tasks WHERE summarysheet_id = " + summarysheet_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                ids.add(rs.getInt("id"));
            }
        });
        return ids;
    }

    private float getEstimatedTime() {
        return this.estimatedTime;
    }

    private int getAmount() {
        return this.amount;
    }

    public String testString() {
        StringBuilder result = new StringBuilder(this.toString() + "\n");

        result.append(procedure.testString());
        for(CookingJob job: jobs){
            result.append("\t" + job.testString());
            result.append("\n");
        }

        return result.toString() + "\n";
    }

    public String toString() {
        return "Task: " + id + " position: " + position + " sheetId: " + summarySheetId + " recipe: " + procedure.getId() + " " + procedure + " quantità: " + amount + "tempo stimato: " + estimatedTime + (toDo ? " da " : "da non ") +
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

    public KitchenShift deleteCookingJob(CookingJob oldJob) {
        KitchenShift kShift = oldJob.getkShift();
        kShift.freeTime(oldJob.getEstimatedTime());
        this.jobs.remove(oldJob);
        return kShift;
    }

    public ArrayList<CookingJob> getJobs() {
        return this.jobs;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setEstimatedTime(Float estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int newPos) {
        this.position = newPos;
    }

    public void setSummarySheetId(int summarySheetId) {
        this.summarySheetId = summarySheetId;
    }
}
