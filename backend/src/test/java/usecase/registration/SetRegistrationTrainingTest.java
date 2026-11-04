package usecase.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import data.AgentData;
import data.RegistrationTrainingData;
import data.TrainingData;
import domaine.entity.RegistrationStatus;
import usecase.data.AgentDataUsecase;
import usecase.data.RegistrationTrainingUsecase;
import usecase.data.TrainingDataUsecase;
import usecase.exceptions.RegistrationRefuseException;
import usecase.mock.FakeAgentRepository;
import usecase.mock.FakeRegistrationRepository;
import usecase.mock.FakeTrainingRepository;

public class SetRegistrationTrainingTest {
    private FakeAgentRepository fakeAgentRepo;
    private FakeRegistrationRepository fakeRegistrationRepo;
    private FakeTrainingRepository fakeTrainingRepo;
    private SetRegistrationAgentTraining setRegistrationAgentTraining;

    @BeforeEach 
    void setUp(){
        fakeTrainingRepo = new FakeTrainingRepository();
        fakeAgentRepo = new FakeAgentRepository();
        fakeRegistrationRepo = new FakeRegistrationRepository();
        setRegistrationAgentTraining = new SetRegistrationAgentTraining(fakeAgentRepo, fakeTrainingRepo, fakeRegistrationRepo);
    }
   

    @Test
    @DisplayName  ("Un agent ne peux pas s'inscrire à une formation sans les compétences requises") 
     void agent_sans_competences_requises_est_refuse() {
        int matricule = AgentData.agentWithSkillForCE().getMatricule();
        int formationNumber = TrainingData.trainingCE().getTrainingNumber();
        AgentDataUsecase.insertAgentInRepo(AgentData.agentWithoutSkillForCE(), fakeAgentRepo);
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingCE(), fakeTrainingRepo);

        RegistrationRefuseException e = assertThrows(RegistrationRefuseException.class,
                () -> setRegistrationAgentTraining.execute(matricule,formationNumber));
        assertEquals("Cette agent n'a pas les compétences requise", e.getMessage());
    }

    @Test
    @DisplayName  ("Un agent ne peux pas s'inscrire à une formation qui a commencé") 
     void date_training_passe() {
        int matricule = AgentData.agentWithSkillForCE().getMatricule();
        int formationNumber = TrainingData.trainingCE().getTrainingNumber();
        AgentDataUsecase.insertAgentInRepo(AgentData.agentWithSkillForCE(),  fakeAgentRepo);
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingWithStartDateInPast(), fakeTrainingRepo);

        RegistrationRefuseException e = assertThrows(RegistrationRefuseException.class,
                () -> setRegistrationAgentTraining.execute(matricule,formationNumber));
        assertEquals("Cette formation à déja commencé", e.getMessage());
    }

    @Test
    @DisplayName  ("Un agent avec déjà la compétence ne peut pas s'incrire sur la formation") 
     void agent_avec_deja_la_competence_ne_peut_pas_inscrire() {
        int matricule = AgentData.agentCE().getMatricule();
        int formationNumber = TrainingData.trainingCE().getTrainingNumber();
        AgentDataUsecase.insertAgentInRepo(AgentData.agentCE(), fakeAgentRepo);
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingCE(), fakeTrainingRepo);
    
        RegistrationRefuseException e = assertThrows(RegistrationRefuseException.class,
                () -> setRegistrationAgentTraining.execute(matricule,formationNumber));
        assertEquals("Cette agent n'a pas les compétences requise", e.getMessage());
    }
    
    @Test
    @DisplayName  ("Un agent déjà inscrit ne peut pas se réinscrire à une formation") 
     void agent_deja_inscrit() {
        int matricule = AgentData.agentWithSkillForCE().getMatricule();
        int formationNumber = TrainingData.trainingCE().getTrainingNumber();
        AgentDataUsecase.insertAgentInRepo(AgentData.agentWithSkillForCE(), fakeAgentRepo);
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingCE(), fakeTrainingRepo);
        RegistrationTrainingUsecase.insertRegistrationInRepo(RegistrationTrainingData.registration(RegistrationStatus.PENDING), 1, fakeRegistrationRepo);
        
        RegistrationRefuseException e = assertThrows(RegistrationRefuseException.class,
                () -> setRegistrationAgentTraining.execute(matricule,formationNumber));
        assertEquals("Cette agent est déjà inscrit à cette formation agent id: " + matricule +  "formation id: " + formationNumber, e.getMessage());
    }
   

    @Test
    @DisplayName ("Un agent peux s'inscrire si il a toutes les compétences") 
     void agent_inscrit() {
        int matricule = AgentData.agentWithSkillForCE().getMatricule();
        int formationNumber = TrainingData.trainingCE().getTrainingNumber();
        AgentDataUsecase.insertAgentInRepo(AgentData.agentWithSkillForCE(), fakeAgentRepo);
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingCE(), fakeTrainingRepo);
        setRegistrationAgentTraining.execute(matricule,formationNumber);
        RegistrationTrainingUsecase.insertRegistrationInRepo(RegistrationTrainingData.registration(RegistrationStatus.PENDING), 1, fakeRegistrationRepo);
        assertNotNull(fakeRegistrationRepo.findById(1));
    }
}
