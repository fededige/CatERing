package businesslogic.kitchenTask;

import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.KitchenProcedure;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Task {
    private int amount;
    private float estimatedTime;
    private boolean toDo;
    private KitchenProcedure procedure;
    private int id;

    public Task(KitchenProcedure kProc){
        amount = 0;
        estimatedTime = 0;
        toDo = true;
        procedure = kProc;
    }

    public static void saveAllNewTasks(int summarysheet_id, List<Task> tasks) {
        String taskInsert = "INSERT INTO catering.Tasks (summarysheet_id, amount, estimatedTime, toDo) VALUES (?, ?, ?, ?);";
        PersistenceManager.executeBatchUpdate(taskInsert, tasks.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, summarysheet_id);
                ps.setInt(2, tasks.get(batchCount).amount);
                ps.setFloat(3, tasks.get(batchCount).estimatedTime);
                ps.setBoolean(4, tasks.get(batchCount).toDo);
                ps.setInt(3, batchCount);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                tasks.get(count).id = rs.getInt(1);
            }
        });
    }

    public String testString() {
        String result = this.toString() + "\n";

        result += procedure.testString();
        result += "\n";

        return result;
    }

    public String toString() {
        return "Task: " + id + " quantità: " + amount + "tempo stimato: " + estimatedTime + (toDo ? " da " : "da non ") +
                "farsi";
    }
}
