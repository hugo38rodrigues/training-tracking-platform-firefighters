package usecase;

import java.time.LocalDate;
import java.util.List;
import domaine.entity.Agent;
import domaine.repository.AgentRepository;
import domaine.repository.RegistrationAgentInTrainingRepository;
import domaine.repository.TrainingRepository;
import usecase.DTO.TrainingOption;
import usecase.exceptions.InvalideAgentsException;

public class GetAvailableTrainings {
    private AgentRepository agentRepo;
    private TrainingRepository trainingRepo;
    private RegistrationAgentInTrainingRepository registrationAgentInTrainingRepo;

    

    public GetAvailableTrainings(AgentRepository agentRepo, TrainingRepository trainingRepo,
            RegistrationAgentInTrainingRepository registrationAgentInTrainingRepo) {
        this.agentRepo = agentRepo;
        this.trainingRepo = trainingRepo;
        this.registrationAgentInTrainingRepo = registrationAgentInTrainingRepo;
    }



    public List<TrainingOption> execute(int agentId) {

        Agent agent = agentRepo.findAgent(agentId);

        if (agent == null) {
            throw new InvalideAgentsException("Agent introuvable");
        }

        LocalDate today = LocalDate.now();
        List<TrainingOption> trainings = trainingRepo.findAll().stream()
                .filter(training -> !training.getStartDate().isBefore(today))
                .filter(agent::canRegistrationTraining)
                .sorted((a, b) -> a.getStartDate().compareTo(b.getStartDate()))
                .map(TrainingOption::from)
                .toList();

        return trainings.stream().filter(training -> registrationAgentInTrainingRepo.findByAgent(agentId).isEmpty()).toList();

    }
}
