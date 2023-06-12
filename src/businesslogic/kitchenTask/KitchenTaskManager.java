package businesslogic.kitchenTask;

import businesslogic.CatERing;
import businesslogic.KitchenException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.shift.KitchenShift;
import businesslogic.user.User;

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
        currentSheet.addProcedure(newKProcs);

        return currentSheet;
    }

//    public SummarySheet removeProcedure(KitchenProcedure oldKProc) throws UseCaseLogicException {
//        if(currentSheet == null){
//            throw new UseCaseLogicException();
//        }
//        currentSheet.removeProcedure(oldKProc);
//        return currentSheet;
//    }

    private void notifyKitchenProceduresAdded(ArrayList<KitchenProcedure> newKProcs) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            for(KitchenProcedure kProc: newKProcs){
                er.updateKitchenProcedureAdded(kProc);
            }
        }

    }

    public CookingJob createNewCookingJob(Task t, KitchenShift kShift, int amount, float estimatedTime) throws UseCaseLogicException, KitchenException {
        if(currentSheet == null){
            throw new UseCaseLogicException();
        }
        if(!currentSheet.hasTask(t)){
            throw new KitchenException();
        }
        CookingJob job = currentSheet.createCookingJob(t, kShift, amount, estimatedTime);
        this.notifyCookingJobAdded(job, t.getId(), kShift.getId());
        return job;
    }

    private void notifyCookingJobAdded(CookingJob job, int task_id, int shift_id) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateCookingJobAdded(job, task_id, shift_id);
        }
    }

    public void deleteCookingJob(Task t, CookingJob oldJob) throws KitchenException, UseCaseLogicException {
        if(currentSheet == null){
            throw new UseCaseLogicException();
        }
        if(!currentSheet.hasTask(t)){
            throw new KitchenException();
        }
        if(!t.hasJob(oldJob)){
            throw new KitchenException();
        }

        currentSheet.deleteCookingJob(t, oldJob);
        notifyCookingJobDeleted(oldJob);
    }

    private void notifyCookingJobDeleted(CookingJob oldJob) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateCookingJobDeleted(oldJob);
        }
    }

    public void modifyCookingJob(Task t, CookingJob c, Integer amount, Float estimatedTime) throws UseCaseLogicException, KitchenException {
        if(currentSheet == null){
            throw new UseCaseLogicException();
        }
        if(!currentSheet.hasTask(t)){
            throw new KitchenException();
        }
        if(!t.hasJob(c)){
            throw new KitchenException();
        }
        currentSheet.modifyCookingJob(t, c, amount, estimatedTime);
        notifyCookingJobChanged(c);
    }
    public void modifyCookingJob(Task t, CookingJob c, Integer amount) throws KitchenException, UseCaseLogicException {
        modifyCookingJob(t, c, amount, null);
    }
    public void modifyCookingJob(Task t, CookingJob c, Float estimatedTime) throws KitchenException, UseCaseLogicException {
        modifyCookingJob(t, c, null, estimatedTime);
    }

    private void notifyCookingJobChanged(CookingJob c) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateCookingJobChanged(c);
        }
    }

    public void addCook(CookingJob c, User cook) throws UseCaseLogicException {
        if(currentSheet == null){
            throw new UseCaseLogicException();
        }
        if(!currentSheet.hasJob(c)){
            throw new UseCaseLogicException();
        }
        try {
            c = currentSheet.addCook(c, cook);
        } catch (KitchenException e) {
            System.err.println("job non trovato");
        }
        notifyCookingJobChanged(c);
    }
}
