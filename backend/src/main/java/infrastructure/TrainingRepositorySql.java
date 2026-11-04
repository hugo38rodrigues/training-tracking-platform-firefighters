package infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import domaine.entity.Skill;
import domaine.entity.Training;
import domaine.repository.TrainingRepository;

public class TrainingRepositorySql implements TrainingRepository {

    private static final String SQL_NEEDED_SKILLS = """
            SELECT skill.id, skill.name
            FROM training_skills_needed
            JOIN skill ON training_skills_needed.skill_id = skill.id
            WHERE training_skills_needed.training_formation_number = ?""";

    private static final String SQL_DELIVERED_SKILLS = """
            SELECT skill.id, skill.name
            FROM training_skills_delivered
            JOIN skill ON training_skills_delivered.skill_id = skill.id
            WHERE training_skills_delivered.training_formation_number = ?""";

    private final JdbcTemplate jdbc;

    public TrainingRepositorySql(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------- Écriture ----------

    @Override
    @Transactional
    public void addTraining(Training training) {
        jdbc.update("""
                INSERT INTO training (training_number, name, start_date, end_date, has_differentiation)
                VALUES (?, ?, ?, ?, ?)
                """,
                training.getTrainingNumber(),
                training.getName(),
                training.getStartDate(),
                training.getEndDate(),
                training.hasDifferentiation());

        addSkills("training_skills_needed", training.getTrainingNumber(), training.getSkillsNeeded());
        addSkills("training_skills_delivered", training.getTrainingNumber(), training.getSkillDelivered());
    }

    private void addSkills(String table, int formationNumber, List<Skill> skills) {
        jdbc.batchUpdate(
                "INSERT INTO " + table
                        + " (training_formation_number, skill_id) SELECT ?, id FROM skill WHERE name = ?",
                skills.stream()
                        .map(skill -> new Object[] { formationNumber, skill.getName() })
                        .toList());
    }

    // ---------- Lecture ----------

    @Override
    public boolean existsByTrainingNumber(int formationNumber) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM training WHERE training_number = ?", Integer.class, formationNumber);
        return count > 0;
    }

    @Override
    public List<Training> findAll() {
        return jdbc.query("SELECT * FROM training ORDER BY start_date", this::mapTraining);
    }

    @Override
    public Training findByTrainingNumber(int formationNumber) {
        return jdbc.query("SELECT * FROM training WHERE training_number = ?", this::mapTraining, formationNumber)
                .stream().findFirst().orElse(null);
    }

    private Training mapTraining(ResultSet rs, int rowNum) throws SQLException {
        int formationNumber = rs.getInt("training_number");
        return new Training(
                rs.getString("name"),
                formationNumber,
                rs.getObject("start_date", LocalDate.class),
                rs.getObject("end_date", LocalDate.class),
                findSkills(SQL_NEEDED_SKILLS, formationNumber),
                findSkills(SQL_DELIVERED_SKILLS, formationNumber),
                rs.getBoolean("has_differentiation"));
    }

    private List<Skill> findSkills(String sql, int formationNumber) {
        return jdbc.query(sql, (rs, rowNum) -> {
            Skill skill = new Skill(rs.getString("name"));
            skill.setId(rs.getInt("id"));
            return skill;
        }, formationNumber);
    }
}