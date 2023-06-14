package businesslogic.kitchenTask;

import businesslogic.recipe.KitchenProcedure;
import businesslogic.shift.KitchenShift;
import businesslogic.shift.Shift;

import java.util.ArrayList;

public interface KitchenTaskEventReceiver {
    void updateSheetCreated(SummarySheet currentSheet);

    void updateSheetDeleted(SummarySheet deleteSheet);

    void updateKitchenProcedureAdded(KitchenProcedure kProc); //TODO: da spostare in KitchenProcedureEventReceiver

    void updateKitchenProcedureRemoved(KitchenProcedure oldProc); //TODO: da spostare in KitchenProcedureEventReceiver

    void updateCookingJobAdded(CookingJob job, int task_id, int shift_id);

    void updateCookingJobDeleted(CookingJob oldJob);

    void updateCookingJobChanged(CookingJob c);

    void updateTaskChanged(Task t);

    void updateTasksAdded(int sheet_id, ArrayList<Task> newTasks);

    void updateTaskDeleted(Task task);

    void updateShiftChanged(Shift kShift); //TODO: da spostare in KitchenProcedureEventReceiver
}
