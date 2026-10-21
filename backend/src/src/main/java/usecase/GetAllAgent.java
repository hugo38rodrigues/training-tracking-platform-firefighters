package usecase;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import domaine.entity.Agent;
import domaine.entity.RegistrationAgentInTraining;
import domaine.entity.Training;
import domaine.repository.AgentRepository;
import domaine.repository.RegistrationAgentInTrainingRepository;
import domaine.repository.TrainingRepository;
import usecase.DTO.DashboardAgentProfil;
import usecase.exceptions.InvalideAgentsException;

public class GetAllAgent {
    
    private AgentRepository agentRepo;
    private RegistrationAgentInTrainingRepository registrationRepo;
    private TrainingRepository trainingRepo;
  
    public GetAllAgent(AgentRepository agentRepo, RegistrationAgentInTrainingRepository registrationRepo,
            TrainingRepository trainingRepo) {
        this.agentRepo = agentRepo;
        this.registrationRepo = registrationRepo;
        this.trainingRepo = trainingRepo;
    }


    public List<DashboardAgentProfil> execute() {
        LocalDate today = LocalDate.now();
        List<Agent> agents = agentRepo.findAllAgent();
        List<DashboardAgentProfil> agentProfils = new ArrayList<>(); 

        if (agents.isEmpty()){
            throw new InvalideAgentsException("Aucun agent enregistré");
        }
        
        for (Agent agent : agents){
            List<RegistrationAgentInTraining> registrations = registrationRepo.findByAgent(agent.getId());
            for (RegistrationAgentInTraining registration : registrations){
                Training training = trainingRepo.findTrainingById(registration.getIdTraining());
                registration.setStatut(registration.computeStatus(today, training));
            }
            agentProfils.add(DashboardAgentProfil.from(agent, registrations));
        }
        
        return agentProfils;
    }
}
