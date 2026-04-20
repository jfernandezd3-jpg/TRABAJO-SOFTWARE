import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}