package infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import domaine.entity.RegistrationAgentInTraining;
import domaine.entity.RegistrationStatus;
import domaine.repository.RegistrationAgentInTrainingRepository;

public class RegistrationAgentInTrainingRepositorySql implements RegistrationAgentInTrainingRepository{
    private Connection conn;

    public RegistrationAgentInTrainingRepositorySql(Connection conn) {
        this.conn = conn;
    }
    
    @Override
    public void save(RegistrationAgentInTraining registration) {
        String sql ="""
                INSERT INTO Registration_agent_in_training (registration_start, registration_end, status, agent_id,training_id) VALUES (?, ? ,?,?,?)
                """;
                try(PreparedStatement pstmt = conn.prepareStatement(sql, Statement.KEEP_CURRENT_RESULT)) {
                    pstmt.setString(1, registration.getStartRegistration().toString());
                    pstmt.setString(2, registration.getEndRegistration().toString());
                    pstmt.setString(3, registration.getStatut().toString());
                    pstmt.setInt(4, registration.getIdAgent());
                    pstmt.setInt(5, registration.getIdTraining());
                    
                    pstmt.executeUpdate();

                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            registration.setId(keys.getInt(1));   // récupère l'id généré
                        }
                    }

                } catch (SQLException e) {
                    throw new RuntimeException("Erreur dans la sauvegarde de l'inscription",e);
                }
    }

    @Override
    public List<RegistrationAgentInTraining> findByAgent(int agentId) {
        String sql ="""
                SELECT * FROM  Registration_agent_in_training WHERE agent_id = ?
                """;
        try(PreparedStatement pstmt = conn.prepareStatement(sql, Statement.KEEP_CURRENT_RESULT)) {
            pstmt.setInt(1, agentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                RegistrationAgentInTraining registration = new RegistrationAgentInTraining(
                    LocalDate.parse(rs.getString("registration_start")), 
                    LocalDate.parse(rs.getString("registration_end")),
                    RegistrationStatus.valueOf(rs.getString("status")), 
                    rs.getInt("agent_id"),
                    rs.getInt("training_id")
                );
                registration.setId(rs.getInt("id"));
                return List.of(registration);
            }
            return List.of();
            

        } catch (SQLException e) {
            throw new RuntimeException("Erreur recuperation inscription par agent" + agentId,e);
        }
    }

    @Override
    public List<RegistrationAgentInTraining> findByTraining(int trainingId) {
       String sql ="""
                SELECT * FROM  Registration_agent_in_training WHERE training_id = ?
                """;
                try(PreparedStatement pstmt = conn.prepareStatement(sql, Statement.KEEP_CURRENT_RESULT)) {
                    pstmt.setInt(1, trainingId);
                    ResultSet rs = pstmt.executeQuery();
                    while(rs.next()){
                        RegistrationAgentInTraining registration  = new RegistrationAgentInTraining(
                            rs.getString("registration_start") == null ? null : LocalDate.parse(rs.getString("registration_start")), 
                            rs.getString("registration_end") == null ? null : LocalDate.parse(rs.getString("registration_end")),
                            RegistrationStatus.valueOf(rs.getString("status")), 
                            rs.getInt("agent_id"),
                            rs.getInt("training_id")
                        );
                    
                        registration.setId(rs.getInt("id"));
                        return List.of(registration);
                
                    }
                    return List.of();
                }
                catch (SQLException e) {
                    throw new RuntimeException("Erreur recuperation inscription par formation" + trainingId,e);
                }
    }

    @Override
    public RegistrationAgentInTraining findById(int id) {
        String sql = "Select * from Registration_agent_in_training WHERE id = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql, Statement.KEEP_CURRENT_RESULT)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
            RegistrationAgentInTraining registration  =new RegistrationAgentInTraining(
                    rs.getString("registration_start") == null ? null : LocalDate.parse(rs.getString("registration_start")), 
                    rs.getString("registration_end") == null ? null : LocalDate.parse(rs.getString("registration_end")),
                    RegistrationStatus.valueOf(rs.getString("status")), 
                    rs.getInt("agent_id"),
                    rs.getInt("training_id")
                );
                registration.setId(rs.getInt("id"));
                return  registration;
            }
            return null;
            
        }
        catch(SQLException e){
            throw new RuntimeException("Erreur recuperation inscription par id" + id,e);
        }
    }


    @Override
    public void update(RegistrationAgentInTraining registration) {
        String sql = "update Registration_agent_in_training set registration_start = ?, registration_end = ?, status = ? where id = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)){
            pstm.setString(1, registration.getStartRegistration().toString());
            pstm.setString(2, registration.getEndRegistration().toString());
            pstm.setString(3, registration.getStatut().toString());
            pstm.setInt(4, registration.getId());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur dans l'update d'une inscription",e);
        }
    }

}
