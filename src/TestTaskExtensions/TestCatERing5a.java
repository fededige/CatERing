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
        CatERing.getInstance().getUserManager().fakeLogin("Marinella");
        System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());
        EventInfo event = EventInfo.loadEventById(3);
        System.out.println(event);
        ServiceInfo service = ServiceInfo.loadServiceInfoForEvent(event.getId()).get(2);
        System.out.println(service);
        SummarySheet s;
        try {
            s = CatERing.getInstance().getKitchenTaskManager().openSummarySheet(event, service);
            System.out.println(s.testString());
            System.out.println("TEST DELETE COOKING JOB");
            Task t = Task.loadTasksBySheetId(s.getId()).get(0);
            System.out.println("Task prima della delete: " + t.testString());
            CookingJob c = CookingJob.loadCookingJobByTaskId(t.getId()).get(0);
            try {
                CatERing.getInstance().getKitchenTaskManager().deleteCookingJob(t, c);
                t = Task.loadTasksBySheetId(s.getId()).get(0);
                System.out.println("Task prima dopo la delete: " + t.testString());
            } catch (UseCaseLogicException e) {
                throw new RuntimeException(e);
            }
        } catch (KitchenException e) {
            System.err.println("SummarySheet inesistente");
        }
    }
}
