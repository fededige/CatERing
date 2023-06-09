package businesslogic.kitchenTask;

import businesslogic.CatERing;
import businesslogic.KitchenException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.user.User;
import persistence.PersistenceManager;

import java.util.ArrayList;

public class KitchenTaskManager {
    private SummarySheet currentSheet;
    private ArrayList<KitchenTaskEventReceiver> eventReceivers;

    public KitchenTaskManager() {
        eventReceivers = new ArrayList<>();
    }

    public SummarySheet createSummarySheet(ServiceInfo service, EventInfo event) throws UseCaseLogicException, KitchenException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef()){
            throw new UseCaseLogicException();
        }
        if (event.getChef() != user) {
            throw new UseCaseLogicException();
        }
        if (!(event.containsService(service))){
            throw new UseCaseLogicException();
        }
        if(!service.isActive()){
            throw new KitchenException();
        }
        SummarySheet existingSheet = null;
        try {
            existingSheet = SummarySheet.loadSummarySheetByServiceId(service.getId());
            return existingSheet;
        } catch (KitchenException e){};

        ArrayList<KitchenProcedure> kProcedures = service.getRecipies();

        SummarySheet newSheet = new SummarySheet();

        for(KitchenProcedure kitchenProcedure: kProcedures){
            Task newTask = new Task(kitchenProcedure);
            newSheet.addTask(newTask);
        }

        newSheet.addServiceId(service.getId());

        currentSheet = newSheet;

        notifySummarySheetCreated(currentSheet);
        return currentSheet;
    }

    private void notifySummarySheetCreated(SummarySheet currentSheet) {
        for(KitchenTaskEventReceiver kTaskReceiver: eventReceivers){
            kTaskReceiver.updateSheetCreated(currentSheet);
        }
    }

    public void addEventReceiver(KitchenTaskEventReceiver rec) {
        this.eventReceivers.add(rec);
    }

    public SummarySheet openSummarySheet(EventInfo event, ServiceInfo service) throws UseCaseLogicException, KitchenException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef()){
            throw new UseCaseLogicException();
        }
        if(!event.containsService(service)){
            throw new UseCaseLogicException();
        }
        if (event.getChef() != user) {
            throw new UseCaseLogicException();
        }
        currentSheet = SummarySheet.loadSummarySheetByServiceId(service.getId());
        if(currentSheet == null){
            throw new KitchenException();
        }
        return currentSheet;
    }

    public void deleteSummarySheet(EventInfo event, SummarySheet summarySheet) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef()){
            throw new UseCaseLogicException();
        }
        if (event.getChef() != user) {
            throw new UseCaseLogicException();
        }

        String delTasks = "DELETE FROM tasks WHERE summarysheet_id = " + summarySheet.getId();
        PersistenceManager.executeUpdate(delTasks);

        String del = "DELETE FROM SummarySheets WHERE id = " + summarySheet.getId();
        PersistenceManager.executeUpdate(del);
    }
}
