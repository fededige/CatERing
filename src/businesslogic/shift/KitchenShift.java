package businesslogic.shift;

import businesslogic.UseCaseLogicException;

import java.sql.Time;
import java.time.LocalTime;

public class KitchenShift extends Shift{
    private boolean isFull;
    private float availableTime;
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
}
