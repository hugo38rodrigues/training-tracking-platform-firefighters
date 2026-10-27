package infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitialisation {
    

    private static void createAgent(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Agent (
                id                 INTEGER PRIMARY KEY AUTOINCREMENT,
                last_name          TEXT    NOT NULL,
                first_name         TEXT    NOT NULL,
                matricule          INTEGER NOT NULL UNIQUE,
                is_active          INTEGER NOT NULL DEFAULT 1,
                is_differentiated  INTEGER NOT NULL DEFAULT 0,
                rank               TEXT    NOT NULL,
                rank_start_date    TEXT    NOT NULL
            );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createRegistrationAgentInTraining(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS Registration_agent_in_training(
                    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
                    registration_start TEXT NOT NULL,
                    registration_end TEXT,
                    status TEXT NOT NULL,
                    agent_id INTEGER NOT NULL,
                    training_id  INTEGER NOT NULL,
                    FOREIGN KEY(agent_id) REFERENCES Agent (id),
                    FOREIGN KEY(training_id) REFERENCES Training(id)
                );
            """;
          try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createTraining(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Training (
                id                            INTEGER    PRIMARY KEY AUTOINCREMENT,
                formation_number              INTEGER    Not NULL UNIQUE,
                name                          TEXT       NOT NULL,
                start_date                     TEXT       NOT NULL,
                end_date                       TEXT       NOT NULL,
                has_differentiation           INTEGER    NOT NULL
            );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createSkill(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Skill (
                id   INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT    NOT NULL UNIQUE
            );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
    public static void createAgentSkill(Connection conn) throws SQLException {
    String sql = """
        CREATE TABLE IF NOT EXISTS Agent_skill (
            agent_id INTEGER NOT NULL,
            skill_id INTEGER NOT NULL,
            PRIMARY KEY (agent_id, skill_id),
            FOREIGN KEY (agent_id) REFERENCES Agent(id),
            FOREIGN KEY (skill_id) REFERENCES Skill(id)
        );
        """;
    try (Statement stmt = conn.createStatement()) {
        stmt.execute(sql);
    }
}

    private static void createTrainingNeededSkills(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Training_skills_needed (
                training_id INTEGER NOT NULL,
                skill_id INTEGER NOT NULL,
                PRIMARY KEY (training_id, skill_id),
                FOREIGN KEY(training_id) REFERENCES Training(id),
                FOREIGN KEY(skill_id) REFERENCES Skill(id)
            );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void createAgentLicense(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Agent_license (
                agent_id     INTEGER NOT NULL,
                license_name TEXT    NOT NULL,
                PRIMARY KEY (agent_id, license_name),
                FOREIGN KEY (agent_id) REFERENCES Agent(id)
            );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void createTrainingSkillDelivered(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Training_skills_delivered (
                training_id     INTEGER    NOT NULL,
                skill_id        INTEGER    NOT NULL,
                PRIMARY KEY (training_id, skill_id),
                FOREIGN KEY (training_id) REFERENCES Training(id),
                FOREIGN KEY (skill_id)    REFERENCES Skill(id)
            );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

        public static void createRankingCondition(Connection conn) throws SQLException {
            String sql = """
                CREATE TABLE IF NOT EXISTS Ranking_condition (
                    id             INTEGER PRIMARY KEY AUTOINCREMENT,
                    rank           TEXT    NOT NULL UNIQUE,
                    previous_rank  TEXT,
                    required_years INTEGER NOT NULL DEFAULT 0
                )
                """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        }

    public static void createRankingConditionSkill(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Ranking_condition_skill (
                rank_condition_id INTEGER NOT NULL,
                skill_id          INTEGER NOT NULL,
                PRIMARY KEY (rank_condition_id, skill_id),
                FOREIGN KEY (rank_condition_id) REFERENCES Ranking_condition (id),
                FOREIGN KEY (skill_id) REFERENCES Skill (id)
            )
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void insertDefaultSkills(Connection conn)throws SQLException{
        String[] skills = {"EQ DIV HYM", "EQ EA", "EQ INC", "EQ PMA", "EQ PPBE",
                   "EQ SSUAP", "COND VL", "COND VSAV LEGER", "COND VSAV MOYEN",
                   "CE INC", "CATE SSUAP", "CATE TRANS", "CATE VTU"}; 
                   
        String sql = """
                INSERT OR IGNORE INTO Skill(name) VALUES(?)
               """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String name : skills){
                pstmt.setString(1,name);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public static void insertDefaultRankConditions(Connection conn) throws SQLException {
        if (!isEmpty(conn, "Ranking_condition")) {
            return; // déjà fait lors d'un lancement précédent
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
            return rs.getInt(1) == 0;
        }
    }

        private static void insertOneRankCondition(Connection conn, String rank, String previousRank,
                                                int years, String[] skillNames) throws SQLException {
            // 1. insérer la règle, récupérer son id
            int conditionId;
            String sql = "INSERT INTO Ranking_condition (rank, previous_rank, required_years) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, rank);
                pstmt.setString(2, previousRank);
                pstmt.setInt(3, years);
                pstmt.executeUpdate();
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    keys.next();
                    conditionId = keys.getInt(1);
                }
            }

            // 2. pour chaque compétence, trouver son id et insérer le lien
            String linkSql = """
                INSERT INTO Ranking_condition_skill (rank_condition_id, skill_id)
                VALUES (?, (SELECT id FROM Skill WHERE name = ?))
                """;
            try (PreparedStatement pstmt = conn.prepareStatement(linkSql)) {
                for (String skillName : skillNames) {
                    pstmt.setInt(1, conditionId);
                    pstmt.setString(2, skillName);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        }



        public static void initialisation(Connection conn) throws SQLException{
            createAgent(conn);
            createSkill(conn);
            createTraining(conn);
            createRankingCondition(conn);
            createTrainingNeededSkills(conn);
            createAgentLicense(conn);
            createTrainingSkillDelivered(conn);
            createAgentSkill(conn);
            createRankingConditionSkill(conn);
            createRegistrationAgentInTraining(conn);
            insertDefaultSkills(conn);
            insertDefaultRankConditions(conn);

        }

    
}