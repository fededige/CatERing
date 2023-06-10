package persistence;

import businesslogic.kitchenTask.CookingJob;
import businesslogic.kitchenTask.KitchenTaskEventReceiver;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Recipe;

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
    public void updateCookingJobAdded(CookingJob job, int task_id) {
        CookingJob.saveNewCookingJob(job, task_id);
    }
}
