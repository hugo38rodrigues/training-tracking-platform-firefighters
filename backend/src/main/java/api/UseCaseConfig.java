package api;

import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import domaine.entity.RankCalculator;
import domaine.repository.AgentRepository;
import domaine.repository.RankConditionRepository;
import domaine.repository.RegistrationAgentInTrainingRepository;
import domaine.repository.SkillRepository;
import domaine.repository.TrainingRepository;
import infrastructure.AgentRepositorySql;
import infrastructure.DatabaseInitialisation;
import infrastructure.RankConditionSql;
import infrastructure.RegistrationAgentInTrainingRepositorySql;
import infrastructure.SkillRepositorySql;
import infrastructure.TrainingRepositorySql;
import usecase.AddAgent;
import usecase.GetAllAgent;
import usecase.GetSkills;
import usecase.registration.CancelTraining;
import usecase.registration.DoneTraining;
import usecase.registration.SetRegistrationAgentTraining;

@Configuration
public class UseCaseConfig {


    // --- Les repositories ---
    @Bean
    public AgentRepository agentRepository(JdbcTemplate jdbc) {
        return new AgentRepositorySql(jdbc);
    }

    @Bean
    public SkillRepository skillRepository(JdbcTemplate jdbc) {
        return new SkillRepositorySql(jdbc);
    }

    @Bean
    public TrainingRepository trainingRepository(JdbcTemplate jdbc) {
        return new TrainingRepositorySql(jdbc);
    }

    @Bean
    public RegistrationAgentInTrainingRepository registrationRepository(JdbcTemplate jdbc) {
        return new RegistrationAgentInTrainingRepositorySql(jdbc);
    }

    @Bean
    public RankConditionRepository rankConditionRepository(JdbcTemplate jdbc) {
        return new RankConditionSql(jdbc);
    }

    @Bean
    public ApplicationRunner initDatabase(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                DatabaseInitialisation.initialisation(connection);
            }
        };
    }

    // --- Les use cases ---
    @Bean
    public GetAllAgent getAllAgent(AgentRepository agents, RegistrationAgentInTrainingRepository registrations,
            TrainingRepository trainings) {
        return new GetAllAgent(agents, registrations, trainings);
    }

    @Bean
    public AddAgent addAgent(AgentRepository agents, SkillRepository skills) {
        return new AddAgent(agents, skills);
    }

    @Bean
    public GetSkills getSkills(SkillRepository skills) {
        return new GetSkills(skills);
    }

    @Bean
    public usecase.GetAvailableTrainings getAvailableTrainings(AgentRepository agents, TrainingRepository trainings, RegistrationAgentInTrainingRepositorySql registrationAgentInTrainingRepo) {
        return new usecase.GetAvailableTrainings(agents, trainings, registrationAgentInTrainingRepo);
    }

    @Bean
    public SetRegistrationAgentTraining registerAgent(AgentRepository agents, TrainingRepository trainings,
            RegistrationAgentInTrainingRepository registrations) {
        return new SetRegistrationAgentTraining(agents, trainings, registrations);
    }

    @Bean
    public CancelTraining cancelTraining(RegistrationAgentInTrainingRepository registrations) {
        return new CancelTraining(registrations);
    }

    @Bean
    public DoneTraining doneTraining(RegistrationAgentInTrainingRepository registrations, AgentRepository agents,
            RankConditionRepository rankConditions) {
        return new DoneTraining(registrations, agents, rankConditions, new RankCalculator());
    }
}