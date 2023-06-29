package TestTaskExtensions;

import businesslogic.CatERing;
import businesslogic.KitchenException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.SummarySheet;

public class TestCatERing1a {
    public static void main(String[] args) throws UseCaseLogicException {
        System.out.println("TEST OPEN SUMMARY SHEET");
        CatERing.getInstance().getUserManager().fakeLogin("Lidia");
        System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());
        EventInfo event = EventInfo.loadEventById(1);
        System.out.println(event);
        ServiceInfo service = ServiceInfo.loadServiceInfoForEvent(event.getId()).get(0);
        System.out.println(service);
        SummarySheet s = null;
        try {
            s = CatERing.getInstance().getKitchenTaskManager().openSummarySheet(event, service);
            System.out.println(s.testString());
        } catch (KitchenException e) {
            System.err.println("SummarySheet inesistente");
        }
    }
}
