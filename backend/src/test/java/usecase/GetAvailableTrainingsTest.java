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
    @DisplayName ("Doit retourner une list de formation pour un agent possédent les compétences requise")
    void agent_with_require_skills(){
        AgentDataUsecase.insertAgentInRepo(AgentData.agentWithSkillForCE(), 1, fakeAgentRepo);
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingCE(), 1, fakeTrainingRepo);
        assertEquals(getAvailableTrainings.execute(1), TrainingData.trainingCE());

    }
   
    @Test 
    @DisplayName ("Doit retourner une list de formation sans la formation qui demande une compétences non possédée")
    void agent_without_require_skills(){
        AgentDataUsecase.insertAgentInRepo(AgentData.agentWithoutSkillForCE(), 1, fakeAgentRepo);
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingCE(), 1, fakeTrainingRepo);
        assertNotEquals(getAvailableTrainings.execute(1), TrainingData.trainingCE());
    }

    @Test 
    @DisplayName ("Doit retourner une list de formation sans la formation qui est déja dépassée")
    void training_with_expired_date(){
        AgentDataUsecase.insertAgentInRepo(AgentData.agentWithSkillForCE(), 1, fakeAgentRepo);
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingWithStartDateInPast(), 1, fakeTrainingRepo);
        assertNotEquals(getAvailableTrainings.execute(1), TrainingData.trainingWithStartDateInPast());
    }
}
