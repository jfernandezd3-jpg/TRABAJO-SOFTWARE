import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserData {

    public int id;
    public String username;
    public String email;
    public String password;
    public String phone;
    public String role;

    public UserData(int id, String username, String email, String password, String phone, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    public static UserData getUserByEmail(Connection connection, String email) {
        String sql = "SELECT * FROM users WHERE email=?";
        UserData user = null;

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, email);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public static int updateUser(Connection connection, UserData user) {
        String sql = "UPDATE users SET username=?, password=?, phone=? WHERE id=?";
        int n = 0;

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, user.username);
            pstmt.setString(2, user.password);
            pstmt.setString(3, user.phone);
            pstmt.setInt(4, user.id);

            n = pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return n;
    }

    public static int deleteUser(Connection connection, int id) {
        String sql = "DELETE FROM users WHERE id=?";
        int n = 0;

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            n = pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return n;
    }
}
