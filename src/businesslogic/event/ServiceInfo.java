package businesslogic.event;

import businesslogic.kitchenTask.SummarySheet;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.KitchenProcedure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextFormatter;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Objects;

public class ServiceInfo implements EventItemInfo {
    private int id;
    private String name;
    private Date date;
    private Time timeStart;
    private Time timeEnd;
    private int participants;
    private String state;
    private Menu menu;
    private SummarySheet sheet;
    private ArrayList<Change> changes;
    public ServiceInfo(String name) {
        this.name = name;
        this.state = "planned";
    }


    public String toString() {
        return name + ": " + date + " (" + timeStart + "-" + timeEnd + "), " + participants + " pp.";
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<ServiceInfo> loadServiceInfoForEvent(int event_id) {
        ObservableList<ServiceInfo> result = FXCollections.observableArrayList();
        String query = "SELECT id, name, service_date, time_start, time_end, expected_participants " +
                "FROM Services WHERE event_id = " + event_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String s = rs.getString("name");
                ServiceInfo serv = new ServiceInfo(s);
                serv.id = rs.getInt("id");
                serv.date = rs.getDate("service_date");
                serv.timeStart = rs.getTime("time_start");
                serv.timeEnd = rs.getTime("time_end");
                serv.participants = rs.getInt("expected_participants");
                result.add(serv);
            }
        });

        return result;
    }

    public boolean isConfirmed() {
        return this.state.equals("confirmed");
    }

    public ArrayList<KitchenProcedure> getRecipies() {

        ArrayList<KitchenProcedure> kProcedures = new ArrayList<>();
        for(Section section: menu.getSections()){
            for(MenuItem menuItem: section.getItems()){
                for(Change change: changes){
                    if(change.isAddition() && change.getMenuItem().equals(menuItem)){
                        kProcedures.add(menuItem.getItemRecipe());
                        ArrayList<KitchenProcedure> subProcedures = menuItem.getItemRecipe().getProcedures();
                        kProcedures.addAll(subProcedures);
                    }
                }
            }
        }

        for(MenuItem freeItem: menu.getFreeItems()){
            for(Change change: changes){
                if(change.isAddition() && change.getMenuItem().equals(freeItem)){
                    kProcedures.add(freeItem.getItemRecipe());
                    ArrayList<KitchenProcedure> subProcedures = freeItem.getItemRecipe().getProcedures();
                    kProcedures.addAll(subProcedures);
                }
            }
        }

        for(Change change: changes){
            if(change.isAddition()){
                ArrayList<KitchenProcedure> subProcedures = change.getMenuItem().getItemRecipe().getProcedures();
                kProcedures.addAll(subProcedures);
            }
        }

        return kProcedures;
    }

    public void addSummarySheet(SummarySheet sheet) {
        this.sheet = sheet;
    }
}
