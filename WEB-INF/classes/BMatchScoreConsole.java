import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.sql.Connection;

// JAIME FERNANDEZ DE BETOÑO

@SuppressWarnings("serial")
public class BMatchScoreConsole extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        res.setContentType("text/html; charset=UTF-8");
        res.setCharacterEncoding("UTF-8");
        PrintWriter toClient = res.getWriter();
        
        String idTournament = req.getParameter("id_tournament");
        String idPlayer1 = req.getParameter("id_player1");
        String idPlayer2 = req.getParameter("id_player2");

        String tName = "Torneo Desconocido";
        String p1Name = "Jugador 1";
        String p2Name = "Jugador 2";

        try {
            if (idTournament != null && idPlayer1 != null && idPlayer2 != null) {
                tName = BMatchData.getTournamentName(connection, Integer.parseInt(idTournament));
                p1Name = BMatchData.getPlayerUsername(connection, Integer.parseInt(idPlayer1));
                p2Name = BMatchData.getPlayerUsername(connection, Integer.parseInt(idPlayer2));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        toClient.println(Utils.header("Consola de Puntuacion", req));
        
        toClient.println("<div class='container'>");
        toClient.println("<h2 style='text-align:center; color:#1a4f2c;'>Partido en Directo</h2>");
        toClient.println("<p style='text-align:center;'>Torneo: " + tName + " | " + p1Name + " vs " + p2Name + "</p>");
        
        toClient.println("<div style='display:flex; justify-content:space-around; margin: 30px 0;'>");
        
        toClient.println("  <div class='info-box' style='text-align:center;'>");
        toClient.println("    <h3>" + p1Name + "</h3>");
        toClient.println("    <h1 id='puntos-j1' style='font-size: 48px; color: #2e8b57; margin: 10px 0;'>0</h1>");
        toClient.println("    <button type='button' class='btn' onclick='sumarPunto(1)' style='margin-right: 5px;'>+1</button>");
        toClient.println("    <button type='button' class='btn' onclick='restarPunto(1)' style='background-color: #f0ad4e;'>-1</button>");
        toClient.println("  </div>");

        toClient.println("  <div class='info-box' style='text-align:center;'>");
        toClient.println("    <h3>" + p2Name + "</h3>");
        toClient.println("    <h1 id='puntos-j2' style='font-size: 48px; color: #2e8b57; margin: 10px 0;'>0</h1>");
        toClient.println("    <button type='button' class='btn' onclick='sumarPunto(2)' style='margin-right: 5px;'>+1</button>");
        toClient.println("    <button type='button' class='btn' onclick='restarPunto(2)' style='background-color: #f0ad4e;'>-1</button>");
        toClient.println("  </div>");
        toClient.println("</div>");

        toClient.println("<div id='mensaje-ajax' style='text-align:center; margin-bottom: 20px; display:none;'></div>");

        toClient.println("<div class='text-center'>");
        toClient.println("  <button id='btn-finalizar' class='btn' style='background-color: #d9534f;' onclick='finalizarPartido(" + idTournament + ", " + idPlayer1 + ", " + idPlayer2 + ")'>Finalizar y Guardar Resultado</button>");
        toClient.println("</div>");

        toClient.println("<script src='js/arbitraje.js'></script>");

        toClient.println("</div>"); 
        toClient.println(Utils.footer());
        toClient.close();
    }
}