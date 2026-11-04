package usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import data.AgentData;
import usecase.exceptions.InvalidMatriculesException;
import usecase.mock.FakeAgentRepository;
import usecase.mock.FakeSkillRepository;

public class AddAgentTest {
    private FakeAgentRepository fakeAgentRepo;
    private FakeSkillRepository fakeSkillRepo;
    private AddAgent addAgent;   

    @BeforeEach 
    void setUp(){
        fakeAgentRepo = new FakeAgentRepository();
        fakeSkillRepo = new FakeSkillRepository();
        addAgent = new AddAgent(fakeAgentRepo, fakeSkillRepo);
        fakeAgentRepo.add(AgentData.exitantAgent());
    }
  
    @Test 
    @DisplayName("Un agent avec un matricule déjà pris est refusé")
    void ajout_agent_avec_matricule_existant_leve_une_erreur() {
        
        assertThrows(InvalidMatriculesException.class, () -> addAgent.execute(AgentData.agentWithMatriculeExistant()));
    }

    @Test
    @DisplayName("Un agent avec un matricule trop long est refusé")
    void un_agent_avec_un_matricule_trop_long(){
        assertThrows(InvalidMatriculesException.class, () -> addAgent.execute(AgentData.agentWithMatriculeToBig()));
    }

    @Test
     @DisplayName("Un agent avec un nouveau matricule est ajouté")
    void ajout_agent_valide(){
        addAgent.execute(AgentData.newAgent());
        assertTrue(fakeAgentRepo.existsByMatricule(AgentData.newAgent().getMatricule()));
    }

}
