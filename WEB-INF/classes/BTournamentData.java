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
    double latitude;  // NUEVO
    double longitude; // NUEVO

    // Constructor principal actualizado con latitud y longitud
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

    // LISTA COMPLETA
    public static Vector<BTournamentData> getTournamentList(Connection connection) {

        Vector<BTournamentData> vec = new Vector<BTournamentData>();

        String sql = "SELECT * FROM tournaments";
        System.out.println("getTournamentList: " + sql);

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                BTournamentData t = new BTournamentData(
                    rs.getInt("ID"),
                    rs.getInt("organizer_id"),
                    rs.getString("tournament"),
                    rs.getString("modality"),
                    rs.getString("location"),
                    rs.getString("tournament_date"),
                    rs.getDouble("entry_price"),
                    rs.getDouble("win_price"),
                    rs.getString("rules"),
                    rs.getInt("max_participants"),
                    rs.getDouble("latitude"),  // Leemos latitud
                    rs.getDouble("longitude")  // Leemos longitud
                );
                vec.addElement(t);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in getTournamentList: " + sql + " Exception: " + e);
        }

        return vec;
    }

    // ==========================================================
    // NUEVO MÉTODO: BUSCAR UN SOLO TORNEO POR ID
    // ==========================================================
    public static BTournamentData getTournamentById(Connection connection, int id) {
        BTournamentData t = null;
        String sql = "SELECT * FROM tournaments WHERE ID = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                t = new BTournamentData(
                    rs.getInt("ID"),
                    rs.getInt("organizer_id"),
                    rs.getString("tournament"),
                    rs.getString("modality"),
                    rs.getString("location"),
                    rs.getString("tournament_date"),
                    rs.getDouble("entry_price"),
                    rs.getDouble("win_price"),
                    rs.getString("rules"),
                    rs.getInt("max_participants"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude")
                );
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in getTournamentById Exception: " + e);
        }
        return t;
    }

    // Metodo para desapuntar
    public static int deleteRegistration(Connection connection, int tournamentId, String username) {
        int n = 0;
        
        String sqlUser = "SELECT ID FROM users WHERE email = ?"; 
        System.out.println("getUserID: " + sqlUser);
        
        try {
            PreparedStatement pstmtUser = connection.prepareStatement(sqlUser);
            pstmtUser.setString(1, username);
            ResultSet rsUser = pstmtUser.executeQuery();
            
            if(rsUser.next()) {
                int userId = rsUser.getInt("ID");
                
                String sqlDelete = "DELETE FROM registrations WHERE user_id = ? AND torunament_id = ?";
                System.out.println("deleteRegistration: " + sqlDelete);
                
                PreparedStatement pstmtDel = connection.prepareStatement(sqlDelete);
                pstmtDel.setInt(1, userId);
                pstmtDel.setInt(2, tournamentId);
                
                n = pstmtDel.executeUpdate();
                pstmtDel.close();
            }
            rsUser.close();
            pstmtUser.close();
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Error in deleteRegistration Exception: " + e);
        }
        return n;
    }
}