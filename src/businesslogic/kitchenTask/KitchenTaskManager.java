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
    private static SummarySheet currentSheet;
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
            currentSheet = existingSheet;
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
        this.notifySummarySheetDeleted(summarySheet);
    }

    private void notifySummarySheetDeleted(SummarySheet summarySheet) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateSheetDeleted(summarySheet);
        }
    }

    public SummarySheet addProcedure(KitchenProcedure newKProc) throws UseCaseLogicException {
        if(currentSheet == null){
            throw new UseCaseLogicException();
        }
        ArrayList<KitchenProcedure> newKProcs = newKProc.getProcedures();
        newKProcs.add(newKProc);
//        this.notifyKitchenProceduresAdded(newKProcs);
        currentSheet.addProcedure(newKProcs);

        return currentSheet;
    }

    private void notifyKitchenProceduresAdded(ArrayList<KitchenProcedure> newKProcs) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            for(KitchenProcedure kProc: newKProcs){
                er.updateKitchenProcedureAdded(kProc);
            }
        }

    }

    public SummarySheet getSummarySheet() {
        return this.currentSheet;
    }
}
