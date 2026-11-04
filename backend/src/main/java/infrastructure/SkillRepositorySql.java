package infrastructure;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import domaine.entity.Skill;
import domaine.repository.SkillRepository;

public class SkillRepositorySql implements SkillRepository {

    private static final RowMapper<Skill> SKILL_MAPPER = (rs, rowNum) -> {
        Skill skill = new Skill(rs.getString("name"));
        skill.setId(rs.getInt("id"));
        return skill;
    };

    private final JdbcTemplate jdbc;

    public SkillRepositorySql(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Skill> findAll() {
        return jdbc.query("SELECT id, name FROM skill ORDER BY name", SKILL_MAPPER);
    }

    @Override
    public Skill findByName(String name) {
        return jdbc.query("SELECT id, name FROM skill WHERE name = ?", SKILL_MAPPER, name)
                .stream().findFirst().orElse(null);
    }
}