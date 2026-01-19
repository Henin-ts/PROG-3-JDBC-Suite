import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    Dotenv dotenv = Dotenv.load();

    String url = dotenv.get("db_url");
    String user = dotenv.get("db_user");
    String password = dotenv.get("db_password");

    public Connection getConnection(){

        try{
            Connection conn = DriverManager.getConnection(url,user,password);
            System.out.println("Connexion ok !");
            return conn;
        } catch (SQLException e) {
            System.err.println("Erreur de connexion !");
            throw new RuntimeException(e);
        }
    }
}
