import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserData {
    int id;
    String username;
    String email;
    String password;
    String phone;
    String role;

    public UserData(int id, String username, String email, String password, String phone, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    public static UserData getUser(Connection connection, String idStr) {
        String sql = "SELECT id, username, email, password, phone, role FROM users WHERE id=?";
        UserData user = null;

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, idStr);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new UserData(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("phone"),
                    rs.getString("role")
                );
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in getUser: " + sql + " Exception: " + e);
        }

        return user;
    }

    public static int updateUser(Connection connection, UserData user) {
        String sql = "UPDATE users SET username=?, email=?, password=?, phone=?, role=? WHERE id=?";
        int n = 0;

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, user.username);
            stmt.setString(2, user.email);
            stmt.setString(3, user.password);
            stmt.setString(4, user.phone);
            stmt.setString(5, user.role);
            stmt.setInt(6, user.id);

            n = stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in updateUser: " + sql + " Exception: " + e);
        }

        return n;
    }
}
