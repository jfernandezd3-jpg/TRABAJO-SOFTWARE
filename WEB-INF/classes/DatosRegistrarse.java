import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatosRegistrarse {
    String username;
    String email;
    String password;
    String phone;

    public DatosRegistrarse(String username, String email, String password, String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public static int insertUser(Connection connection, DatosRegistrarse u) {
        String sql ="INSERT INTO users (username, email, password, phone, role) VALUES (?, ?, ?, ?, 'user')";
        int n = 0;
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, u.username);
            stmt.setString(2, u.email);
            stmt.setString(3, u.password);
            stmt.setString(4, u.phone);
            n = stmt.executeUpdate();
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return n;
    }
}
