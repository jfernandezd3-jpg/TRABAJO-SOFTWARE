import java.util.Vector;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TournamentData {

    int id;
    int organizer_id;
    String tournament; // OJO: Tu compañero ha llamado 'tournament' al nombre del torneo
    String modality;
    String location;
    String tournament_date;
    double entry_price;
    double win_price;
    String rules;
    int max_partici;

    // Constructor principal
    public TournamentData(int id, int organizer_id, String tournament, String modality,
                          String location, String tournament_date, double entry_price,
                          double win_price, String rules, int max_partici) {

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
    }

    // LISTA COMPLETA (sin filtros) - CREADA POR TU COMPAÑERO
    public static Vector<TournamentData> getTournamentList(Connection connection) {

        Vector<TournamentData> vec = new Vector<TournamentData>();

        String sql = "SELECT * FROM tournaments";
        System.out.println("getTournamentList: " + sql);

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                TournamentData t = new TournamentData(
                    rs.getInt("ID"),
                    rs.getInt("organizer_id"),
                    rs.getString("tournament"),
                    rs.getString("modality"),
                    rs.getString("location"),
                    rs.getString("tournament_date"),
                    rs.getDouble("entry_price"),
                    rs.getDouble("win_price"),
                    rs.getString("rules"),
                    rs.getInt("max_participants")
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

    // =====================================================================
    // NUEVO CÓDIGO AÑADIDO PARA LA BAJA (No afecta a lo de arriba)
    // =====================================================================
    
    // Metodo para desapuntar (eliminar de registrations)
    public static int deleteRegistration(Connection connection, int tournamentId, String username) {
        int n = 0;
        
        // Primero buscamos el ID del usuario usando su username
        String sqlUser = "SELECT ID FROM users WHERE username = ?";
        System.out.println("getUserID: " + sqlUser);
        
        try {
            PreparedStatement pstmtUser = connection.prepareStatement(sqlUser);
            pstmtUser.setString(1, username);
            ResultSet rsUser = pstmtUser.executeQuery();
            
            if(rsUser.next()) {
                int userId = rsUser.getInt("ID");
                
                // Si existe el usuario, lo borramos de la tabla registrations
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