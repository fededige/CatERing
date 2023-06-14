package TestTaskExtensions;

import businesslogic.CatERing;
import businesslogic.KitchenException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.CookingJob;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.kitchenTask.Task;

public class TestCatERing5a {
    public static void main(String[] args) throws KitchenException, UseCaseLogicException {
        System.out.println("TEST FAKE LOGIN");
        CatERing.getInstance().getUserManager().fakeLogin("Marinella"); //Lidia
        System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

        System.out.println("\nTEST CREATE SUMMARY SHEET");
        EventInfo event = EventInfo.loadEventById(3);
        ServiceInfo service = ServiceInfo.loadServiceInfoForEvent(event.getId()).get(2);
        service.approveMenu();
        SummarySheet s = CatERing.getInstance().getKitchenTaskManager().createSummarySheet(service, event);
        System.out.println(s.testString());

        System.out.println("TEST DELETE COOKING JOB");
        Task t = Task.loadTasksBySheetId(39).get(0);
        CookingJob c = CookingJob.loadCookingJobByTaskId(t.getId()).get(0);
        try {
            CatERing.getInstance().getKitchenTaskManager().deleteCookingJob(t, c);
        } catch (UseCaseLogicException e) {
            throw new RuntimeException(e);
        }
    }
}
