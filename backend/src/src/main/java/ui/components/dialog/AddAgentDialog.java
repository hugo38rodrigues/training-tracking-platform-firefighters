package ui.components.dialog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import domaine.entity.Agent;
import domaine.entity.License;
import domaine.entity.Rank;
import domaine.entity.Skill;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import ui.DisplayNames;
import usecase.AddAgent;
import usecase.GetSkills;


public class AddAgentDialog extends Dialog<ButtonType> {

    public static final ButtonType ADD = new ButtonType("Ajouter", ButtonData.OK_DONE);

    private final AddAgent addAgent;

    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final TextField matricule = new TextField();
    private final ComboBox<Rank> rank = new ComboBox<>();
    private final DatePicker rankStartDate = new DatePicker(LocalDate.now());
    private final Map<License, CheckBox> licenses = new EnumMap<>(License.class);
    private final Map<Skill, CheckBox> skills = new LinkedHashMap<>();
    private final Label error = new Label();

    public AddAgentDialog(AddAgent addAgent, GetSkills getSkills) {
        this.addAgent = addAgent;
        setTitle("Ajouter un agent");

        configureMatricule();
        configureRank();

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(12);
        form.addRow(0, label("Prénom", firstName), firstName);
        form.addRow(1, label("Nom", lastName), lastName);
        form.addRow(2, label("Matricule", matricule), matricule);
        form.addRow(3, label("Grade", rank), rank);
        form.addRow(4, label("Dans le grade depuis le", rankStartDate), rankStartDate);
        form.addRow(5, new Label("Permis"), createLicenseBoxes());
        form.addRow(6, new Label("Compétences"), createSkillBoxes(getSkills.execute()));

        error.getStyleClass().add("form-error");
        error.setWrapText(true);
        error.setMaxWidth(480);

        getDialogPane().setContent(new VBox(16, form, error));
        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ADD);
        getDialogPane().getStyleClass().add("form-dialog");
        getDialogPane().getStylesheets().add(
                getClass().getResource("/ui/styles.css").toExternalForm());

        // Au clic sur "Ajouter" : on tente l'ajout AVANT que la fenêtre se ferme
        Button addButton = (Button) getDialogPane().lookupButton(ADD);
        addButton.addEventFilter(ActionEvent.ACTION, this::submit);
    }

    private void submit(ActionEvent event) {
        try {
            addAgent.execute(buildAgent());
        } catch (RuntimeException e) {
            error.setText(e.getMessage());
            event.consume(); // annule la fermeture : le formulaire reste ouvert
        }
    }

    private Agent buildAgent() {
        if (firstName.getText().isBlank() || lastName.getText().isBlank()) {
            throw new IllegalArgumentException("Le prénom et le nom sont obligatoires.");
        }
        if (matricule.getText().isEmpty()) {
            throw new IllegalArgumentException("Le matricule est obligatoire.");
        }
        if (rankStartDate.getValue() == null) {
            throw new IllegalArgumentException("La date d'entrée dans le grade est obligatoire.");
        }

        List<License> selectedLicenses = new ArrayList<>();
        licenses.forEach((license, box) -> {
            if (box.isSelected())
                selectedLicenses.add(license);
        });

        List<Skill> selectedSkills = new ArrayList<>();
        skills.forEach((skill, box) -> {
            if (box.isSelected())
                selectedSkills.add(skill);
        });

        return new Agent(
                firstName.getText().trim(),
                lastName.getText().trim(),
                Integer.parseInt(matricule.getText()),
                rank.getValue(),
                rankStartDate.getValue(),
                selectedLicenses,
                selectedSkills);
    }

    private void configureMatricule() {
        matricule.setTextFormatter(
                new TextFormatter<String>(change -> change.getControlNewText().matches("\\d{0,7}") ? change : null));
    }

    private void configureRank() {
        rank.getItems().addAll(Rank.values());
        rank.setValue(Rank.SAPEUR_DEUXIEME_CLASSE);
        rank.setMaxWidth(Double.MAX_VALUE);
        rank.setConverter(new StringConverter<>() {
            @Override
            public String toString(Rank value) {
                return value == null ? "" : DisplayNames.rank(value);
            }

            @Override
            public Rank fromString(String text) {
                return null;
            }
        });
    }

    private HBox createLicenseBoxes() {
        HBox boxes = new HBox(16);
        for (License license : License.values()) {
            CheckBox box = new CheckBox(DisplayNames.license(license));
            licenses.put(license, box);
            boxes.getChildren().add(box);
        }
        return boxes;
    }

    private ScrollPane createSkillBoxes(List<Skill> allSkills) {
        FlowPane boxes = new FlowPane(16, 8);
        boxes.setPrefWrapLength(360);
        for (Skill skill : allSkills) {
            CheckBox box = new CheckBox(skill.getName());
            skills.put(skill, box);
            boxes.getChildren().add(box);
        }
        ScrollPane scroll = new ScrollPane(boxes);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(140);
        return scroll;
    }

    private Label label(String text, Node field) {
        Label label = new Label(text);
        label.setLabelFor(field);
        return label;
    }
}