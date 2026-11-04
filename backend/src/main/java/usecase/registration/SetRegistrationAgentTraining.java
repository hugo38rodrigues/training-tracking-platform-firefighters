package usecase.registration;

import java.time.LocalDate;
import domaine.entity.Agent;
import domaine.entity.RegistrationAgentInTraining;
import domaine.entity.RegistrationStatus;
import domaine.entity.Training;
import domaine.repository.AgentRepository;
import domaine.repository.RegistrationAgentInTrainingRepository;
import domaine.repository.TrainingRepository;
import usecase.exceptions.RegistrationRefuseException;

public class SetRegistrationAgentTraining {
    private AgentRepository agentRepo;
    private TrainingRepository trainingRepo;
    private RegistrationAgentInTrainingRepository registrationRepo;

    
    public SetRegistrationAgentTraining(AgentRepository agentRepo, TrainingRepository trainingRepo, RegistrationAgentInTrainingRepository registrationRepo) {
        this.agentRepo = agentRepo;
        this.trainingRepo = trainingRepo;
        this.registrationRepo = registrationRepo;

    }


    public void execute(int agentId, int trainingId){
        Agent agent = agentRepo.findAgent(agentId);
        Training training = trainingRepo.findTrainingById(trainingId);
        LocalDate today = LocalDate.now();

        if(!agent.canRegistrationTraining(training)){
            throw new RegistrationRefuseException("Cette agent n'a pas les compétences requise");
        }

        if(training.getStartDate().isBefore(today)){
            throw new RegistrationRefuseException("Cette formation à déja commencé");
        }

        RegistrationAgentInTraining registration = new RegistrationAgentInTraining(today,training.getEndDate(), RegistrationStatus.PENDING,agentId, trainingId);
       
        if (!registrationRepo.findByAgent(agentId).isEmpty()){
            throw new RegistrationRefuseException("Cette agent est déjà inscrit à cette formation agent id: " + agentId +"formation id: " + trainingId);
        }
        
        registrationRepo.save(registration);
        
    }
}
