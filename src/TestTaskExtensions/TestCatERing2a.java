package TestTaskExtensions;

import businesslogic.CatERing;
import businesslogic.KitchenException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Recipe;

public class TestCatERing2a {
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


        System.out.println("TEST REMOVE PROCEDURE");
        KitchenProcedure oldKProc = Recipe.loadRecipeById(2);
        try {
            s = CatERing.getInstance().getKitchenTaskManager().removeProcedure(oldKProc);
        } catch (UseCaseLogicException | KitchenException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        System.out.println(s.testString());
    }
}
