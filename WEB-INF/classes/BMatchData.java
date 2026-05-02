import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// JAIME FERNANDEZ DE BETOÑO

public class BMatchData {

    public static int insertMatchScore(Connection connection, int idTournament, int idPlayer1, int idPlayer2, int pointsP1, int pointsP2) {
        int n = 0;
        String sql = "INSERT INTO matches (id_tournament, id_player1, id_player2, points_p1, points_p2) VALUES (?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idTournament);
            pstmt.setInt(2, idPlayer1);
            pstmt.setInt(3, idPlayer2);
            pstmt.setInt(4, pointsP1);
            pstmt.setInt(5, pointsP2);
            
            n = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error en insertMatchScore: " + e);
        }
        return n;
    }

    public static String getTournamentName(Connection connection, int id) {
        String name = "Desconocido";
        String sql = "SELECT tournament FROM tournaments WHERE ID = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("tournament");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error en getTournamentName: " + e);
        }
        return name;
    }

    public static String getPlayerUsername(Connection connection, int id) {
        String name = "Desconocido";
        String sql = "SELECT username FROM users WHERE ID = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("username");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error en getPlayerUsername: " + e);
        }
        return name;
    }
}