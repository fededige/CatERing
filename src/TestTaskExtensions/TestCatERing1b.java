package TestTaskExtensions;

import businesslogic.CatERing;
import businesslogic.KitchenException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.SummarySheet;

public class TestCatERing1b {
    public static void main(String[] args) throws UseCaseLogicException {
        System.out.println("TEST DELETE SUMMARY SHEET");
        CatERing.getInstance().getUserManager().fakeLogin("Lidia");
        System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());
        EventInfo event = EventInfo.loadEventById(1);
        System.out.println(event);
        SummarySheet sheet = null;
        try {
            sheet = SummarySheet.loadSummarySheetById(30);
            CatERing.getInstance().getKitchenTaskManager().deleteSummarySheet(event, sheet);
            System.out.println("OPENING DELETE SUMMARY SHEET TO CHECK IF IT WORKED");
            try{
                ServiceInfo service = ServiceInfo.loadServiceInfoForEvent(event.getId()).get(0);
                CatERing.getInstance().getKitchenTaskManager().openSummarySheet(event, service);
                System.out.println("DELETE SUMMARY SHEET DIDN'T WORK");
            }catch (KitchenException e){
                System.out.println("DELETE SUMMARY SHEET WORKED");
            }
        } catch (KitchenException e) {
            System.err.println("SummarySheet inesistente");
        }
    }
}
