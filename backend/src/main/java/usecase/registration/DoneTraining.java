package usecase.registration;
import java.time.LocalDate;
import java.util.List;

import domaine.entity.Agent;
import domaine.entity.Rank;
import domaine.entity.RankCalculator;
import domaine.entity.RankCondition;
import domaine.entity.RegistrationAgentInTraining;
import domaine.entity.RegistrationStatus;
import domaine.repository.AgentRepository;
import domaine.repository.RankConditionRepository;
import domaine.repository.RegistrationAgentInTrainingRepository;
import usecase.exceptions.InvalidRegistrationException;
import usecase.exceptions.InvalidStatusException;


public class DoneTraining {
    private RegistrationAgentInTrainingRepository registrationRepo;
    private AgentRepository agentRepo;
    private RankConditionRepository rankConditionRepo;
    private RankCalculator rankCalculator;
    
    

    public DoneTraining(RegistrationAgentInTrainingRepository registrationRepo, AgentRepository agentRepo,
            RankConditionRepository rankConditionRepo, RankCalculator rankCalculator) {
        this.registrationRepo = registrationRepo;
        this.agentRepo = agentRepo;
        this.rankConditionRepo = rankConditionRepo;
        this.rankCalculator = rankCalculator;
    }



    public void execute(int agentId, int registrationId){
        LocalDate today = LocalDate.now();
        RegistrationAgentInTraining registration = registrationRepo.findById(registrationId);

        if(registration == null){
            throw new InvalidRegistrationException("La formation n'existe pas");
        }

        boolean registrationIsNotFinish = registration.getStatut().equals(RegistrationStatus.CANCEL) || registration.getStatut().equals(RegistrationStatus.FAILED);
        boolean registrationIsPendingOrDone = registration.getStatut().equals(RegistrationStatus.PENDING) || registration.getStatut().equals(RegistrationStatus.DONE) ;
        
        if (registrationIsNotFinish){
            throw new InvalidStatusException("Une formation ne peut pas être fini si elle est annulé ou échoué");
        }

        if (registrationIsPendingOrDone){
            throw new InvalidStatusException("Une formation ne peut pas être fini si elle n'a pas commencé ou déjà fini");            
        }

        registration.setStatut(RegistrationStatus.DONE);
        registrationRepo.save(registration);

        List<RankCondition> rankCondtions = rankConditionRepo.getRankCondition();
        Agent agent = agentRepo.findAgent(agentId);
        Rank newRank = rankCalculator.compileNewRank(agent, rankCondtions, today );
        if (newRank != agent.getRank()){
            agent.setRank(newRank);
            agent.setRankStartDate(today);
            agentRepo.update(agent);
        }

    }
    
}
