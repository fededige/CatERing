package persistence;

import businesslogic.kitchenTask.KitchenTaskEventReceiver;
import businesslogic.kitchenTask.SummarySheet;

public class KitchenTaskPersistence implements KitchenTaskEventReceiver {

    @Override
    public void updateSheetCreated(SummarySheet currentSheet) {
        SummarySheet.saveNewSheet(currentSheet);
    }
}
