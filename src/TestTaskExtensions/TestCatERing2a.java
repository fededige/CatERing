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
        System.out.println("TEST OPEN SUMMARY SHEET");
        CatERing.getInstance().getUserManager().fakeLogin("Lidia");
        System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());
        EventInfo event = EventInfo.loadEventById(1);
        System.out.println(event);
        ServiceInfo service = ServiceInfo.loadServiceInfoForEvent(event.getId()).get(0);
        System.out.println(service);
        SummarySheet s;
        try {
            s = CatERing.getInstance().getKitchenTaskManager().openSummarySheet(event, service);
            System.out.println(s.testString());
            System.out.println("TEST REMOVE PROCEDURE");
            KitchenProcedure oldKProc = Recipe.loadRecipeById(9);
            try {
                s = CatERing.getInstance().getKitchenTaskManager().removeProcedure(oldKProc);
            } catch (UseCaseLogicException | KitchenException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            System.out.println(s.testString());

        } catch (KitchenException e) {
            System.err.println("SummarySheet inesistente");
        }



    }
}
