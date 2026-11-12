package api.controllers.DTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import domaine.entity.Agent;
import domaine.entity.License;
import domaine.entity.Rank;
import domaine.entity.Skill;

// La forme exacte du JSON envoyé par le formulaire React
public record CreateAgentRequest(
        int matricule,
        String firstName,
        String lastName,
        Rank rank, // "CAPORAL" → Rank.CAPORAL (Jackson sait faire pour les enums)
        LocalDate rankStartDate, // "2026-11-09" → LocalDate
        List<License> licenses, // ["PERMIS_B"] → License.PERMIS_B
        List<String> skills, // ["COND VL"] → de simples textes
        boolean isDifferentiated) {

    // Constructeur « compact » : il s'exécute avant de remplir le record.
    // Si un champ manque dans le JSON, on met une liste vide au lieu de null.
    public CreateAgentRequest {
        licenses = licenses == null ? List.of() : licenses;
        skills = skills == null ? List.of() : skills;
    }

    // Conversion vers l'entité du domaine
    public Agent toAgent() {
        List<Skill> agentSkills = new ArrayList<>(skills.stream().map(Skill::new).toList());

        Agent agent = new Agent(firstName, lastName, matricule, rank, rankStartDate,
                new ArrayList<>(licenses), agentSkills);
        agent.setDifferentiated(isDifferentiated);
        return agent;
    }
}