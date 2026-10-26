package ui.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Sidebard {
    
    public static VBox create() {
        // Le carré rouge du logo : une Region est un nœud vide, qu'on dessine en CSS
        Region logo = new Region();
        logo.getStyleClass().add("logo");

        Label title = new Label("Suivi des formations");
        title.setMinWidth(Region.USE_PREF_SIZE);
        title.getStyleClass().add("brand-title");
        Label subtitle = new Label("Sapeurs-pompiers");
        subtitle.getStyleClass().add("brand-subtitle");

        // VBox(espacement, enfants...) : le titre au-dessus du sous-titre
        VBox brandText = new VBox(2, title, subtitle);
        // HBox : le logo à gauche du texte
        HBox brand = new HBox(12, logo, brandText);

        VBox nav = new VBox(4,
                NavButton.create("Agents", true),
                NavButton.create("Formations", false),
                NavButton.create("Inscriptions", false));

        VBox sidebar = new VBox(32, brand, nav);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(232);
        return sidebar;
    }
}
