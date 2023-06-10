package businesslogic.kitchenTask;

import businesslogic.shift.KitchenShift;
import businesslogic.user.User;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CookingJob {

    private KitchenShift kShift;
    private int amount;
    private float estimatedTime;
    private User cook;
    private boolean completed;
    private int id;

    public CookingJob(KitchenShift kShift, int amount, float estimatedTime) {
        this.id = 0;
        this.kShift = kShift;
        this.amount = amount;
        this.estimatedTime = estimatedTime;
        this.completed = false;
        this.cook = null;
    }

    public static void saveNewCookingJob(CookingJob job, int task_id) {
        String jobInsert = "INSERT INTO catering.cookingjobs (task_id, cook_id, amount, estimatedTime, completed) VALUES (?, ?, ?, ?, ?);";
        int[] result = PersistenceManager.executeBatchUpdate(jobInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, task_id);
                ps.setInt(2, job.getCook() == null ? 0 : job.getCook().getId());
                ps.setInt(3, job.getAmount());
                ps.setFloat(4, job.getEstimatedTime());
                ps.setBoolean(5, job.isCompleted());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                job.id = rs.getInt(1);
            }
        });

    }

    public int getAmount() {
        return amount;
    }

    public float getEstimatedTime() {
        return estimatedTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public User getCook() {
        return cook;
    }
}
