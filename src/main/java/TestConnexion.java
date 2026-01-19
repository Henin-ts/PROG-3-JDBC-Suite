public class TestConnexion {
    public static void main(String[] args) {

        DBConnection db = new DBConnection();
        System.out.println(db.getConnection());
        
    }
}
