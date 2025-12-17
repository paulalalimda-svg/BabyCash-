import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class VerifyHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "Admin123!";
        String hashFromDb = "$2a$10$N9qo8uLOickgx2ZMRZoMye1J0tSfXOGCx0WqP/3rkuJtl8w/J9xUG"; // From create-database.sql

        boolean matches = encoder.matches(password, hashFromDb);
        System.out.println("Password: " + password);
        System.out.println("Hash from DB: " + hashFromDb);
        System.out.println("Matches: " + matches);

        if (!matches) {
            System.out.println("Generating new hash for " + password + ":");
            System.out.println(encoder.encode(password));
        }
    }
}
