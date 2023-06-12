package businesslogic.kitchenTask;

import businesslogic.recipe.KitchenProcedure;

public interface KitchenTaskEventReceiver {
    public void updateSheetCreated(SummarySheet currentSheet);

    public void updateSheetDeleted(SummarySheet deleteSheet);

    public void updateKitchenProcedureAdded(KitchenProcedure kProc);

    void updateCookingJobAdded(CookingJob job, int task_id, int shift_id);

    void updateCookingJobDeleted(CookingJob oldJob);

    void updateCookingJobChanged(CookingJob c);

    void updateTaskChanged(Task t);
}
