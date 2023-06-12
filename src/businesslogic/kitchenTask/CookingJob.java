package businesslogic.kitchenTask;

import businesslogic.KitchenException;
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

    public static void saveNewCookingJob(CookingJob job, int task_id, int shift_id) {
        String jobInsert = "INSERT INTO catering.cookingjobs (task_id, cook_id, shift_id, amount, estimatedTime, completed) VALUES (?, ?, ?, ?, ?, ?);";
        int[] result = PersistenceManager.executeBatchUpdate(jobInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, task_id);
                ps.setInt(2, job.getCook() == null ? 0 : job.getCook().getId());
                ps.setInt(3, shift_id);
                ps.setInt(4, job.getAmount());
                ps.setFloat(5, job.getEstimatedTime());
                ps.setBoolean(6, job.isCompleted());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                job.id = rs.getInt(1);
            }
        });

    }

    public static void deleteCookingJob(CookingJob oldJob) {
        String delJob = "DELETE FROM cookingjobs WHERE id = " + oldJob.getId();
        PersistenceManager.executeUpdate(delJob);
    }

    public static ArrayList<CookingJob> loadCookingJobByTaskId(int task_id) {
        ArrayList<CookingJob> jobs = new ArrayList<>();
        String query = "SELECT * FROM cookingjobs WHERE task_id = " + task_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int shift_id = rs.getInt("shift_id");
                int amount = rs.getInt("amount");
                float estimatedTime = rs.getFloat("estimatedTime");
                KitchenShift kShift = null;
                try {
                    kShift = KitchenShift.loadKitchenShiftById(shift_id);
                } catch (KitchenException e) {
                    throw new RuntimeException(e);
                }

                CookingJob job = new CookingJob(kShift, amount, estimatedTime);
                job.id = rs.getInt("id");
                job.cook = rs.getInt("cook_id") != 0 ? User.loadUserById(rs.getInt("cook_id")) : null;
                jobs.add(job);
            }
        });
        return jobs;
    }

    public static void updateCookingJob(CookingJob c) {
        String upd = "UPDATE cookingjobs SET amount = " + c.getAmount() +
                " WHERE id = " + c.getId();
        PersistenceManager.executeUpdate(upd);
        upd = "UPDATE cookingjobs SET estimatedTime = " + c.getEstimatedTime() +
                " WHERE id = " + c.getId();
        PersistenceManager.executeUpdate(upd);
        if(c.getCook() != null){
            upd = "UPDATE cookingjobs SET cook_id = " + c.getCook().getId() +
                    " WHERE id = " + c.getId();
            PersistenceManager.executeUpdate(upd);
        }
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int a){
        this.amount = a;
    }

    public void setEstimatedTime(float e){
        this.estimatedTime = e;
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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CookingJob){
            return ((CookingJob) obj).getId() == this.id;
        }
        return false;
    }

    private int getId() {
        return this.id;
    }

    public KitchenShift getkShift() {
        return kShift;
    }

    public void setCook(User cook) {
        System.out.println("precook: " + this.cook);
        this.cook = cook;
        System.out.println("postcook: " + this.cook);
    }

    public String testString() {
        return "Job: " + this.id + " amount: " + this.amount + " estimatedTIme: " + this.estimatedTime + " cook: " + (this.cook != null ? this.cook.toString() : "null");
    }
}
