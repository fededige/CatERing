package businesslogic.kitchenTask;

import businesslogic.KitchenException;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Recipe;
import businesslogic.shift.KitchenShift;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class SummarySheet {
    private static Map<Integer, SummarySheet> loadedSheets = FXCollections.observableHashMap();
    private ObservableList<Task> tasks;
    private int id;
    private int serviceId;

    public SummarySheet(){
        tasks = FXCollections.observableArrayList();
    }


    public static void saveNewSheet(SummarySheet s) {
        String sheetInsert = "INSERT INTO catering.SummarySheets (service_id) VALUES (?);";
        int[] result = PersistenceManager.executeBatchUpdate(sheetInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, s.getServiceId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // should be only one
                if (count == 0) {
                    s.id = rs.getInt(1);
                }
            }
        });

        if (result[0] > 0) { // sheet effettivamente inserito
            // salva i tasks
            // salva le sezioni
            if (s.tasks.size() > 0) {
                Task.saveAllNewTasks(s.id, s.tasks);
            }
            loadedSheets.put(s.id, s);
        }
    }

    public static SummarySheet loadSummarySheetByServiceId(int service_id) throws KitchenException {
        ArrayList<SummarySheet> sheetByID = new ArrayList<>();
        String query = "SELECT * FROM SummarySheets WHERE service_id = " + service_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                SummarySheet sheet = new SummarySheet();
                sheet.id = rs.getInt("id");
                sheet.serviceId = rs.getInt("service_id");
                sheet.tasks = Task.loadTasksBySheetId(sheet.id);
                sheetByID.add(sheet);
            }
        });
        if(sheetByID.size() == 0){
            throw new KitchenException();
        }

        return sheetByID.get(0);
    }

    public static SummarySheet loadSummarySheetById(int id) throws KitchenException {
        ArrayList<SummarySheet> sheetByID = new ArrayList<>();
        String query = "SELECT * FROM SummarySheets WHERE id = " + id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                SummarySheet sheet = new SummarySheet();
                sheet.id = rs.getInt("id");
                sheet.serviceId = rs.getInt("service_id");
                sheet.tasks = Task.loadTasksBySheetId(sheet.id);
                sheetByID.add(sheet);
            }
        });
        if(sheetByID.size() == 0){
            throw new KitchenException();
        }

        return sheetByID.get(0);
    }


    public static void deleteSummarySheet(SummarySheet summarySheet){
        String delSheet = "DELETE FROM tasks WHERE summarysheet_id = " + summarySheet.getId();
        PersistenceManager.executeUpdate(delSheet);

        String del = "DELETE FROM SummarySheets WHERE id = " + summarySheet.getId();
        PersistenceManager.executeUpdate(del);
    }


    private int getServiceId() {
        return this.serviceId;
    }

    public void addTask(Task newTask) {
        tasks.add(newTask);
    }

    public String testString() {
        String result = this.toString() + "\n";

        if(tasks != null){
            for (Task tsk : tasks) {
                result += tsk.testString();
                result += "\n";
            }
        }

        return result;
    }

    public String toString() {
        return "id del foglio riepilogativo: " + id;
    }

    public void addServiceId(int id) {
        this.serviceId = id;
    }

    public int getId() {
        return this.id;
    }

    public ArrayList<Task> addProcedure(KitchenProcedure newKProc) {
        ArrayList<KitchenProcedure> newKProcs = new ArrayList<>();
        newKProcs.add(newKProc);
        newKProcs.addAll(((Recipe) newKProc).getProcedures());
        ArrayList<Task> newTasks = new ArrayList<>();
        int pos = tasks.get(tasks.size() - 1).getPosition() + 1;
        for(KitchenProcedure proc: newKProcs){
            Task newTask = new Task(proc, pos);
            newTask.setSummarySheetId(this.id);
            newTasks.add(newTask);
            pos++;
        }

        tasks.addAll(newTasks);
        return newTasks;
    }

    public boolean hasTask(Task t){
        return tasks.contains(t);
    }

    public CookingJob createCookingJob(Task t, KitchenShift kShift, int amount, float estimatedTime) throws KitchenException {
        for(Task task: tasks){
            if(task.equals(t)){
                CookingJob newJob = new CookingJob(kShift, amount, estimatedTime);
                t.addJob(newJob);
                return newJob;
            }
        }
        throw new KitchenException();
    }

    public KitchenShift deleteCookingJob(Task t, CookingJob oldJob) throws KitchenException {
        for(Task task: tasks){
            if(task.equals(t)){
                return task.deleteCookingJob(oldJob);
            }
        }
        throw new KitchenException();
    }

    public void modifyCookingJob(Task t, CookingJob c, Integer amount, Float estimatedTime) {
        for(Task task : tasks){
            if(task.equals(t)){
                for(CookingJob job: t.getJobs()){
                    if(job.equals(c)){
                        if(amount != null)
                            c.setAmount(amount);
                        if(estimatedTime != null)
                            c.setEstimatedTime(estimatedTime);
                        return;
                    }
                }
            }
        }
    }

    public boolean hasJob(CookingJob c) {
        for(Task t: tasks){
            if(t.hasJob(c))
                return true;
        }
        return false;
    }

    public CookingJob addCook(CookingJob c, User cook) throws KitchenException {
        for(Task t: tasks){
            for(CookingJob j: t.getJobs()){
                if(j.equals(c)){
                    j.setCook(cook);
                    System.out.println(j.testString());
                    return j;
                }
            }
        }
        throw new KitchenException();
    }

    public Task changeTask(Task t, Integer amount, Float estimatedTime) throws KitchenException {
        for(Task task: tasks){
            if(task.equals(t)){
                if(amount != null){
                    t.setAmount(amount);
                }
                if(estimatedTime != null){
                    t.setEstimatedTime(estimatedTime);
                }
                return t;
            }
        }
        throw new KitchenException();
    }

    public Task removeProcedure(KitchenProcedure oldProc) throws KitchenException {
        for(Task t: tasks){
            if(t.getProcedure().equals(oldProc)){
                for(CookingJob j: t.getJobs()){
                    t.deleteCookingJob(j);
                }
                tasks.remove(t);
                return t;
            }
        }
        throw new KitchenException();
    }

    public int lengthTasks() {
        return this.tasks.size();
    }

    public ArrayList<Task> moveTask(Task t, int newPos){
        ArrayList<Task> changeTasks = new ArrayList<>();
        for(Task task: tasks){
            if(task.equals(t)){
                task.setPosition(newPos);
                t.setPosition(newPos);
                changeTasks.add(task);
                break;
            }
        }
        for(Task task: tasks){
            if(!task.equals(t) && task.getPosition() >= newPos){
                task.setPosition(task.getPosition() + 1);
                changeTasks.add(task);
            }
        }
        return changeTasks;
    }
}
