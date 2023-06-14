package persistence;

import businesslogic.kitchenTask.CookingJob;
import businesslogic.kitchenTask.KitchenTaskEventReceiver;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.kitchenTask.Task;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Recipe;
import businesslogic.shift.KitchenShift;
import businesslogic.shift.ServiceShift;
import businesslogic.shift.Shift;

import java.util.ArrayList;

public class KitchenTaskPersistence implements KitchenTaskEventReceiver {

    @Override
    public void updateSheetCreated(SummarySheet currentSheet) {
        SummarySheet.saveNewSheet(currentSheet);
    }

    @Override
    public void updateSheetDeleted(SummarySheet deleteSheet) {
        SummarySheet.deleteSummarySheet(deleteSheet);
    }

    @Override
    public void updateKitchenProcedureAdded(KitchenProcedure kProc) {
        Recipe.saveNewKitchenProcedure((Recipe) kProc);
    }

    @Override
    public void updateCookingJobAdded(CookingJob job, int task_id, int shift_id) {
        CookingJob.saveNewCookingJob(job, task_id, shift_id);
    }


    @Override
    public void updateTaskChanged(Task t) {
        Task.updateTask(t);
    }

    @Override
    public void updateKitchenProcedureRemoved(KitchenProcedure oldProc) {
        Recipe.deleteKProcedure(oldProc);
    }

    @Override
    public void updateTasksAdded(int sheet_id, ArrayList<Task> newTasks) {
        Task.saveAllNewTasks(sheet_id, newTasks);
    }

    @Override
    public void updateTaskDeleted(Task task) {
        Task.deleteTask(task);
    }

    @Override
    public void updateCookingJobDeleted(CookingJob oldJob) {
        CookingJob.deleteCookingJob(oldJob);
    }

    @Override
    public void updateCookingJobChanged(CookingJob c) {
        CookingJob.updateCookingJob(c);
    }
    @Override
    public void updateShiftChanged(Shift shift) {
        if(shift instanceof KitchenShift)
            KitchenShift.updateShift((KitchenShift) shift);
        else if(shift instanceof ServiceShift)
            ServiceShift.updateShift((ServiceShift) shift);
    }
}
