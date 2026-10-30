package ui.components;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import domaine.entity.Rank;
import domaine.entity.RegistrationStatus;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import ui.DisplayNames;
import usecase.DTO.DashBoardRegistration;
import usecase.DTO.DashboardAgentProfil;

public class AgentCard extends VBox {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int MAX_SKILLS = 5;
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("dd/MM");
    private static final int MAX_REGISTRATIONS = 2;
    // Insignes de grade, dessinés dans un carré de 24 × 24
    private static final String PATCH = "M0 3 Q0 0 3 0 H21 Q24 0 24 3 V21 Q24 24 21 24 H3 Q0 24 0 21 Z";
    private static final String SLANT_1 = "M7.5 18 L10.5 18 L16.5 6 L13.5 6 Z";
    private static final String SLANT_2 = "M5.25 18 L8.25 18 L14.25 6 L11.25 6 Z M9.75 18 L12.75 18 L18.75 6 L15.75 6 Z";
    private static final String SLANT_3 = "M3 18 L6 18 L12 6 L9 6 Z M7.5 18 L10.5 18 L16.5 6 L13.5 6 Z M12 18 L15 18 L21 6 L18 6 Z";
    private static final String SLANT_3_GAPS = "M6.35 18 L7.15 18 L13.15 6 L12.35 6 Z M10.85 18 L11.65 18 L17.65 6 L16.85 6 Z";
    private static final String SERGENT_EDGE = "M7.5 18 L8.3 18 L14.3 6 L13.5 6 Z";
    private static final String SERGENT_STRIPE = "M8.3 18 L10.5 18 L16.5 6 L14.3 6 Z";
    private static final String BAR = "M4 10 H20 V14 H4 Z";
    private static final String BAR_LINE = "M4 11.7 H20 V12.3 H4 Z";
    private static final String BARS_2 = "M4 8 H20 V11 H4 Z M4 13 H20 V16 H4 Z";
    private static final String BARS_3 = "M4 6.75 H20 V9.25 H4 Z M4 10.75 H20 V13.25 H4 Z M4 14.75 H20 V17.25 H4 Z";
    private static final String BARS_4 = "M4 5.75 H20 V7.75 H4 Z M4 9.25 H20 V11.25 H4 Z M4 12.75 H20 V14.75 H4 Z M4 16.25 H20 V18.25 H4 Z";
    private static final String BARS_5_ODD = "M4 5.1 H20 V6.9 H4 Z M4 11.1 H20 V12.9 H4 Z M4 17.1 H20 V18.9 H4 Z";
    private static final String BARS_5_EVEN = "M4 8.1 H20 V9.9 H4 Z M4 14.1 H20 V15.9 H4 Z";
    private static final String ARCHIVE_ICON = "M3 3 H15 A0.75 0.75 0 0 1 15.75 3.75 V6 A0.75 0.75 0 0 1 15 6.75 "
            + "H3 A0.75 0.75 0 0 1 2.25 6 V3.75 A0.75 0.75 0 0 1 3 3 Z "
            + "M3.75 6.75 v7.5 a0.75 0.75 0 0 0 0.75 0.75 h9 a0.75 0.75 0 0 0 0.75 -0.75 V6.75 "
            + "M7.5 9.75 h3";
    private static final String STARS_2 = "M7.5 8.2 L8.35 10.83 L11.11 10.83 L8.88 12.45 L9.73 15.07 L7.5 13.45 L5.27 15.07 "
            + "L6.12 12.45 L3.89 10.83 L6.65 10.83 Z "
            + "M16.5 8.2 L17.35 10.83 L20.11 10.83 L17.88 12.45 L18.73 15.07 L16.5 13.45 L14.27 15.07 "
            + "L15.12 12.45 L12.89 10.83 L15.65 10.83 Z";
            
    public AgentCard(DashboardAgentProfil profil,  Consumer<DashboardAgentProfil> onRegister) {
        super(16);
        getStyleClass().add("agent-card");
        
        getChildren().addAll(createHeader(profil),createRankBlock(profil));

        if (profil.isDifferentiated()) {
            getChildren().add(createChip("Parcours différencié", "badge-differentiated"));
        }
        
        getChildren().addAll(
        createSection("Permis", createLicenseChips(profil)),
        createSection("Compétences · " + profil.skills().size(), createSkillChips(profil)),
        createSection("Inscriptions", createRegistrationBlock(profil)),
        createActionBlock(profil, onRegister)
    );
        
    }

    // ---------- En-tête : identité + badge ----------
    private HBox createHeader(DashboardAgentProfil profil) {

        Label name = new Label(profil.lastName().toUpperCase() + " " + profil.firstName());
        name.getStyleClass().add("card-name");

        Label matricule = new Label("Matricule " + profil.matricule());
        matricule.getStyleClass().add("card-muted");

        VBox identity = new VBox(2, name, matricule);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = profil.isActive() ? new Label("Actif") : new Label("Inactif");
        status.getStyleClass().add("badge");
        status.getStyleClass().add(profil.isActive() ? "badge-active" : "badge-archived");
        return new HBox(12, identity, spacer, status);

        
    }

    // ---------- Bloc grade ----------
    private HBox createRankBlock(DashboardAgentProfil profil) {
        Label rank = new Label(DisplayNames.rank(profil.rank()).toUpperCase());
        rank.getStyleClass().add("rank-name");

        Label since = new Label("Depuis le " + profil.rankStartDate().format(DATE_FORMAT)
                + " · " + DisplayNames.seniority(profil.rankStartDate()));
        since.getStyleClass().add("card-muted");

        HBox block = new HBox(12, createRankIcon(profil.rank()), new VBox(1, rank, since));
        block.setAlignment(Pos.CENTER_LEFT);
        block.getStyleClass().add("rank-block");
        return block;
    }
    
    // ---------- Bloc profil inscription ----------
    private HBox createActionBlock(DashboardAgentProfil profil, Consumer<DashboardAgentProfil> onRegister){
        Button viewProfileBtn = createTextBtn("Voir le profil","view-profile-button");
        Button signInBtn = createTextBtn("Inscrire", "sign-in-button");
        Button archiveBtn = createIconBtn(ARCHIVE_ICON, "archive-icon", "Archiver l'agent");

        for (Button button : List.of(viewProfileBtn, signInBtn)) {
            button.setPrefWidth(0);
            button.setMaxWidth(Double.MAX_VALUE); // le bouton a le droit de s'élargir
            HBox.setHgrow(button, Priority.ALWAYS); // et il prend la place libre
        }

        HBox actions = new HBox(8, viewProfileBtn, signInBtn);
        if (profil.isActive()) {
            actions.getChildren().add(archiveBtn);
        }
        signInBtn.setOnAction(event -> onRegister.accept(profil));
        signInBtn.setDisable(!profil.isActive());
        return actions;
    }
    

    // ---------- Inscriptions ----------
    private VBox createRegistrationBlock(DashboardAgentProfil profil) {
        List<DashBoardRegistration> registrations = profil.registration();
        VBox block = new VBox(8);

        if (registrations.isEmpty()) {
            Label empty = new Label("Aucune inscription");
            empty.getStyleClass().add("registration-empty");
            empty.setMaxWidth(Double.MAX_VALUE);
            block.getChildren().add(empty);
            return block;
        }

        registrations.stream()
                .sorted(Comparator.comparingInt(r -> statusPriority(r.status())))
                .limit(MAX_REGISTRATIONS)
                .forEach(r -> block.getChildren().add(createRegistrationRow(r)));

        int hidden = registrations.size() - MAX_REGISTRATIONS;
        if (hidden > 0) {
            Button more = new Button("+" + hidden + (hidden > 1 ? " autres inscriptions" : " autre inscription"));
            more.getStyleClass().add("registration-more");
            more.setMaxWidth(Double.MAX_VALUE);
            block.getChildren().add(more);
        }
        return block;
    }

    // Une ligne = un nouvel ensemble de nœuds, à chaque appel
    private HBox createRegistrationRow(DashBoardRegistration registration) {
        Label name = new Label(registration.name());
        name.getStyleClass().add("registration-name");
        Label dates = new Label(registration.startDate().format(SHORT_DATE)
                + " → " + registration.endDate().format(DATE_FORMAT));
        dates.getStyleClass().add("card-muted");
        VBox text = new VBox(2, name, dates);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label status = new Label(DisplayNames.registrationStatus(registration.status()));

        status.getStyleClass().addAll("badge", "badge-" + registration.status().name().toLowerCase());
        status.setMinWidth(Region.USE_PREF_SIZE);

        HBox row = new HBox(12, text, spacer, status);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("registration-row");
        return row;
    }

    private static int statusPriority(RegistrationStatus status) {
        return switch (status) {
            case IN_PROGRESS -> 0;
            case PENDING -> 1;
            case DONE -> 2;
            case FAILED -> 3;
            case CANCEL -> 4;
        };
    }

    private Group createRankIcon(Rank rank) {
        List<SVGPath> galons = switch (rank) {
            case SAPEUR_DEUXIEME_CLASSE -> List.of();
            case SAPEUR_PREMIERE_CLASSE -> List.of(IconSvg.createIcon(SLANT_1, "insignia-red"));
            case CAPORAL -> List.of(IconSvg.createIcon(SLANT_2, "insignia-red"));
            case CAPORAL_CHEF -> List.of(IconSvg.createIcon(SLANT_3, "insignia-red"));
            case SERGENT -> List.of(IconSvg.createIcon(SERGENT_EDGE, "insignia-red"),
                    IconSvg.createIcon(SERGENT_STRIPE, "insignia-silver"));
            case SERGENT_CHEF -> List.of(IconSvg.createIcon(SLANT_3, "insignia-silver"),
                    IconSvg.createIcon(SLANT_3_GAPS, "insignia-red"));
            case ADJUDANT -> List.of(IconSvg.createIcon(BAR, "insignia-gold"),
                    IconSvg.createIcon(BAR_LINE, "insignia-red"));
            case ADJUDANT_CHEF -> List.of(IconSvg.createIcon(BAR, "insignia-silver"),
                    IconSvg.createIcon(BAR_LINE, "insignia-red"));
            case LIEUTENANT -> List.of(IconSvg.createIcon(BARS_2, "insignia-silver"));
            case CAPITAINE -> List.of(IconSvg.createIcon(BARS_3, "insignia-silver"));
            case COMMANDANT -> List.of(IconSvg.createIcon(BARS_4, "insignia-silver"));
            case LIEUTENANT_COLONEL -> List.of(IconSvg.createIcon(BARS_5_ODD, "insignia-silver"),
                    IconSvg.createIcon(BARS_5_EVEN, "insignia-gold"));
            case COLONEL -> List.of(IconSvg.createIcon(BARS_5_ODD, "insignia-silver"),
                    IconSvg.createIcon(BARS_5_EVEN, "insignia-silver"));
            case CONTROLEUR_GENERAL -> List.of(IconSvg.createIcon(STARS_2, "insignia-silver"));
        };

        Group icon = new Group(IconSvg.createIcon(PATCH, "insignia-patch")); // calque du fond
        icon.getChildren().addAll(galons); // galons par-dessus
        return icon;
    }

    private Button createTextBtn(String text, String css) {
        Button btn = new Button(text);
        btn.getStyleClass().add(css);
        return btn;
    }

    private Button createIconBtn(String icon, String css, String tooltipText) {
        Button button = new Button();
        button.setGraphic(IconSvg.createIcon(icon, css));
        button.getStyleClass().add("icon-button");
        button.setAccessibleText(tooltipText);
        button.setTooltip(new Tooltip(tooltipText));
        return button;
    }


    // ---------- Section avec un titre (Permis, Compétences...) ----------
    private VBox createSection(String title, Node content) {
        Label label = new Label(title.toUpperCase());
        label.getStyleClass().add("section-title");
        return new VBox(8, label, content);
    }

    // ---------- Pastilles ----------
    private FlowPane createLicenseChips(DashboardAgentProfil profil) {
        FlowPane chips = new FlowPane(6, 6);
        profil.licenses()
                .forEach(license -> chips.getChildren().add(createChip(DisplayNames.license(license), "chip-dark")));
        return chips;
    }

    private FlowPane createSkillChips(DashboardAgentProfil profil) {
        FlowPane chips = new FlowPane(6, 6);
        List<String> skills = profil.skills();

        skills.stream()
                .limit(MAX_SKILLS)
                .forEach(skill -> chips.getChildren().add(createChip(skill, "chip")));

        int hidden = skills.size() - MAX_SKILLS;
        if (hidden > 0) {
            chips.getChildren().add(createChip("+" + hidden, "chip-more"));
        }
        return chips;
    }

    private Label createChip(String text, String styleClass) {
        Label chip = new Label(text);
        chip.getStyleClass().add(styleClass);
        return chip;
    }
}







