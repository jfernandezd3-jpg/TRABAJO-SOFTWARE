import java.util.Vector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PTournamentData {
    int id; // Añadido y vital para la edición
    int organizer_id;
    String tournament, modality, location, tournament_date, rules;
    double win_price, entry_price;
    int max_partici;

    // CONSTRUCTOR 1: Para CREAR torneos (FR13) - No lleva ID porque Access lo genera solo
    public PTournamentData(int organizer_id, String tournament, String modality, String location, 
                          String tournament_date, double entry_price, double win_price, String rules, int max_partici) {
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

    // CONSTRUCTOR 2: Para LEER y EDITAR torneos (FR14) - Incluye el ID
    public PTournamentData(int id, int organizer_id, String tournament, String modality, String location, 
                          String tournament_date, double entry_price, double win_price, String rules, int max_partici) {
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

    // ==========================================================
    // MÉTODO 1: CREAR TORNEO (FR13)
    // ==========================================================
    public static int insertTournament(Connection con, PTournamentData t) {
        int result = 0;
        String sql = "INSERT INTO tournaments (tournament, modality, tournament_date, location, rules, win_price, max_participants, entry_price, organizer_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.tournament);
            ps.setString(2, t.modality);
            ps.setString(3, t.tournament_date);
            ps.setString(4, t.location);
            ps.setString(5, t.rules);
            ps.setDouble(6, t.win_price);
            ps.setInt(7, t.max_partici);
            ps.setDouble(8, t.entry_price);
            ps.setInt(9, t.organizer_id);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error SQL al insertar: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    // ==========================================================
    // MÉTODO 2: LISTAR TORNEOS DEL ORGANIZADOR
    // ==========================================================
    public static Vector<PTournamentData> getTournamentsByOrganizer(Connection con, int orgId) {
        Vector<PTournamentData> lista = new Vector<>();
        String sql = "SELECT id, tournament, modality, location, tournament_date, entry_price, win_price, rules, max_participants FROM tournaments WHERE organizer_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orgId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Usamos el Constructor 2 (con ID)
                PTournamentData t = new PTournamentData(
                    rs.getInt("id"),
                    orgId,
                    rs.getString("tournament"),
                    rs.getString("modality"),
                    rs.getString("location"),
                    rs.getString("tournament_date"),
                    rs.getDouble("entry_price"),
                    rs.getDouble("win_price"),
                    rs.getString("rules"),
                    rs.getInt("max_participants")
                );
                lista.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // ==========================================================
    // MÉTODO 3: BUSCAR UN SOLO TORNEO (Para rellenar el formulario de Edición)
    // ==========================================================
    public static PTournamentData getTournamentById(Connection con, int id) {
        PTournamentData t = null;
        String sql = "SELECT * FROM tournaments WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Usamos el Constructor 2 (con ID)
                t = new PTournamentData(
                    rs.getInt("id"),
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
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return t;
    }

    // ==========================================================
    // MÉTODO 4: ACTUALIZAR TORNEO (FR14)
    // ==========================================================
    public static int updateTournament(Connection con, PTournamentData t) {
        int result = 0;
        String sql = "UPDATE tournaments SET tournament=?, modality=?, tournament_date=?, location=?, rules=?, win_price=?, max_participants=?, entry_price=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.tournament);
            ps.setString(2, t.modality);
            ps.setString(3, t.tournament_date);
            ps.setString(4, t.location);
            ps.setString(5, t.rules);
            ps.setDouble(6, t.win_price);
            ps.setInt(7, t.max_partici);
            ps.setDouble(8, t.entry_price);
            ps.setInt(9, t.id); // Aquí usamos el ID oculto para actualizar la fila correcta
            result = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }
}