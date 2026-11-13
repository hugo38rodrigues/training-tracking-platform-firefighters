package infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import domaine.entity.RegistrationAgentInTraining;
import domaine.entity.RegistrationStatus;
import domaine.repository.RegistrationAgentInTrainingRepository;

public class RegistrationAgentInTrainingRepositorySql implements RegistrationAgentInTrainingRepository {

    private final JdbcTemplate jdbc;

    public RegistrationAgentInTrainingRepositorySql(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(RegistrationAgentInTraining registration) {
        Integer id = jdbc.queryForObject("""
            INSERT INTO registration_agent_in_training
            (registration_start, registration_end, status, agent_matricule, training_id)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id """,
                Integer.class,
                registration.getStartRegistration(),
                registration.getEndRegistration(),
                registration.getStatut().name(),
                registration.getAgentMatricule(),
                registration.getIdTraining());
        registration.setId(id);
    }

    @Override
    public void update(RegistrationAgentInTraining registration) {
        int rows = jdbc.update("""
                UPDATE registration_agent_in_training
                SET registration_start = ?, registration_end = ?, status = ?
                WHERE id = ?
                """,
                registration.getStartRegistration(),
                registration.getEndRegistration(),
                registration.getStatut().name(),
                registration.getId());
        if (rows == 0) {
            throw new IllegalArgumentException("Aucune inscription avec l'id " + registration.getId());
        }
    }

    @Override
    public RegistrationAgentInTraining findById(int id) {
        return jdbc.query("SELECT * FROM registration_agent_in_training WHERE id = ?", this::mapRegistration, id)
                .stream().findFirst().orElse(null);
    }

    @Override
    public List<RegistrationAgentInTraining> findByAgent(int agentMatricule) {
        return jdbc.query(
                "SELECT * FROM registration_agent_in_training WHERE agent_matricule = ? ORDER BY registration_start",
                this::mapRegistration, agentMatricule);
    }

    @Override
    public List<RegistrationAgentInTraining> findByTraining(int trainingId) {
        return jdbc.query(
                "SELECT * FROM registration_agent_in_training WHERE training_id = ? ORDER BY registration_start",
                this::mapRegistration, trainingId);
    }

    private RegistrationAgentInTraining mapRegistration(ResultSet rs, int rowNum) throws SQLException {
        RegistrationAgentInTraining registration = new RegistrationAgentInTraining(
                rs.getObject("registration_start", LocalDate.class),
                rs.getObject("registration_end", LocalDate.class),
                RegistrationStatus.valueOf(rs.getString("status")),
                rs.getInt("agent_matricule"),
                rs.getInt("training_id"));
        registration.setId(rs.getInt("id"));
        return registration;
    }
}