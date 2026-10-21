package infrastructure;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import domaine.entity.Agent;
import domaine.entity.License;
import domaine.entity.Rank;
import domaine.entity.Skill;
import domaine.repository.AgentRepository;

public class AgentRepositorySql implements AgentRepository {

    private final Connection conn;      

    public AgentRepositorySql(Connection conn) {   
        this.conn = conn;
    }

    @Override
    public void add(Agent agent) {
        String sql = """
            INSERT INTO Agent (first_name, last_name, matricule, rank, rank_start_date, is_active, is_differentiated)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, agent.getFirstName());
            pstmt.setString(2, agent.getLastName());
            pstmt.setInt(3, agent.getMatricule());
            pstmt.setString(4, agent.getRank().name());              
            pstmt.setString(5, agent.getRankStartDate().toString()); 
            pstmt.setInt(6, agent.isActive() ? 1 : 0);              
            pstmt.setInt(7, agent.isDifferentiated() ? 1 : 0);

            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    agent.setId(keys.getInt(1));   // récupère l'id généré
                }
            }

            addLicenses(agent); 
            addSkills(agent.getId(), agent.getSkills() );  // les permis dans la table de liaison

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'agent", e);
        }
    }

    @Override
    public Agent findAgent(Integer id) {
        String sql = " SELECT * FROM Agent WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                Agent agent = new Agent(
                rs.getString("first_name"),  
                rs.getString("last_name"),  
                rs.getInt("matricule"),  
                Rank.valueOf(rs.getString("rank")),  
                LocalDate.parse(rs.getString("rank_start_date")),
                new ArrayList<>(),
                new ArrayList<>()
                );
                
                agent.setId(rs.getInt("id"));
                agent.setDifferentiated(rs.getBoolean("is_differentiated"));
                agent.setActive(rs.getBoolean("is_active"));
                agent.setLicenses(findLicensesForAgent(agent.getId()));
                agent.setSkills(findSkillsForAgent(agent.getId()));
                return agent;
            }
            return null;
        }
        catch(SQLException e){
            throw new RuntimeException("Erreur récupération de l'agent: " + id,e);
        }
    }

    @Override
    public List<Agent> findAllAgent() {
          String sql = " SELECT * FROM Agent";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            List<Agent> agents = new ArrayList<>();

            while(rs.next()){
                Agent agent = new Agent(
                    rs.getString("first_name"),  
                    rs.getString("last_name"),  
                    rs.getInt("matricule"),  
                    Rank.valueOf(rs.getString("rank")),  
                    LocalDate.parse(rs.getString("rank_start_date")),
                    new ArrayList<>(),
                    new ArrayList<>()
                );
                
                agent.setId(rs.getInt("id"));
                agent.setDifferentiated(rs.getBoolean("is_differentiated"));
                agent.setActive(rs.getBoolean("is_active"));
                agent.setLicenses(findLicensesForAgent(agent.getId()));
                agent.setSkills(findSkillsForAgent(agent.getId()));
                agents.add(agent);
            }
            return agents;
        }
        catch(SQLException e){
            throw new RuntimeException("Erreur récupération de tout les agents",e);
        }
    }

    private List<License> findLicensesForAgent(int idAgent){
        String sql = "SELECT license_name FROM Agent_license WHERE agent_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAgent);
            ResultSet rs = pstmt.executeQuery();
            List<License> licenses = new ArrayList<>();
            while(rs.next()){
                licenses.add(License.valueOf(rs.getString("license_name")));
            }
            return licenses;
        }
        catch (SQLException e) {
            throw new RuntimeException("Erreur récupération des permis pour: " + idAgent,e);
        }
    }
    
    private List<Skill> findSkillsForAgent(int idAgent){
        String sql = """
                SELECT Skill.id, Skill.name FROM Agent_skill 
                JOIN Skill ON Agent_skill.skill_id = Skill.id 
                WHERE Agent_skill.agent_id = ?
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAgent);
            ResultSet rs = pstmt.executeQuery();
            List<Skill> skills = new ArrayList<>();
            while(rs.next()){
                Skill skill = new Skill(
                    rs.getString("name")
                );
                skill.setId(rs.getInt("id"));
                skills.add(skill);
            }
            return skills;
        }
        catch (SQLException e) {
            throw new RuntimeException("Erreur récupération des compétences pour: " + idAgent,e);
        }
    }

    private void addLicenses(Agent agent) throws SQLException {
        String sql = "INSERT INTO Agent_license (agent_id, license_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (License license : agent.getLicenses()) {
                pstmt.setInt(1, agent.getId());
                pstmt.setString(2, license.name());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    private void addSkills(int id, List<Skill> skills) throws SQLException {
        String sql = "INSERT INTO Agent_skill (agent_id, skill_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Skill skill : skills) {
                pstmt.setInt(1, id);
                pstmt.setInt(2, skill.getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public boolean existsByMatricule(int matricule) {
        String sql = "SELECT COUNT(*) FROM Agent WHERE matricule = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, matricule);              // remplit le ? avec le matricule
            ResultSet rs = pstmt.executeQuery();     // executeQuery pour un SELECT
            if (rs.next()) {                         // se place sur la ligne du résultat
                return rs.getInt(1) > 0;             // COUNT > 0 → le matricule existe
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur vérification matricule", e);
        }
    }

    
}