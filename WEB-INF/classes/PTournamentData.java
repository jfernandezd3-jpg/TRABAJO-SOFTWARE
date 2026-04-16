import java.util.Vector;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PTournamentData {
    int organizer_id;
    String tournament, modality, location, tournament_date, rules;
    double win_price, entry_price;
    int max_partici;

    // Constructor sincronizado con el Servlet
    public PTournamentData(int organizer_id, String tournament, String modality, String location, 
                          String tournament_date, double win_price, double entry_price, String rules, int max_partici) {
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
    public static int insertTournament(Connection con, PTournamentData t) {
        int result = 0;
        // Columnas exactas de tu tabla en Access
        String sql = "INSERT INTO tournaments (tournament, modality, tournament_date, location, rules, win_price, max_participants, entry_price, organizer_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
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
	
	
	public static Vector<PTournamentData> getTournamentsByOrganizer(Connection con, int orgId) {
    Vector<PTournamentData> lista = new Vector<>();
    // SQL filtrando por organizer_id
    String sql = "SELECT id, tournament, modality, location, tournament_date, win_price, entry_price, rules, max_participants FROM tournaments WHERE organizer_id = ?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, orgId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            // Creamos un objeto por cada fila encontrada
            PTournamentData t = new PTournamentData(
                orgId,
                rs.getString("tournament"),
                rs.getString("modality"),
                rs.getString("location"),
                rs.getString("tournament_date"),
                rs.getDouble("win_price"),
                rs.getDouble("entry_price"),
                rs.getString("rules"),
                rs.getInt("max_participants")
            );
            // IMPORTANTE: Si añadiste el campo 'id' a tu clase, asígnale el valor aquí
            // t.id = rs.getInt("id"); 
            lista.add(t);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}
	
	
}