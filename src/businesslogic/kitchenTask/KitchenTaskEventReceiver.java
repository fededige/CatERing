package businesslogic.kitchenTask;

public interface KitchenTaskEventReceiver {
    public void updateSheetCreated(SummarySheet currentSheet);

    public void updateSheetDeleted(SummarySheet deleteSheet);
}
