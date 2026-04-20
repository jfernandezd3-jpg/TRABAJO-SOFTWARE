import java.util.Vector;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BTournamentData {

    int id;
    int organizer_id;
    String tournament; 
    String modality;
    String location;
    String tournament_date;
    double entry_price;
    double win_price;
    String rules;
    int max_partici;
    double latitude;
    double longitude;

    // Constructor principal
    public BTournamentData(int id, int organizer_id, String tournament, String modality,
                          String location, String tournament_date, double entry_price,
                          double win_price, String rules, int max_partici, 
                          double latitude, double longitude) {

        this.id = id;
        this.organizer_id = organizer_id;
        this.tournament = tournament;
        this.modality = modality;
        this.location = location;
        this.tournament_date = tournament_date;
        this.entry_price = entry_price;
        this.win_price = win_price;
        this.rules = rules;
        this.max_partici = max_partici;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // 1. OBTENER TODOS LOS TORNEOS
    public static Vector<BTournamentData> getTournamentList(Connection connection) {
        Vector<BTournamentData> vec = new Vector<BTournamentData>();
        String sql = "SELECT * FROM tournaments";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                BTournamentData t = new BTournamentData(
                    rs.getInt("ID"), rs.getInt("organizer_id"), rs.getString("tournament"),
                    rs.getString("modality"), rs.getString("location"), rs.getString("tournament_date"),
                    rs.getDouble("entry_price"), rs.getDouble("win_price"), rs.getString("rules"),
                    rs.getInt("max_participants"), rs.getDouble("latitude"), rs.getDouble("longitude")
                );
                vec.addElement(t);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error in getTournamentList: " + sql + " Exception: " + e);
        }
        return vec;
    }

    // 2. OBTENER UN TORNEO POR ID
    public static BTournamentData getTournamentById(Connection connection, int id) {
        BTournamentData t = null;
        String sql = "SELECT * FROM tournaments WHERE ID = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                t = new BTournamentData(
                    rs.getInt("ID"), rs.getInt("organizer_id"), rs.getString("tournament"),
                    rs.getString("modality"), rs.getString("location"), rs.getString("tournament_date"),
                    rs.getDouble("entry_price"), rs.getDouble("win_price"), rs.getString("rules"),
                    rs.getInt("max_participants"), rs.getDouble("latitude"), rs.getDouble("longitude")
                );
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getTournamentById Exception: " + e);
        }
        return t;
    }

    // 3. OBTENER SOLO TORNEOS CON COORDENADAS PARA EL MAPA
    public static Vector<BTournamentData> getTournamentsWithCoordinates(Connection connection) {
        Vector<BTournamentData> vec = new Vector<BTournamentData>();
        String sql = "SELECT * FROM tournaments WHERE latitude IS NOT NULL AND longitude IS NOT NULL";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                BTournamentData t = new BTournamentData(
                    rs.getInt("ID"), rs.getInt("organizer_id"), rs.getString("tournament"),
                    rs.getString("modality"), rs.getString("location"), rs.getString("tournament_date"),
                    rs.getDouble("entry_price"), rs.getDouble("win_price"), rs.getString("rules"),
                    rs.getInt("max_participants"), rs.getDouble("latitude"), rs.getDouble("longitude")
                );
                vec.addElement(t);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error in getTournamentsWithCoordinates: " + sql + " Exception: " + e);
        }
        return vec;
    }

    // 4. DESAPUNTAR USUARIO
    public static int deleteRegistration(Connection connection, int tournamentId, String username) {
        int n = 0;
        String sqlUser = "SELECT ID FROM users WHERE email = ?"; 
        try {
            PreparedStatement pstmtUser = connection.prepareStatement(sqlUser);
            pstmtUser.setString(1, username);
            ResultSet rsUser = pstmtUser.executeQuery();
            if(rsUser.next()) {
                int userId = rsUser.getInt("ID");
                String sqlDelete = "DELETE FROM registrations WHERE user_id = ? AND tournament_id = ?";
                PreparedStatement pstmtDel = connection.prepareStatement(sqlDelete);
                pstmtDel.setInt(1, userId);
                pstmtDel.setInt(2, tournamentId);
                n = pstmtDel.executeUpdate();
                pstmtDel.close();
            }
            rsUser.close();
            pstmtUser.close();
        } catch(SQLException e) {
            System.out.println("Error in deleteRegistration Exception: " + e);
        }
        return n;
    }

    // 5. OBTENER SOLO LOS TORNEOS A LOS QUE ESTA APUNTADO EL USUARIO
    public static Vector<BTournamentData> getTournamentsByUserEmail(Connection connection, String email) {
        Vector<BTournamentData> vec = new Vector<BTournamentData>();
        
        String sql = "SELECT t.* FROM tournaments t, registrations r, users u " +
                     "WHERE t.ID = r.tournament_id AND r.user_id = u.ID AND u.email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BTournamentData t = new BTournamentData(
                    rs.getInt("ID"), rs.getInt("organizer_id"), rs.getString("tournament"),
                    rs.getString("modality"), rs.getString("location"), rs.getString("tournament_date"),
                    rs.getDouble("entry_price"), rs.getDouble("win_price"), rs.getString("rules"),
                    rs.getInt("max_participants"), rs.getDouble("latitude"), rs.getDouble("longitude")
                );
                vec.addElement(t);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getTournamentsByUserEmail: " + sql + " Exception: " + e);
        }
        return vec;
    }
}