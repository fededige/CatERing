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

import java.util.ArrayList;


public class TestCatERing {
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

        System.out.println("\nTEST ADD PROCEDURE");
        KitchenProcedure newKProc = Recipe.loadRecipeById(2);
        CatERing.getInstance().getKitchenTaskManager().addProcedure(newKProc);
        System.out.println(s.testString());

        System.out.println("\nTEST MOVE TASK");
        Task t = Task.loadTaskById(291);
        CatERing.getInstance().getKitchenTaskManager().moveTask(t, 4);
        System.out.println(s.testString());

        System.out.println("TEST GET SHIFT TABLE");
        ArrayList<KitchenShift> sTable = CatERing.getInstance().getKitchenTaskManager().getShiftTable();
        for(KitchenShift kS: sTable){
            System.out.println(kS);
        }

        System.out.println("TEST CREATE COOKING JOB");
        Task task = Task.loadTasksBySheetId(s.getId()).get(0);
        CatERing.getInstance().getKitchenTaskManager().createNewCookingJob(task, KitchenShift.loadKitchenShiftById(1), 0, 0);
        System.out.println(task.testString());

        System.out.println("TEST MODIFY TASK");
        CatERing.getInstance().getKitchenTaskManager().modifyTask(task, 15, (float) 2.2);
        System.out.println(task.testString());

    }
}
