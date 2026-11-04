package infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import domaine.entity.Rank;
import domaine.entity.RankCondition;
import domaine.entity.Skill;
import domaine.repository.RankConditionRepository;

public class RankConditionSql implements RankConditionRepository {

    private final JdbcTemplate jdbc;

    public RankConditionSql(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<RankCondition> getRankCondition() {
        return jdbc.query("SELECT * FROM ranking_condition", this::mapRankCondition);
    }

    // Transforme UNE ligne de ranking_condition en objet RankCondition
    private RankCondition mapRankCondition(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String previousRank = rs.getString("previous_rank");

        RankCondition rankCondition = new RankCondition(
                Rank.valueOf(rs.getString("rank")),
                previousRank == null ? null : Rank.valueOf(previousRank),
                rs.getInt("required_years"),
                findRequiredSkills(id));
        rankCondition.setId(id);
        return rankCondition;
    }

    private List<Skill> findRequiredSkills(int rankConditionId) {
        return jdbc.query("""
                SELECT skill.id, skill.name
                FROM skill
                JOIN ranking_condition_skill ON ranking_condition_skill.skill_id = skill.id
                WHERE ranking_condition_skill.rank_condition_id = ?
                """,
                (rs, rowNum) -> {
                    Skill skill = new Skill(rs.getString("name"));
                    skill.setId(rs.getInt("id"));
                    return skill;
                },
                rankConditionId);
    }
}