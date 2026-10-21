package usecase.registration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import data.AgentData;
import data.RegistrationTrainingData;
import domaine.entity.Rank;
import domaine.entity.RankCalculator;
import domaine.entity.RegistrationStatus;
import usecase.data.AgentDataUsecase;
import usecase.data.RegistrationTrainingUsecase;
import usecase.exceptions.InvalidRegistrationException;
import usecase.exceptions.InvalidStatusException;
import usecase.mock.FakeAgentRepository;
import usecase.mock.FakeRankConditionRepository;
import usecase.mock.FakeRegistrationRepository;


public class DoneTrainingTest {
    private FakeAgentRepository fakeAgentRepository;
    private FakeRegistrationRepository fakeRegistrationRepository;
    private FakeRankConditionRepository fakeRankConditionRepository;
    private RankCalculator rankCalculator;
    private DoneTraining doneTraining;

    @BeforeEach 
    void setUp(){
        fakeAgentRepository = new FakeAgentRepository();
        fakeRegistrationRepository = new FakeRegistrationRepository();
        fakeRankConditionRepository = new FakeRankConditionRepository();
        rankCalculator = new RankCalculator(); 
        doneTraining = new DoneTraining( fakeRegistrationRepository, fakeAgentRepository, fakeRankConditionRepository, rankCalculator);
    }
    
    @Test
    @DisplayName ("Doit lever une exeption si l'inscription n'existe pas")
    void registration_not_exist(){
        
        InvalidRegistrationException e = assertThrows(InvalidRegistrationException.class,() -> doneTraining.execute(1,1));
        assertEquals("La formation n'existe pas", e.getMessage());
    }

    @ParameterizedTest(name = "Une inscription {0} ne peut pas être validée")
    @EnumSource (value = RegistrationStatus.class, names = {"FAILED", "CANCEL"})
    void traing_status_failed_or_cancel(RegistrationStatus status){
        int registrationId = RegistrationTrainingUsecase.insertRegistrationInRepo(RegistrationTrainingData.registration(status), 1, fakeRegistrationRepository);
        int agentid = AgentDataUsecase.insertAgentInRepo(AgentData.agentCE(), 1, fakeAgentRepository);

        InvalidStatusException e = assertThrows(InvalidStatusException.class,() -> doneTraining.execute(agentid, registrationId));
   
        assertEquals("Une formation ne peut pas être fini si elle est annulé ou échoué", e.getMessage());

    }

    @ParameterizedTest(name = "Une inscription {0} ne peut pas être validée")
    @EnumSource (value = RegistrationStatus.class, names = {"PENDING", "DONE"})
    void traing_status_pending_or_done(RegistrationStatus status){
        int registrationId = RegistrationTrainingUsecase.insertRegistrationInRepo(RegistrationTrainingData.registration(status), 1, fakeRegistrationRepository);
        int agentid = AgentDataUsecase.insertAgentInRepo(AgentData.agentCE(), 1, fakeAgentRepository);
   
        InvalidStatusException e = assertThrows(InvalidStatusException.class,() -> doneTraining.execute(agentid,registrationId));
        assertEquals("Une formation ne peut pas être fini si elle n'a pas commencé ou déjà fini", e.getMessage());

    }
    
    @Test 
    @DisplayName ("Doit passser à true si la formation passe à 'DONE' ")
    void training_status_a_done(){
        int registrationId = RegistrationTrainingUsecase.insertRegistrationInRepo(RegistrationTrainingData.registration(RegistrationStatus.IN_PROGRESS), 1, fakeRegistrationRepository);
        int agentId = AgentDataUsecase.insertAgentInRepo(AgentData.agentCE(), 1, fakeAgentRepository);

        doneTraining.execute(agentId , registrationId);
        assertTrue(fakeRegistrationRepository.findById(registrationId).getStatut().equals(RegistrationStatus.DONE));
    }

    @Test 
    @DisplayName ("Doit valider la monter en grade si les compétences sont complètes")
    void rank_agent_lvl_up(){
        int registrationId = RegistrationTrainingUsecase.insertRegistrationInRepo(RegistrationTrainingData.registration(RegistrationStatus.IN_PROGRESS), 1, fakeRegistrationRepository);
        int agentId = AgentDataUsecase.insertAgentInRepo(AgentData.agentWithSkillForCE(), 1, fakeAgentRepository);
        doneTraining.execute(agentId, registrationId);
        assertEquals(fakeAgentRepository.findAgent(agentId).getRank(), Rank.CAPORAL);
        assertEquals(fakeAgentRepository.findAgent(agentId).getRankStartDate(), LocalDate.now());


    }
    
    @Test 
    @DisplayName ("Ne doit pas valider la monter en grade si les compétences sont incomplètes")
    void rank_agent_not_lvl_up(){
        int registrationId = RegistrationTrainingUsecase.insertRegistrationInRepo(RegistrationTrainingData.registration(RegistrationStatus.IN_PROGRESS), 1, fakeRegistrationRepository);
        int agentId = AgentDataUsecase.insertAgentInRepo(AgentData.agentWithoutSkillForCE(), 1, fakeAgentRepository);
        doneTraining.execute(agentId, registrationId);
        assertEquals(fakeAgentRepository.findAgent(agentId).getRank(), Rank.SAPEUR_PREMIERE_CLASSE);
        assertEquals(fakeAgentRepository.findAgent(agentId).getRankStartDate(), LocalDate.parse("2022-03-26"));
    }

}
