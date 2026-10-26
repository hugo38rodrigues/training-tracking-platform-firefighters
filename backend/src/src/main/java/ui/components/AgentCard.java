package ui.components;

import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import ui.DisplayNames;
import usecase.DTO.DashboardAgentProfil;

public class AgentCard extends VBox {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int MAX_SKILLS = 5;

    public AgentCard(DashboardAgentProfil profil) {
        super(16);
        getStyleClass().add("agent-card");
        
        getChildren().addAll(createHeader(profil),createRankBlock(profil));

        if (profil.isDifferentiated()) {
            getChildren().add(createChip("Parcours différencié", "badge-differentiated"));
        }
        
        getChildren().addAll(
                createSection("Permis", createLicenseChips(profil)),
                createSection("Compétences · " + profil.skills().size(), createSkillChips(profil)));
    }

    // ---------- En-tête : identité + badge ----------
    private HBox createHeader(DashboardAgentProfil profil) {
        getStyleClass().add("agent-card");

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

        HBox block = new HBox(12, createRankIcon(), new VBox(1, rank, since));
        block.setAlignment(Pos.CENTER_LEFT);
        block.getStyleClass().add("rank-block");
        return block;
    }

    private SVGPath createRankIcon() {
        SVGPath icon = new SVGPath();
        icon.setContent("M5 10 L12 6 L19 10 M5 15 L12 11 L19 15"); // deux chevrons
        icon.getStyleClass().add("rank-icon");
        return icon;
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







