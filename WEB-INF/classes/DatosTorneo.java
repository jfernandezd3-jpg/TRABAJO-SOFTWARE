import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Clase de datos para el torneo (FR13).
 * Autor: Paul
 */
public class DatosTorneo {
    private String name, modality, date, address, rules, prizes;
    private int maxParticipants;
    private double entryPrice;

    // Constructor que coincide exactamente con los parámetros de tu Servlet
    public DatosTorneo(String name, String modality, String date, String address, 
                       String rules, String prizes, int maxParticipants, double entryPrice) {
        this.name = name;
        this.modality = modality;
        this.date = date;
        this.address = address;
        this.rules = rules;
        this.prizes = prizes;
        this.maxParticipants = maxParticipants;
        this.entryPrice = entryPrice;
    }

    /**
     * Inserta el objeto torneo en la base de datos Microsoft Access.
     */
    public static int insertTournament(Connection con, DatosTorneo t) {
    int result = 0;
    // Hemos ajustado los nombres para que coincidan EXACTAMENTE con tu imagen de Access
    // Usamos [Date] porque "Date" es una palabra reservada en bases de datos.
    String sql = "INSERT INTO Tournaments (Name, Modality, [Date], Address, Rules, Prizes, MaxParticipants, EntryPrice) VALUES (?,?,?,?,?,?,?,?)";
    
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, t.name);
        ps.setString(2, t.modality);
        ps.setString(3, t.date);
        ps.setString(4, t.address);
        ps.setString(5, t.rules);
        ps.setString(6, t.prizes);
        ps.setInt(7, t.maxParticipants);
        ps.setDouble(8, t.entryPrice);
        
        result = ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error al insertar torneo: " + e.getMessage());
        e.printStackTrace();
    }
    return result;
	}
}