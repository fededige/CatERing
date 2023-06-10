package businesslogic.shift;

import businesslogic.KitchenException;
import businesslogic.UseCaseLogicException;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.kitchenTask.Task;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

public class KitchenShift extends Shift{
    private boolean isFull;
    private float availableTime;
    private int id;
    public KitchenShift(float aTime){
        this.availableTime = aTime;
    }
    public void freeTime(float duration){
        if(availableTime != 0 && duration > 0)
            this.isFull = false;
        availableTime += duration;
    }
    public void addTime(float duration) throws UseCaseLogicException {
        if(availableTime == 0 && duration > 0)
            throw new UseCaseLogicException();
        availableTime -= duration;
        if(availableTime == 0)
            this.isFull = true;
    }
    public boolean isFull(){
        return isFull;
    }

    public int getId() {
        return this.id;
    }

    public static KitchenShift loadKitchenShiftById(int id) throws KitchenException {
        ArrayList<KitchenShift> shifts = new ArrayList<>();
        String query = "SELECT * FROM kitchenshifts WHERE id = " + id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                float aTime = rs.getInt("availableTime");
                KitchenShift shift = new KitchenShift(aTime);
                shift.id = rs.getInt("id");
                shift.setDate(rs.getDate("date"));
                shift.setShiftHours(rs.getString("shiftHours"));
                shifts.add(shift);
            }
        });
        if(shifts.size() == 0){
            throw new KitchenException();
        }

        return shifts.get(0);
    }
}
