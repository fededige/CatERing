package businesslogic.kitchenTask;

import businesslogic.KitchenException;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.shift.KitchenShift;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
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

    public void addProcedure(ArrayList<KitchenProcedure> newKProcs) {
        ArrayList<Task> newTasks = new ArrayList<>();
        for(KitchenProcedure newKProc: newKProcs){
            Task newTask = new Task(newKProc);
            System.out.println(newTask);
            newTasks.add(newTask);
        }

        Task.saveAllNewTasks(this.id, newTasks);
        tasks.addAll(newTasks);
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

    public void deleteCookingJob(Task t, CookingJob oldJob) {
        for(Task task: tasks){
            if(task.equals(t)){
                task.deleteCookingJob(oldJob);
                return;
            }
        }
    }

    public void modifyCookingJob(Task t, CookingJob c, Integer amount, Float estimatedTime) {
        int i = 0;
        for(Task task : tasks){
            if(task.equals(t)){
                for(CookingJob job: t.getJobs()){
                    i++;
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

//    public void removeProcedure(KitchenProcedure oldKProc) {
//        for(Task task: tasks){
//            if(task.getProcedure().equals(oldKProc)){
//                for()
//            }
//        }
//    }
}
