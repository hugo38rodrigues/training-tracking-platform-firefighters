package ui.components.dialog;

import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import ui.DisplayNames;
import usecase.DTO.DashboardAgentProfil;
import usecase.DTO.TrainingOption;
import usecase.GetAvailableTrainings;
import usecase.registration.SetRegistrationAgentTraining;

public class RegisterAgent extends Dialog<ButtonType> {

    public static final ButtonType REGISTER = new ButtonType("Inscrire", ButtonData.OK_DONE);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DashboardAgentProfil profil;
    private final SetRegistrationAgentTraining registerAgent;
    private final ListView<TrainingOption> trainings = new ListView<>();
    private final Label error = new Label();

    public RegisterAgent(DashboardAgentProfil profil,
            GetAvailableTrainings getAvailableTrainings,
            SetRegistrationAgentTraining registerAgent) {
        this.profil = profil;
        this.registerAgent = registerAgent;
        setTitle("Inscrire un agent à une formation");

        trainings.getItems().setAll(getAvailableTrainings.execute(profil.id()));
        trainings.setCellFactory(list -> new TrainingCell());
        trainings.setPlaceholder(new Label("Aucune formation accessible avec les compétences de cet agent."));
        trainings.getStyleClass().add("training-list");

        Label listTitle = new Label("FORMATIONS ACCESSIBLES");
        listTitle.getStyleClass().add("section-title");

        error.getStyleClass().add("form-error");
        error.setWrapText(true);
        error.setMaxWidth(440);

        VBox content = new VBox(16, createAgentSummary(), new VBox(8, listTitle, trainings), error);
        content.setPrefWidth(460);

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, REGISTER);
        getDialogPane().getStyleClass().add("form-dialog");
        getDialogPane().getStylesheets().add(
                getClass().getResource("/ui/styles.css").toExternalForm());

        Button registerButton = (Button) getDialogPane().lookupButton(REGISTER);
        // "Inscrire" reste grisé tant qu'aucune formation n'est sélectionnée
        registerButton.disableProperty().bind(
                trainings.getSelectionModel().selectedItemProperty().isNull());
        registerButton.addEventFilter(ActionEvent.ACTION, this::submit);
    }

    // Rappel de l'agent concerné, en lecture seule
    private VBox createAgentSummary() {
        Label name = new Label(profil.lastName().toUpperCase() + " " + profil.firstName());
        name.getStyleClass().add("card-name");
        Label details = new Label("Matricule " + profil.matricule() + " · " + DisplayNames.rank(profil.rank()));
        details.getStyleClass().add("card-muted");

        FlowPane skills = new FlowPane(6, 6);
        for (String skill : profil.skills()) {
            Label chip = new Label(skill);
            chip.getStyleClass().add("chip");
            skills.getChildren().add(chip);
        }

        VBox summary = new VBox(10, new VBox(2, name, details), skills);
        summary.getStyleClass().add("rank-block");
        return summary;
    }

    private void submit(ActionEvent event) {
        TrainingOption selected = trainings.getSelectionModel().getSelectedItem();
        try {
            registerAgent.execute(profil.id(), selected.id()); // À VÉRIFIER : ordre (agent, formation)
        } catch (RuntimeException e) {
            error.setText(e.getMessage());
            event.consume();
        }
    }

    // Comment afficher UNE formation dans la liste
    private static class TrainingCell extends ListCell<TrainingOption> {
        @Override
        protected void updateItem(TrainingOption training, boolean empty) {
            super.updateItem(training, empty);
            if (empty || training == null) {
                setText(null);
                setGraphic(null);
                return;
            }
            Label name = new Label(training.name());
            name.getStyleClass().add("registration-name");
            Label dates = new Label("Du " + training.startDate().format(DATE_FORMAT)
                    + " au " + training.endDate().format(DATE_FORMAT));
            dates.getStyleClass().add("card-muted");
            setText(null);
            setGraphic(new VBox(2, name, dates));
        }
    }
}