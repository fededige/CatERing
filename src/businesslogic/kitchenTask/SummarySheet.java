package businesslogic.kitchenTask;

import businesslogic.KitchenException;
import businesslogic.event.ServiceInfo;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuException;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
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

    public static SummarySheet loadSummarySheetByServiceID(int service_id) {
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
            return null;
        }

        return sheetByID.get(0);
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
}
