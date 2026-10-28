package infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import domaine.entity.Skill;
import domaine.repository.SkillRepository;

public class SkillRepositorySql implements SkillRepository {
    private final Connection conn;

    public SkillRepositorySql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Skill findByName(String name) {
        String sql = """
                Select * FROM Skill WHERE name = ?;
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Skill skill = new Skill(
                        rs.getString("name"));
                skill.setId(rs.getInt("id"));
                return skill;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération Skill: " + name, e);
        }
    }

    @Override
    public List<Skill> findAll() {
        String sql = """
                Select * FROM Skill;
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()){
            
            List<Skill> skills = new ArrayList<>();
            
            while (rs.next()) {
                Skill skill = new Skill(
                        rs.getString("name"));
                skill.setId(rs.getInt("id"));
                skills.add(skill);
            }
            return skills;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération des Skills", e);
        }
    }
}
