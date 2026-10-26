package ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ui.AppContext;
import usecase.DTO.DashboardAgentProfil;
import usecase.exceptions.InvalideAgentsException;

public class Content {
    public static VBox create(AppContext context) {
        Label title = new Label("Agents");
        
         // create a button
        Button b = new Button("Ajouter un agent");
        b.getStyleClass().add("btn-add");

        title.getStyleClass().add("page-title");
        title.setMinWidth(Region.USE_PREF_SIZE);
        Label subtitle = new Label("Profil, grade, compétences et inscriptions de chaque agent");
        subtitle.getStyleClass().add("page-subtitle");
        VBox titleBlock = new VBox(4,title, subtitle);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(titleBlock, spacer, b);
        header.setAlignment(Pos.BOTTOM_LEFT);

        // Création des profiles pour les agents
        VBox cards = new VBox(16); // deviendra une grille à l'étape suivante
        try {
            for (DashboardAgentProfil profil : context.getAllAgent.execute()) {
                cards.getChildren().add(new AgentCard(profil));
            }
        } catch (InvalideAgentsException e) {
            cards.getChildren().add(new Label(e.getMessage()));
        }

        VBox page = new VBox(24, header);
        page.getStyleClass().add("content");
        page.getChildren().add(cards);
        return page;
    }
}
