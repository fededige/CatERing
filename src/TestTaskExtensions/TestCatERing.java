package TestTaskExtensions;

import businesslogic.CatERing;
import businesslogic.KitchenException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.kitchenTask.Task;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Recipe;
import businesslogic.shift.KitchenShift;


public class TestCatERing {
    public static void main(String[] args) throws KitchenException, UseCaseLogicException {
        System.out.println("TEST FAKE LOGIN");
        CatERing.getInstance().getUserManager().fakeLogin("Lidia");
        System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

        System.out.println("\nTEST CREATE SUMMARY SHEET");
        EventInfo event = EventInfo.loadAllEventInfo().get(0);
        ServiceInfo service = ServiceInfo.loadServiceInfoForEvent(event.getId()).get(0);
        service.approveMenu();
        SummarySheet s = CatERing.getInstance().getKitchenTaskManager().createSummarySheet(service, event);
        System.out.println(s.testString());

//        System.out.println("\nTEST ADD PROCEDURE");
//        KitchenProcedure newKProc = Recipe.loadRecipeById(14);
//        CatERing.getInstance().getKitchenTaskManager().addProcedure(newKProc);
//        System.out.println(s.testString());

        System.out.println("TEST CREATE COOKING JOB");
        Task task = Task.loadTasksBySheetId(s.getId()).get(0);
        CatERing.getInstance().getKitchenTaskManager().createNewCookingJob(task, new KitchenShift(1), 0, 0);
    }

}
