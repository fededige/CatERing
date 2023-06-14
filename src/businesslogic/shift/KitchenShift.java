package businesslogic.shift;

import businesslogic.KitchenException;
import businesslogic.UseCaseLogicException;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class KitchenShift extends Shift{
    private boolean isFull;
    private float availableTime;
    private int id;
    private Date date;
    private String shiftHours;
    public KitchenShift(float aTime){
        this.availableTime = aTime;
    }

    public static ArrayList<KitchenShift> loadAllKShift() {
        ArrayList<KitchenShift> shifts = new ArrayList<>();
        String query = "SELECT * FROM kitchenshifts";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                float aTime = rs.getInt("availableTime");
                KitchenShift shift = new KitchenShift(aTime);
                shift.id = rs.getInt("id");
                shift.setDate(rs.getDate("date"));
                shift.setShiftHours(rs.getString("shiftHours"));
                shift.setDate(rs.getDate("date"));
                shifts.add(shift);
            }
        });

        return shifts;
    }

    public static void updateShift(KitchenShift shift) {
        String upd = "UPDATE kitchenshifts SET isFull = " + shift.isFull() + " AND availableTime = " + shift.getAvailableTime() +" WHERE id = " + shift.getId();
        PersistenceManager.executeUpdate(upd);
    }

    private float getAvailableTime() {
        return this.availableTime;
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
                shift.setDate(rs.getDate("date"));
                shifts.add(shift);
            }
        });
        if(shifts.size() == 0){
            throw new KitchenException();
        }

        return shifts.get(0);
    }

    @Override
    public String toString() {
        return "shift: " + this.id + ", il turno " + (this.isFull ? "non" : "" ) + " è pieno; " + "sono rimaste: " + this.availableTime + " ore" + " si svolge in data: " + this.date + " nelle ore: " + this.shiftHours;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public void setShiftHours(String shiftHours) {
        this.shiftHours = shiftHours;
    }
}
