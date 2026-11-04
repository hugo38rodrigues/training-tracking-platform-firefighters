package infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import domaine.entity.Rank;
import domaine.entity.RankCondition;
import domaine.entity.Skill;
import domaine.repository.RankConditionRepository;

public class RankConditionSql implements RankConditionRepository {
    private Connection conn;
    
    public RankConditionSql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<RankCondition> getRankCondition() {
        String sql = "Select * from Ranking_condition";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            ResultSet rs = pstmt.executeQuery();
            List<RankCondition> rankConditions = new ArrayList<RankCondition>();
            while(rs.next()){
                RankCondition rankCondition = new RankCondition(
                    Rank.valueOf(rs.getString("rank")),  
                    Rank.valueOf(rs.getString("previous_rank")),
                    rs.getInt("required_years"),
                    new ArrayList<>()
                );
                rankCondition.setId(rs.getInt("id"));
                rankCondition.setRequireSkills(findRequiredSkills(rankCondition.getId()));
                rankConditions.add(rankCondition);
            }
            return rankConditions;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupéreation des RankCondition",e);
        }

    }

    private List<Skill> findRequiredSkills(int rankConditionId) {
        String sql = "Select * from Skill join into on Ranking_condition_skill.skill_id =  Skill.id Where rank_condition_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, rankConditionId);
            ResultSet rs = pstmt.executeQuery();
            List<Skill> skills = new ArrayList<Skill>();
            while(rs.next()){
                Skill skill = new Skill(rs.getString("name")); 
                skill.setId(rs.getInt("id"));
                skills.add(skill);
            }
            return skills;
        } catch (SQLException e) {
             throw new RuntimeException("Erreur lors de la récupéreation des Skill pour RankCondition",e);
        }

    }

}
