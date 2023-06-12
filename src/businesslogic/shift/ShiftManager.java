package businesslogic.shift;

import businesslogic.kitchenTask.KitchenTaskManager;

import java.util.ArrayList;

public class ShiftManager {
    private ArrayList<KitchenShift> shiftTable;
    public ShiftManager(){
        shiftTable = KitchenShift.loadAllKShift();
    }
    public ArrayList<KitchenShift> getShiftTable() {
        return this.shiftTable;
    }
}
