package ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ui.AppContext;
import ui.components.AgentCard;
import ui.components.IconSvg;
import ui.components.dialog.AddAgentDialog;
import ui.components.dialog.RegisterAgent;
import usecase.DTO.DashboardAgentProfil;
import usecase.exceptions.InvalideAgentsException;

public class Content extends VBox {
    private static final String PLUS_ICON = "M9 3.75 V14.25 M3.75 9 H14.25";

    private final AppContext context;
    private final FlowPane cards = new FlowPane(20, 20);

    public Content(AppContext context) {
        super(24);
        this.context = context;
        getStyleClass().add("content");

        Label title = new Label("Agents");
        title.getStyleClass().add("page-title");
        title.setMinWidth(Region.USE_PREF_SIZE);
        Label subtitle = new Label("Profil, grade, compétences et inscriptions de chaque agent");
        subtitle.getStyleClass().add("page-subtitle");
        VBox titleBlock = new VBox(4, title, subtitle);

        Button addButton = new Button("Ajouter un agent", IconSvg.createIcon(PLUS_ICON, "btn-add-icon"));
        addButton.getStyleClass().add("btn-add");
        addButton.setGraphicTextGap(8);
        addButton.setOnAction(event -> openAddAgent());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(titleBlock, spacer, addButton);
        header.setAlignment(Pos.BOTTOM_LEFT);

        cards.setPadding(new Insets(0, 0, 24, 0));
        ScrollPane scroll = new ScrollPane(cards);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("cards-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(header, scroll);
        refreshCards();
    }

    private void refreshCards() {
        cards.getChildren().clear();
        try {
            for (DashboardAgentProfil profil : context.getAllAgent.execute()) {
                cards.getChildren().add(new AgentCard(profil, this::openRegistration));
            }
        } catch (InvalideAgentsException e) {
            cards.getChildren().add(new Label(e.getMessage()));
        }
    }

    private void openAddAgent() {
        AddAgentDialog dialog = new AddAgentDialog(context.addAgent, context.getSkills);
        dialog.initOwner(getScene().getWindow());
        dialog.showAndWait()
                .filter(result -> result == AddAgentDialog.ADD)
                .ifPresent(result -> refreshCards());
    }

    private void openRegistration(DashboardAgentProfil profil) {
        RegisterAgent dialog = new RegisterAgent(
                profil, context.getAvailableTrainings, context.registerAgent);
        dialog.initOwner(getScene().getWindow());
        dialog.showAndWait()
                .filter(result -> result == RegisterAgent.REGISTER)
                .ifPresent(result -> refreshCards());
    }
}