import java.util.Vector;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TournamentData {

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

    /**
     * Inserta el torneo usando los campos de esta clase TournamentData
     */
    public static int insertTournament(Connection con, TournamentData t) {
        int result = 0;
        // Columnas exactas de tu tabla en Access
        String sql = "INSERT INTO Tournaments (Name, Modality, [Date], Address, Rules, Prizes, MaxParticipants, EntryPrice, OrganizerID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // Mapeamos las variables de tu clase t a las columnas del Access
            ps.setString(1, t.tournament);      // Columna: Name
            ps.setString(2, t.modality);        // Columna: Modality
            ps.setString(3, t.tournament_date); // Columna: [Date]
            ps.setString(4, t.location);        // Columna: Address
            ps.setString(5, t.rules);           // Columna: Rules
            ps.setDouble(6, t.win_price);       // Columna: Prizes
            ps.setInt(7, t.max_partici);        // Columna: MaxParticipants
            ps.setDouble(8, t.entry_price);     // Columna: EntryPrice
            ps.setInt(9, t.organizer_id);       // Columna: OrganizerID
            
            result = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error SQL al insertar: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}