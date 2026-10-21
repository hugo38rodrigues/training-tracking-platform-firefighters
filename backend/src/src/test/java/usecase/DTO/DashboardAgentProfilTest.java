package usecase.DTO;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import data.AgentData;
import data.RegistrationTrainingData;
import domaine.entity.Agent;
import domaine.entity.RegistrationAgentInTraining;
import domaine.entity.RegistrationStatus;

class DashboardAgentProfilTest {

    @Test
    @DisplayName("Les compétences de l'agent sont converties en liste de noms")
    void les_skills_sont_convertis_en_noms() {
        Agent agent = AgentData.newAgent();
        DashboardAgentProfil result = DashboardAgentProfil.from(agent, List.of());

        assertEquals(
                List.of("EQ DIV HYM", "EQ EA", "EQ INC", "EQ PMA", "EQ PPBE", "EQ SSUAP", "CE INC"),
                result.skills());
    }

    @Test
    @DisplayName("Les informations de l'agent sont recopiées dans le profil")
    void les_infos_de_l_agent_sont_recopiees() {

        Agent agent = AgentData.newAgent();

        DashboardAgentProfil result = DashboardAgentProfil.from(agent, List.of());

        assertEquals(agent.getLastName(), result.lastName());
        assertEquals(agent.getFirstName(), result.firstName());
        assertEquals(agent.getMatricule(), result.matricule());
        assertEquals(agent.getRank(), result.rank());
        assertEquals(agent.getLicenses(), result.licenses());
    }

    @Test
    @DisplayName("Les inscriptions passées en paramètre sont dans le profil")
    void les_inscriptions_sont_ajoutees() {

        RegistrationAgentInTraining registration = RegistrationTrainingData.registration(RegistrationStatus.PENDING);
        List<RegistrationAgentInTraining> registrations = List.of(registration);

        DashboardAgentProfil result = DashboardAgentProfil.from(AgentData.newAgent(), registrations);

        assertEquals(registrations, result.registration());
    }
}