package TestTaskExtensions;

import businesslogic.CatERing;
import businesslogic.KitchenException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.kitchenTask.SummarySheet;

public class TestCatERing1b {
    public static void main(String[] args) throws UseCaseLogicException {
        CatERing.getInstance().getUserManager().fakeLogin("Lidia");
        System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());
        EventInfo event = EventInfo.loadEventById(1);
        System.out.println(event);
        SummarySheet sheet = null;
        try {
            sheet = SummarySheet.loadSummarySheetById(29);
            CatERing.getInstance().getKitchenTaskManager().deleteSummarySheet(event, sheet);
        } catch (KitchenException e) {
            System.err.println("SummarySheet inesistente");
        }
    }
}
