package businesslogic.event;

import businesslogic.KitchenException;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.menu.*;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Preparation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

public class ServiceInfo implements EventItemInfo {
    private int id;
    private String name;
    private Date date;
    private Time timeStart;
    private Time timeEnd;
    private int participants;
    private String state;
    private Menu menu;
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
        String query = "SELECT * " +
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
                int menu_id = rs.getInt("proposed_menu_id");
                if(menu_id == 0)
                    menu_id = rs.getInt("approved_menu_id");
                try {
                    if(menu_id != 0)
                        serv.menu = Menu.loadMenuById(menu_id);
                    else
                        serv.menu = null;
                } catch (MenuException e) {
                    serv.menu = null;
                }
                result.add(serv);
            }
        });

        return result;
    }

    public boolean isActive() {
        return this.state.equals("active");
    }

    public void approveMenu(){
        state = "active";
    }

    public ArrayList<KitchenProcedure> getRecipies() {
        ArrayList<KitchenProcedure> kProcedures = new ArrayList<>();
        // Aggiungiamo le ricette da svolgere che non sono contenute nell'array changes
        for(Section section: menu.getSections()){
            for(MenuItem menuItem: section.getItems()){
                if(changes == null || !toRemove(menuItem)){
                    kProcedures.add(menuItem.getItemRecipe());
                    ArrayList<Preparation> subProcedures = menuItem.getItemRecipe().getProcedures();
                    kProcedures.addAll(subProcedures);
                }
            }
        }

        // aggiungiamo elementi fuori menu
        for(MenuItem freeItem: menu.getFreeItems()){
            if(changes == null || !toRemove(freeItem)) {
                kProcedures.add(freeItem.getItemRecipe());
                ArrayList<Preparation> subProcedures = freeItem.getItemRecipe().getProcedures();
                kProcedures.addAll(subProcedures);
            }
        }

        // aggiungiamo ricette con proposta di modifica
        if(changes != null) {
            for (Change change : changes) {
                if (change.isAddition()) {
                    ArrayList<Preparation> subProcedures = change.getMenuItem().getItemRecipe().getProcedures();
                    kProcedures.addAll(subProcedures);
                }
            }
        }


        return kProcedures;
    }

    private boolean toRemove(MenuItem menuItem) {
        for(Change change: changes){
            if(change.isAddition() && change.getMenuItem().getId() == menuItem.getId()){
                return true;
            }
        }
        return false;
    }

    public boolean isPlanned() {
        return state.equals("planned");
    }

    public int getId(){
        return id;
    }
}
