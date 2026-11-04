package usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import data.AgentData;
import data.TrainingData;
import usecase.data.AgentDataUsecase;
import usecase.data.TrainingDataUsecase;
import usecase.mock.FakeAgentRepository;
import usecase.mock.FakeRegistrationRepository;
import usecase.mock.FakeTrainingRepository;

public class GetAvailableTrainingsTest {
    private FakeTrainingRepository fakeTrainingRepo;
    private FakeAgentRepository fakeAgentRepo;
    private FakeRegistrationRepository fakeRegistrationRepo;
    private GetAvailableTrainings getAvailableTrainings;

    @BeforeEach 
    void setUp(){
        fakeAgentRepo = new FakeAgentRepository();
        fakeTrainingRepo = new FakeTrainingRepository();
        fakeRegistrationRepo = new FakeRegistrationRepository();
        getAvailableTrainings = new GetAvailableTrainings(fakeAgentRepo, fakeTrainingRepo, fakeRegistrationRepo);
    }
    
    @Test 
    @DisplayName ("Doit retourner une list de formation pour un agent possédent les compétences requises")
    void agent_with_require_skills(){
        int matricule = AgentData.agentWithSkillForCE().getMatricule();
        AgentDataUsecase.insertAgentInRepo(AgentData.agentWithSkillForCE(), fakeAgentRepo);
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingCE(), fakeTrainingRepo);
        assertEquals(getAvailableTrainings.execute(matricule), TrainingData.trainingCE());

    }
   
    @Test 
    @DisplayName ("Doit retourner une list de formation sans la formation qui demande une compétences non possédée")
    void agent_without_require_skills(){
        int matricule = AgentData.agentWithoutSkillForCE().getMatricule();
        AgentDataUsecase.insertAgentInRepo(AgentData.agentWithoutSkillForCE(), fakeAgentRepo);
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingCE(),  fakeTrainingRepo);
        assertNotEquals(getAvailableTrainings.execute(matricule), TrainingData.trainingCE());
    }

    @Test 
    @DisplayName ("Doit retourner une list de formation sans la formation qui est déja dépassée")
    void training_with_expired_date(){
        int matricule = AgentData.agentWithSkillForCE().getMatricule();
        AgentDataUsecase.insertAgentInRepo(AgentData.agentWithSkillForCE(), fakeAgentRepo);
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingWithStartDateInPast(), fakeTrainingRepo);
        assertNotEquals(getAvailableTrainings.execute(matricule), TrainingData.trainingWithStartDateInPast());
    }
}
