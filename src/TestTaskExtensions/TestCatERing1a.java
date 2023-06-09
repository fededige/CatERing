package TestTaskExtensions;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.SummarySheet;

public class TestCatERing1a {
    public static void main(String[] args) throws UseCaseLogicException {
        CatERing.getInstance().getUserManager().fakeLogin("Lidia");
        System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());
        EventInfo event = EventInfo.loadEventById(1);
        System.out.println(event);
        ServiceInfo service = ServiceInfo.loadServiceInfoForEvent(event.getId()).get(0);
        System.out.println(service);
        SummarySheet s = CatERing.getInstance().getKitchenTaskManager().openSummarySheet(event, service);
        System.out.println(s.testString());
    }
}
