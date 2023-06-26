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
        } catch (KitchenException ignored){}

        ArrayList<KitchenProcedure> kProcedures = service.getRecipies();

        SummarySheet newSheet = new SummarySheet();

        int pos = 0;
        for(KitchenProcedure kitchenProcedure: kProcedures){
            Task newTask = new Task(kitchenProcedure, pos);
            newSheet.addTask(newTask);
            pos++;
        }

        newSheet.addServiceId(service.getId());

        currentSheet = newSheet;

        notifySummarySheetCreated(currentSheet);
        return currentSheet;
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
        if(currentSheet == null){ //questo controllo nel DSD viene svolto allì'inizio. Per come abbiamo implementato Service conviene invece farlo in questo modo
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



    public SummarySheet addProcedure(KitchenProcedure newKProc) throws UseCaseLogicException {
        if(currentSheet == null){
            throw new UseCaseLogicException();
        }


        ArrayList<Task> newTasks = currentSheet.addProcedure(newKProc);
        notifyTasksAdded(currentSheet.getId(), newTasks);
        return currentSheet;
    }


    public CookingJob createNewCookingJob(Task t, KitchenShift kShift, int amount, float estimatedTime) throws UseCaseLogicException, KitchenException {
        if(currentSheet == null){
            throw new UseCaseLogicException();
        }
        if(!currentSheet.hasTask(t)){
            throw new KitchenException();
        }
        CookingJob job = currentSheet.createCookingJob(t, kShift, amount, estimatedTime);
        notifyCookingJobAdded(job, t.getId(), kShift.getId());
        return job;
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

        KitchenShift kShift = currentSheet.deleteCookingJob(t, oldJob);
        notifyCookingJobDeleted(oldJob);
        notifyShiftChanged(kShift);
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
    public void modifyCookingJob(Task t, CookingJob c, int amount) throws KitchenException, UseCaseLogicException {
        modifyCookingJob(t, c, amount, null);
    }
    public void modifyCookingJob(Task t, CookingJob c, float estimatedTime) throws KitchenException, UseCaseLogicException {
        modifyCookingJob(t, c, null, estimatedTime);
    }



    public void addCook(CookingJob c, User cook) throws UseCaseLogicException, KitchenException {
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
            throw new KitchenException();
        }
        notifyCookingJobChanged(c);
    }

    public void modifyTask(Task t, Integer amount, Float estimatedTime) throws UseCaseLogicException, KitchenException {
        if(currentSheet == null){
            throw new UseCaseLogicException();
        }
        if(!currentSheet.hasTask(t)){
            throw new KitchenException();
        }
        try {
            t = currentSheet.changeTask(t, amount, estimatedTime);
        } catch (KitchenException e) {
            System.err.println("task non trovato");
            throw new KitchenException();
        }
        notifyTaskChanged(t);
    }

    public void modifyTask(Task t, int amount) throws UseCaseLogicException, KitchenException {
        this.modifyTask(t, amount, null);
    }

    public void modifyTask(Task t, float estimatedTime) throws UseCaseLogicException, KitchenException {
        this.modifyTask(t, null, estimatedTime);
    }



    public ArrayList<KitchenShift> getShiftTable(){
        return CatERing.getInstance().getShiftManager().getShiftTable();
    }

    public SummarySheet removeProcedure(KitchenProcedure oldProc) throws UseCaseLogicException, KitchenException {
        if(currentSheet == null){
            throw new UseCaseLogicException();
        }
        Task deletedTask;
        try {
            deletedTask = currentSheet.removeProcedure(oldProc);
        } catch (KitchenException e) {
            System.err.println("procedura non trovata");
            throw new KitchenException();
        }
        notifyTaskDeleted(deletedTask);
        return currentSheet;
    }

    public void moveTask(Task t, int pos) throws UseCaseLogicException, KitchenException {
        if(currentSheet == null){
            throw new UseCaseLogicException();
        }
        if(!currentSheet.hasTask(t)){
            throw new KitchenException();
        }
        if(pos < 0 || pos>=currentSheet.lengthTasks()){
            System.err.println("lunghezza out of bound");
            throw new KitchenException();
        }
        ArrayList<Task> changedTask = currentSheet.moveTask(t, pos);
        System.out.println(changedTask);
        notifyTasksChanged(changedTask);
    }
    private void notifyCookingJobChanged(CookingJob c) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateCookingJobChanged(c);
        }
    }

    private void notifyTaskChanged(Task t) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateTaskChanged(t);
        }
    }


    private void notifyShiftChanged(KitchenShift kShift) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateShiftChanged(kShift);
        }
    }

    private void notifyCookingJobDeleted(CookingJob oldJob) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateCookingJobDeleted(oldJob);
        }
    }

    private void notifyTaskDeleted(Task task) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateTaskDeleted(task);
        }
    }
    private void notifyTasksChanged(ArrayList<Task> changedTask) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            for(Task t: changedTask){
                er.updateTaskChanged(t);
            }
        }
    }
    private void notifyKitchenProcedureRemoved(KitchenProcedure oldProc) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateKitchenProcedureRemoved(oldProc);
        }
    }
    private void notifySummarySheetCreated(SummarySheet currentSheet) {
        for(KitchenTaskEventReceiver kTaskReceiver: eventReceivers){
            kTaskReceiver.updateSheetCreated(currentSheet);
        }
    }

    private void notifyTasksAdded(int sheet_id, ArrayList<Task> newTasks) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateTasksAdded(sheet_id, newTasks);
        }
    }

    private void notifyCookingJobAdded(CookingJob job, int task_id, int shift_id) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateCookingJobAdded(job, task_id, shift_id);
        }
    }

    private void notifySummarySheetDeleted(SummarySheet summarySheet) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            er.updateSheetDeleted(summarySheet);
        }
    }
    private void notifyKitchenProceduresAdded(ArrayList<KitchenProcedure> newKProcs) {
        for(KitchenTaskEventReceiver er: this.eventReceivers){
            for(KitchenProcedure kProc: newKProcs){
                er.updateKitchenProcedureAdded(kProc);
            }
        }
    }
}
