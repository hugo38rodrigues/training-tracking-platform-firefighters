package infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import domaine.entity.Skill;
import domaine.entity.Training;
import domaine.repository.TrainingRepository;

public class TrainingRepositorySql implements TrainingRepository{
    
    private final Connection conn;    
    
    String SQL_DELIVERED_SKILLS = """
            SELECT Skill.id, Skill.name
            FROM Training_skills_delivered
            INNER JOIN Skill ON Training_skills_delivered.skill_id = Skill.id
            WHERE Training_skills_delivered.training_id = ?""";
    String SQL_NEEDED_SKILLS = """
            SELECT Skill.id, Skill.name
            FROM Training_skills_needed
            JOIN Skill ON Training_skills_needed.skill_id = Skill.id
            WHERE Training_skills_needed.training_id = ?""";

    public TrainingRepositorySql(Connection conn) {   
        this.conn = conn;
    }

    @Override
    public void addTraining(Training training) {
        String sql ="""
                INSERT INTO Training (name, formation_number, start_date, end_date, has_differentiation)
                VALUES(?, ?, ?, ?, ?)
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) ) {
            pstmt.setString(1, training.getName());
            pstmt.setInt(2, training.getFormationNumber());
            pstmt.setString(3, training.getStartDate().toString());
            pstmt.setString(4, training.getEndDate().toString());
            pstmt.setInt(5, training.hasDifferentiation() ? 1 : 0);
            pstmt.executeUpdate();
            try (ResultSet keys= pstmt.getGeneratedKeys()){
                if (keys.next()) {
                    training.setId(keys.getInt(1));
                }
            }
            String sqlDelivredSkills= "INSERT INTO Training_skills_delivered (training_id, skill_id) VALUES(?, ?)";
            String sqlNeededSkills= "INSERT INTO Training_skills_needed (training_id, skill_id) VALUES (?, ?)";
            addSkill(training.getId(), training.getSkillDelivered(), sqlDelivredSkills);
            addSkill(training.getId(), training.getSkillsNeeded(), sqlNeededSkills);
        }
        catch(SQLException e){
            throw new RuntimeException("Erreur insertion training numberFormation: " +training.getFormationNumber(), e);
        }
    }

    @Override
    public boolean findTrainingByFormationNumber(int formationNumber) {
        String sql = "SELECT Count(*) FROM Training WHERE formation_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, formationNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
               return rs.getInt(1) > 0;
            }
            return false;
        }

         catch (SQLException e) {
            throw new RuntimeException("Erreur récupération de la formation: " + formationNumber, e);
        }
    }

    @Override
    public List<Training> findTrainings() {
        String sql = "Select * from Training";
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            List<Training> trainings = new ArrayList<>();
            while (rs.next()) {
                Training training = new Training(
                    rs.getString("name"),
                    rs.getInt("formation_number"),
                    LocalDate.parse(rs.getString("start_date")),
                    LocalDate.parse(rs.getString("end_date")),
                    new ArrayList<Skill>(),
                    new ArrayList<Skill>(),
                    rs.getBoolean("has_differentiation")
                );
                training.setId(rs.getInt("id"));
                training.setSkillsNeeded(findSkills(training.getId(), SQL_NEEDED_SKILLS));
                training.setSkillDelivered(findSkills(training.getId(), SQL_DELIVERED_SKILLS));
                trainings.add(training);
            }
            return trainings;
        }

        catch (SQLException e) {
            throw new RuntimeException("Erreur récupération des formations: ", e);
        }
    }
    
    @Override
    public Training findTrainingById(int id) {
        String sql = "SELECT * FROM Training WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
               int trainingId = rs.getInt("id");   // lis l'id une fois

                Training training = new Training(
                    rs.getString("name"),
                    rs.getInt("formation_number"),
                    LocalDate.parse(rs.getString("start_date")),
                    LocalDate.parse(rs.getString("end_date")),
                    new ArrayList<Skill>(),  
                    new ArrayList<Skill>(),    
                    rs.getBoolean("has_differentiation")
                );
                
                training.setId(trainingId);
                training.setSkillsNeeded(findSkills(trainingId, SQL_NEEDED_SKILLS));
                training.setSkillDelivered(findSkills(trainingId, SQL_DELIVERED_SKILLS));
                return training;
            }
            return null;
        }

         catch (SQLException e) {
            throw new RuntimeException("Erreur récupération de la formation: " + id, e);
        }
    }

    private List<Skill> findSkills(Integer trainingId, String sql) {
        List<Skill> skills = new ArrayList<Skill>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainingId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Skill skill = new Skill(rs.getString("name"));
                skill.setId(rs.getInt("id"));
                skills.add(skill);
            }
            return skills;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur compétence délivrée: " + trainingId, e);
        }
    }

    private void addSkill(int trainingId, List<Skill> skills, String sql) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for(Skill skill : skills){
                pstmt.setInt(1, trainingId);
                pstmt.setInt(2, skill.getId());      
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

}
