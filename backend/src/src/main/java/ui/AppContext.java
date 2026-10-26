package ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
import usecase.registration.SetRegistrationAgentTraining;
import usecase.registration.CancelTraining;
import usecase.registration.DoneTraining;


public class AppContext implements AutoCloseable {

    private final Connection connection;

    public final GetAllAgent getAllAgent;
    public final AddAgent addAgent;
    public final SetRegistrationAgentTraining registerAgent;
    public final CancelTraining cancelTraining;
    public final DoneTraining doneTraining;

    public AppContext(String databasePath) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);

        DatabaseInitialisation.initialisation(connection);
        System.out.println("Base de données prête : " + databasePath);

        // 1. Les repositories SQL, qui partagent la même connexion
        AgentRepository agentRepo = new AgentRepositorySql(connection);
        SkillRepository skillRepo = new SkillRepositorySql(connection);
        TrainingRepository trainingRepo = new TrainingRepositorySql(connection);
        RegistrationAgentInTrainingRepository registrationRepo = new RegistrationAgentInTrainingRepositorySql(
                connection);
        RankConditionRepository rankConditionRepo = new RankConditionSql(connection);

        // 2. Les use cases, qui reçoivent les repositories
        getAllAgent = new GetAllAgent(agentRepo, registrationRepo, trainingRepo);
        addAgent = new AddAgent(agentRepo, skillRepo);
        registerAgent = new SetRegistrationAgentTraining(agentRepo, trainingRepo, registrationRepo);
        cancelTraining = new CancelTraining(registrationRepo);
        doneTraining = new DoneTraining(registrationRepo, agentRepo, rankConditionRepo, new RankCalculator());
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}