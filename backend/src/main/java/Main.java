import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import infrastructure.DatabaseInitialisation;

public class Main {
    
    public static void main(String[] args){
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db")){

            DatabaseInitialisation.initialisation(conn);
            System.out.println("Base de donnée préte");         

        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}