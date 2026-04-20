import java.util.Vector;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TournamentData {

    public int id;
    public int organizer_id;
    public String tournament;
    public String modality;
    public String location;
    public String tournament_date;
    public double entry_price;
    public double win_price;
    public String rules;
    public int max_partici;

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


    public static Vector<TournamentData> getTournamentList(Connection connection) {

        Vector<TournamentData> vec = new Vector<>();

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
                vec.add(t);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in getTournamentList: " + sql + " Exception: " + e);
        }

        return vec;
    }


    public static Vector<TournamentData> searchTournaments(Connection connection,
                                                           String modality,
                                                           String location) {

        Vector<TournamentData> vec = new Vector<>();

        String sql = "SELECT * FROM tournaments WHERE 1=1";

        if (modality != null && !modality.trim().isEmpty()) {
            sql += " AND modality LIKE ?";
        }

        if (location != null && !location.trim().isEmpty()) {
            sql += " AND location LIKE ?";
        }

        System.out.println("searchTournaments SQL: " + sql);

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            int index = 1;

            if (modality != null && !modality.trim().isEmpty()) {
                stmt.setString(index++, "%" + modality + "%");
            }

            if (location != null && !location.trim().isEmpty()) {
                stmt.setString(index++, "%" + location + "%");
            }

            ResultSet rs = stmt.executeQuery();

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
                vec.add(t);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vec;
    }
	public static String checkRegistration(Connection connection, int userId, int tournamentId) {

    String sql = "SELECT status FROM registrations WHERE user_id = ? AND tournament_id = ?";

    try {
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.setInt(2, tournamentId);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getString("status"); // accepted / pending / rejected
        }

        rs.close();
        stmt.close();

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return "not_found";
	}
public static String getTournamentModality(Connection connection, int tournamentId) {
    String sql = "SELECT modality FROM tournaments WHERE ID = ?";
    try {
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, tournamentId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String mod = rs.getString("modality");
            rs.close();
            stmt.close();
            return mod;
        }
        rs.close();
        stmt.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

public static java.util.List<Integer> getAcceptedPlayers(Connection connection, int tournamentId) {
    java.util.List<Integer> players = new java.util.ArrayList<>();
    String sql = "SELECT user_id FROM registrations WHERE tournament_id = ? AND status = 'accepted'";
    try {
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, tournamentId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            players.add(rs.getInt("user_id"));
        }
        rs.close();
        stmt.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return players;
}


}
