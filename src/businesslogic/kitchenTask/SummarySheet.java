package businesslogic.kitchenTask;

import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class SummarySheet {
    private static Map<Integer, SummarySheet> loadedSheets = FXCollections.observableHashMap();
    private ObservableList<Task> tasks;
    private int id;
    public SummarySheet(){
        tasks = FXCollections.observableArrayList();
    }


    public static void saveNewSheet(SummarySheet s) {
        String sheetInsert = "INSERT INTO catering.SummarySheets () VALUES ();";
        int[] result = PersistenceManager.executeBatchUpdate(sheetInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
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

    public void addTask(Task newTask) {
        tasks.add(newTask);
    }

    public String testString() {
        String result = this.toString() + "\n";

        for (Task tsk : tasks) {
            result += tsk.testString();
            result += "\n";
        }

        return result;
    }

    public String toString() {
        return "id del foglio riepilogativo: " + id;
    }
}
