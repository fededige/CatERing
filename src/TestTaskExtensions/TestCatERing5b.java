package TestTaskExtensions;

import businesslogic.CatERing;
import businesslogic.KitchenException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.CookingJob;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.kitchenTask.Task;
import businesslogic.shift.KitchenShift;

public class TestCatERing5b {
    public static void main(String[] args) throws KitchenException, UseCaseLogicException {
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

            Task task = Task.loadTasksBySheetId(s.getId()).get(0);
            CookingJob j = CatERing.getInstance().getKitchenTaskManager().createNewCookingJob(task, KitchenShift.loadKitchenShiftById(1), 0, 0);

            s = CatERing.getInstance().getKitchenTaskManager().getCurrentSheet();
            System.out.println(s.testString());


            System.out.println("TEST MODIFY COOKING JOB ALL");
            task = Task.loadTasksBySheetId(s.getId()).get(0);
            CatERing.getInstance().getKitchenTaskManager().modifyCookingJob(task, j, 5, (float) 13.3);
            System.out.println(s.testString());

            System.out.println("TEST MODIFY COOKING JOB ONLY AMOUNT");
            CatERing.getInstance().getKitchenTaskManager().modifyCookingJob(task, j, 3);
            System.out.println(s.testString());


            System.out.println("TEST MODIFY COOKING JOB ONLY ESTIMATED TIME");
            CatERing.getInstance().getKitchenTaskManager().modifyCookingJob(task, j, (float) 2.1);
            System.out.println(s.testString());

        } catch (KitchenException e) {
            System.err.println("SummarySheet inesistente");
        }
    }
}
