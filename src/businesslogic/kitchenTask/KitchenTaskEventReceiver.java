package businesslogic.kitchenTask;

import businesslogic.recipe.KitchenProcedure;

public interface KitchenTaskEventReceiver {
    public void updateSheetCreated(SummarySheet currentSheet);

    public void updateSheetDeleted(SummarySheet deleteSheet);

    public void updateKitchenProcedureAdded(KitchenProcedure kProc);
}
