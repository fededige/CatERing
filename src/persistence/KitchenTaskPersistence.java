package persistence;

import businesslogic.kitchenTask.KitchenTaskEventReceiver;
import businesslogic.kitchenTask.SummarySheet;

public class KitchenTaskPersistence implements KitchenTaskEventReceiver {

    @Override
    public void updateSheetCreated(SummarySheet currentSheet) {
        SummarySheet.saveNewSheet(currentSheet);
    }

    @Override
    public void updateSheetDeleted(SummarySheet deleteSheet) {
        SummarySheet.deleteSummarySheet(deleteSheet);
    }
}
