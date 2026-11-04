package domaine.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import data.AgentData;
import data.TrainingData;

public class AgentTest {

    @Test 
    void archiveRenderAgentInactif(){
        Agent agent = AgentData.newAgent();
        agent.archive();
        assertFalse(agent.isActive());
    }

   
    @Test
    @DisplayName ("Un agent avec toute les compétences peut s'incrire")
    void possibleToValidRegistration(){
        
        Agent agent = AgentData.agentWithSkillForCE();

        Training training = TrainingData.trainingCE();

        assertTrue(agent.canRegistrationTraining(training));
    }
    
    @Test 
    @DisplayName ("Un agent à qui il manque des compétences ne peut pas s'incrire")
    void impossibleToValidRegistration(){
        Agent agent = AgentData.agentWithoutSkillForCE();
        assertFalse(agent.canRegistrationTraining(TrainingData.trainingCE()));
    }
}
