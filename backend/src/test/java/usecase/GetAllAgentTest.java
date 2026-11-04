package usecase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import data.AgentData;
import usecase.DTO.DashboardAgentProfil;
import usecase.data.AgentDataUsecase;
import usecase.exceptions.InvalideAgentsException;
import usecase.mock.FakeAgentRepository;
import usecase.mock.FakeRegistrationRepository;
import usecase.mock.FakeTrainingRepository;

public class GetAllAgentTest {
    private FakeAgentRepository fakeAgentRepository;
    private FakeRegistrationRepository fakeRegistrationRepository;
    private FakeTrainingRepository fakeTrainingRepository;
    private GetAllAgent getAllAgent;
    @BeforeEach 
    void setup(){
        fakeAgentRepository = new FakeAgentRepository();
        fakeRegistrationRepository = new FakeRegistrationRepository();
        fakeTrainingRepository = new FakeTrainingRepository();
        getAllAgent = new GetAllAgent(fakeAgentRepository, fakeRegistrationRepository, fakeTrainingRepository);

    }

    @Test 
    @DisplayName ("Lève une erreur de type InvalideAgentsException si aucun agent n'est enrgistré")
    void aucun_agent_enregistre(){
        InvalideAgentsException e = assertThrows(InvalideAgentsException.class, () -> getAllAgent.execute()) ;
        assertEquals("Aucun agent enregistré", e.getMessage());
    }
    
    @Test 
    @DisplayName ("Retour la liste des agents présent en base")
    void liste_agents_en_base(){

        AgentDataUsecase.insertAgentInRepo(AgentData.newAgent(), 1, fakeAgentRepository);
        AgentDataUsecase.insertAgentInRepo(AgentData.newAgent(), 2, fakeAgentRepository);
        List<DashboardAgentProfil> results = getAllAgent.execute(); 
        assertEquals(2, results.size());
    }
}
