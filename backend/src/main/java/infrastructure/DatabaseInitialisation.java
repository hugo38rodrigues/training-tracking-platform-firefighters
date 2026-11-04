package infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitialisation {

    private static void execute(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createAgent(Connection conn) throws SQLException {
        execute(conn, """
                CREATE TABLE IF NOT EXISTS agent (
                    matricule          INTEGER PRIMARY KEY,
                    first_name         TEXT    NOT NULL,
                    last_name          TEXT    NOT NULL,
                    rank               TEXT    NOT NULL,
                    rank_start_date    DATE    NOT NULL,
                    is_active          BOOLEAN NOT NULL DEFAULT TRUE,
                    is_differentiated  BOOLEAN NOT NULL DEFAULT FALSE
                )""");
    }

    private static void createSkill(Connection conn) throws SQLException {
        execute(conn, """
                CREATE TABLE IF NOT EXISTS skill (
                    id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    name TEXT    NOT NULL UNIQUE
                )""");
    }

    private static void createTraining(Connection conn) throws SQLException {
        execute(conn, """
                CREATE TABLE IF NOT EXISTS training (
                    training_number    INTEGER PRIMARY KEY,
                    name                TEXT    NOT NULL,
                    start_date          DATE    NOT NULL,
                    end_date            DATE    NOT NULL,
                    has_differentiation BOOLEAN NOT NULL DEFAULT FALSE
                )""");
    }

    private static void createAgentLicense(Connection conn) throws SQLException {
        execute(conn, """
                CREATE TABLE IF NOT EXISTS agent_license (
                    agent_matricule INTEGER NOT NULL REFERENCES agent(matricule) ON UPDATE CASCADE ON DELETE CASCADE,
                    license_name    TEXT    NOT NULL,
                    PRIMARY KEY (agent_matricule, license_name)
                )""");
    }

    private static void createAgentSkill(Connection conn) throws SQLException {
        execute(conn, """
                CREATE TABLE IF NOT EXISTS agent_skill (
                    agent_matricule INTEGER NOT NULL REFERENCES agent(matricule) ON UPDATE CASCADE ON DELETE CASCADE,
                    skill_id        INTEGER NOT NULL REFERENCES skill(id),
                    PRIMARY KEY (agent_matricule, skill_id)
                )""");
    }

    private static void createTrainingSkillsNeeded(Connection conn) throws SQLException {
        execute(conn, """
                CREATE TABLE IF NOT EXISTS training_skills_needed (
                    training_formation_number INTEGER NOT NULL
                        REFERENCES training(training_number) ON UPDATE CASCADE ON DELETE CASCADE,
                    skill_id INTEGER NOT NULL REFERENCES skill(id),
                    PRIMARY KEY (training_formation_number, skill_id)
                )""");
    }

    private static void createTrainingSkillsDelivered(Connection conn) throws SQLException {
        execute(conn, """
                CREATE TABLE IF NOT EXISTS training_skills_delivered (
                    training_formation_number INTEGER NOT NULL
                        REFERENCES training(training_number) ON UPDATE CASCADE ON DELETE CASCADE,
                    skill_id INTEGER NOT NULL REFERENCES skill(id),
                    PRIMARY KEY (training_formation_number, skill_id)
                )""");
    }

    private static void createRankingCondition(Connection conn) throws SQLException {
        execute(conn, """
                CREATE TABLE IF NOT EXISTS ranking_condition (
                    id             INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    rank           TEXT    NOT NULL UNIQUE,
                    previous_rank  TEXT,
                    required_years INTEGER NOT NULL DEFAULT 0
                )""");
    }

    private static void createRankingConditionSkill(Connection conn) throws SQLException {
        execute(conn, """
                CREATE TABLE IF NOT EXISTS ranking_condition_skill (
                    rank_condition_id INTEGER NOT NULL REFERENCES ranking_condition(id) ON DELETE CASCADE,
                    skill_id          INTEGER NOT NULL REFERENCES skill(id),
                    PRIMARY KEY (rank_condition_id, skill_id)
                )""");
    }

    private static void createRegistration(Connection conn) throws SQLException {
        execute(conn, """
                CREATE TABLE IF NOT EXISTS registration_agent_in_training (
                    id                        INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    registration_start        DATE    NOT NULL,
                    registration_end          DATE    NOT NULL,
                    status                    TEXT    NOT NULL,
                    agent_matricule           INTEGER NOT NULL
                        REFERENCES agent(matricule) ON UPDATE CASCADE ON DELETE CASCADE,
                    training_formation_number INTEGER NOT NULL
                        REFERENCES training(training_number) ON UPDATE CASCADE
                )""");
    }

    // ---------- Données de départ ----------

    private static void insertDefaultSkills(Connection conn) throws SQLException {
        String[] skills = { "EQ DIV HYM", "EQ EA", "EQ INC", "EQ PMA", "EQ PPBE",
                "EQ SSUAP", "COND VL", "COND VSAV LEGER", "COND VSAV MOYEN",
                "CE INC", "CATE SSUAP", "CATE TRANS", "CATE VTU" };

        String sql = "INSERT INTO skill (name) VALUES (?) ON CONFLICT (name) DO NOTHING";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String name : skills) {
                pstmt.setString(1, name);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private static void insertDefaultRankConditions(Connection conn) throws SQLException {
        if (!isEmpty(conn, "ranking_condition")) {
            return;
        }
        boolean previousAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            insertOneRankCondition(conn, "SAPEUR_PREMIERE_CLASSE", "SAPEUR_DEUXIEME_CLASSE", 1,
                    new String[] { "EQ DIV HYM", "EQ INC", "EQ SSUAP" });
            insertOneRankCondition(conn, "CAPORAL", "SAPEUR_PREMIERE_CLASSE", 3,
                    new String[] { "CE INC" });
            insertOneRankCondition(conn, "SERGENT", "CAPORAL", 3,
                    new String[] { "CATE SSUAP", "CATE TRANS", "CATE VTU" });
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(previousAutoCommit);
        }
    }

    private static boolean isEmpty(Connection conn, String table) throws SQLException {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next(); // se placer sur la ligne du résultat
            return rs.getInt(1) == 0;
        }
    }

    private static void insertOneRankCondition(Connection conn, String rank, String previousRank,
            int years, String[] skillNames) throws SQLException {
        int conditionId;
        String sql = """
                INSERT INTO ranking_condition (rank, previous_rank, required_years)
                VALUES (?, ?, ?) RETURNING id""";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rank);
            pstmt.setString(2, previousRank);
            pstmt.setInt(3, years);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                conditionId = rs.getInt("id");
            }
        }

        String linkSql = """
                INSERT INTO ranking_condition_skill (rank_condition_id, skill_id)
                VALUES (?, (SELECT id FROM skill WHERE name = ?))""";
        try (PreparedStatement pstmt = conn.prepareStatement(linkSql)) {
            for (String skillName : skillNames) {
                pstmt.setInt(1, conditionId);
                pstmt.setString(2, skillName);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public static void initialisation(Connection conn) throws SQLException {
        // Ordre important : une table doit exister avant qu'une autre y fasse référence
        createAgent(conn);
        createSkill(conn);
        createTraining(conn);
        createAgentLicense(conn);
        createAgentSkill(conn);
        createTrainingSkillsNeeded(conn);
        createTrainingSkillsDelivered(conn);
        createRankingCondition(conn);
        createRankingConditionSkill(conn);
        createRegistration(conn);
        insertDefaultSkills(conn);
        insertDefaultRankConditions(conn);
    }
}