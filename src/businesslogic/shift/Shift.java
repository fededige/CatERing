package businesslogic.shift;

import java.sql.Time;
import java.util.Date;

public abstract class Shift {
    private Date date;
    private String shiftHours;

    public void setDate(Date date) {
        this.date = date;
    }

    public void setShiftHours(String shiftHours) {
        this.shiftHours = shiftHours;
    }
}
