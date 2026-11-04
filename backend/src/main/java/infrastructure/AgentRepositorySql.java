package infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import domaine.entity.Agent;
import domaine.entity.License;
import domaine.entity.Rank;
import domaine.entity.Skill;
import domaine.repository.AgentRepository;

public class AgentRepositorySql implements AgentRepository {

    private final JdbcTemplate jdbc;

    public AgentRepositorySql(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------- Écriture ----------

    @Override
    @Transactional
    public void add(Agent agent) {
        jdbc.update("""
                INSERT INTO agent (matricule, first_name, last_name, rank, rank_start_date,
                                   is_active, is_differentiated)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                agent.getMatricule(),
                agent.getFirstName(),
                agent.getLastName(),
                agent.getRank().name(),
                agent.getRankStartDate(),
                agent.isActive(),
                agent.isDifferentiated());

        addLicenses(agent.getMatricule(), agent.getLicenses());
        addSkills(agent.getMatricule(), agent.getSkills());
    }

    @Override
    @Transactional
    public void update(Agent agent) {
        jdbc.update("""
                UPDATE agent
                SET first_name = ?, last_name = ?, rank = ?, rank_start_date = ?,
                    is_active = ?, is_differentiated = ?
                WHERE matricule = ?
                """,
                agent.getFirstName(),
                agent.getLastName(),
                agent.getRank().name(),
                agent.getRankStartDate(),
                agent.isActive(),
                agent.isDifferentiated(),
                agent.getMatricule());

        jdbc.update("DELETE FROM agent_license WHERE agent_matricule = ?", agent.getMatricule());
        jdbc.update("DELETE FROM agent_skill WHERE agent_matricule = ?", agent.getMatricule());
        addLicenses(agent.getMatricule(), agent.getLicenses());
        addSkills(agent.getMatricule(), agent.getSkills());
    }

    private void addLicenses(int matricule, List<License> licenses) {
        jdbc.batchUpdate(
                "INSERT INTO agent_license (agent_matricule, license_name) VALUES (?, ?)",
                licenses.stream()
                        .map(license -> new Object[] { matricule, license.name() })
                        .toList());
    }

    private void addSkills(int matricule, List<Skill> skills) {
        jdbc.batchUpdate(
                "INSERT INTO agent_skill (agent_matricule, skill_id) SELECT ?, id FROM skill WHERE name = ?",
                skills.stream()
                        .map(skill -> new Object[] { matricule, skill.getName() })
                        .toList());
    }

    // ---------- Lecture ----------

    @Override
    public Agent findByMatricule(int matricule) {
        return jdbc.query("SELECT * FROM agent WHERE matricule = ?", this::mapAgent, matricule)
                .stream().findFirst().orElse(null);
    }

    @Override
    public List<Agent> findAllAgent() {
        return jdbc.query("SELECT * FROM agent ORDER BY last_name, first_name", this::mapAgent);
    }

    @Override
    public boolean existsByMatricule(int matricule) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM agent WHERE matricule = ?", Integer.class, matricule);
        return count > 0;
    }

    private Agent mapAgent(ResultSet rs, int rowNum) throws SQLException {
        int matricule = rs.getInt("matricule");
        Agent agent = new Agent(
                rs.getString("first_name"),
                rs.getString("last_name"),
                matricule,
                Rank.valueOf(rs.getString("rank")),
                rs.getObject("rank_start_date", LocalDate.class),
                findLicensesForAgent(matricule),
                findSkillsForAgent(matricule));
                agent.setActive(rs.getBoolean("is_active"));
                agent.setDifferentiated(rs.getBoolean("is_differentiated"));
        return agent;
    }

    private List<License> findLicensesForAgent(int matricule) {
        return jdbc.query(
                "SELECT license_name FROM agent_license WHERE agent_matricule = ?",
                (rs, rowNum) -> License.valueOf(rs.getString("license_name")),
                matricule);
    }

    private List<Skill> findSkillsForAgent(int matricule) {
        return jdbc.query("""
                SELECT skill.id, skill.name
                FROM agent_skill
                JOIN skill ON agent_skill.skill_id = skill.id
                WHERE agent_skill.agent_matricule = ?
                ORDER BY skill.name
                """,
                (rs, rowNum) -> {
                    Skill skill = new Skill(rs.getString("name"));
                    skill.setId(rs.getInt("id"));
                    return skill;
                },
                matricule);
    }
}