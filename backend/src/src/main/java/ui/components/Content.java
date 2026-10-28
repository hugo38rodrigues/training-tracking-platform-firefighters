package ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ui.AppContext;
import usecase.DTO.DashboardAgentProfil;
import usecase.exceptions.InvalideAgentsException;

public class Content {
    private static final String PLUS_ICON = "M9 3.75 V14.25 M3.75 9 H14.25";


    public static VBox create(AppContext context) {
        Label title = new Label("Agents");
        
        // create a button
        Button addButton = new Button("Ajouter un agent", IconSvg.createIcon(PLUS_ICON, "btn-add-icon"));
        addButton.getStyleClass().add("btn-add");
        addButton.setGraphicTextGap(8);
        addButton.armedProperty().addListener((obs, before, armed) -> System.out.println("Bouton armé : " + armed));
        addButton.setOnMousePressed(e -> System.out.println("0bis. Pressé — secondaire:"
                + e.isSecondaryButtonDown() + " milieu:" + e.isMiddleButtonDown()));
                addButton.setOnMousePressed(e -> System.out.println("0bis. Maj:" + e.isShiftDown()
                + " Ctrl:" + e.isControlDown() + " Alt:" + e.isAltDown() + " Cmd:" + e.isMetaDown()));
        title.getStyleClass().add("page-title");
        title.setMinWidth(Region.USE_PREF_SIZE);
        Label subtitle = new Label("Profil, grade, compétences et inscriptions de chaque agent");
        subtitle.getStyleClass().add("page-subtitle");
        VBox titleBlock = new VBox(4,title, subtitle);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(titleBlock, spacer, addButton);
        header.setAlignment(Pos.BOTTOM_LEFT);

        // Création des profiles pour les agents
        FlowPane cards = new FlowPane(20, 20); // les cartes passent à la ligne quand la fenêtre est étroite

        Runnable refreshCards = () -> {
            cards.getChildren().clear();
            try {
                for (DashboardAgentProfil profil : context.getAllAgent.execute()) {
                    cards.getChildren().add(new AgentCard(profil));
                }
            } catch (InvalideAgentsException e) {
                cards.getChildren().add(new Label(e.getMessage()));
            }
        };

        refreshCards.run(); // premier affichage

        addButton.setOnAction(event -> {
            System.out.println("1. Clic reçu");
            AddAgentDialog dialog = new AddAgentDialog(context.addAgent, context.getSkills);
            System.out.println("2. Formulaire construit");
            dialog.initOwner(addButton.getScene().getWindow());
            dialog.showAndWait()
                    .filter(result -> result == AddAgentDialog.ADD)
                    .ifPresent(result -> refreshCards.run());
        });

        VBox page = new VBox(24, header);
        page.getStyleClass().add("content");
        page.getChildren().add(cards);
        return page;
    }
}
