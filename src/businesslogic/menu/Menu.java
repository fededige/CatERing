package businesslogic.menu;

import businesslogic.user.User;

import java.util.ArrayList;

public class Menu {
    private String title;
    private boolean published;
    private boolean inUse;
    private String[] features;
    private boolean[] featureValues;
    private ArrayList<MenuItem> freeItems;
    private ArrayList<Section> sections;

    private User owner;

    public Menu(User user, String title, String[] menuFeatures) {
        if (title != null) {
            this.title = title;
        }

        this.owner = user;

        this.features = menuFeatures;

        this.sections = new ArrayList<>();
        this.freeItems = new ArrayList<>();

        this.featureValues = new boolean[menuFeatures.length];
    }

    public String toString() {
        String result = title + "\n";
        for (int i=0; i < features.length; i++) {
            result += features[i] + ": " + featureValues[i] + "\n";
        }

        result += "\n";
        for (Section sec: sections) {
            result += sec.toString();
            result += "\n";
        }

        if (freeItems.size() > 0) {
            result+="\n" + "VOCI LIBERE:";
        }
        for (MenuItem mi: freeItems) {
            result += mi.toString();
        }

        return result;
    }

    public String getTitle() {
        return this.title;
    }
}
