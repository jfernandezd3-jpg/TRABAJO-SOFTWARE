import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

@SuppressWarnings("serial")
public class BMatchScoreConsole extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        res.setContentType("text/html; charset=UTF-8");
        res.setCharacterEncoding("UTF-8");
        PrintWriter toClient = res.getWriter();
        
        String idTournament = req.getParameter("id_tournament");
        String idPlayer1 = req.getParameter("id_player1");
        String idPlayer2 = req.getParameter("id_player2");

        toClient.println(Utils.header("Consola de Puntuacion", req));
        
        toClient.println("<div class='container'>");
        toClient.println("<h2 style='text-align:center; color:#1a4f2c;'>Partido en Directo</h2>");
        toClient.println("<p style='text-align:center;'>Torneo: " + idTournament + " | Jugador " + idPlayer1 + " vs Jugador " + idPlayer2 + "</p>");
        
        toClient.println("<div style='display:flex; justify-content:space-around; margin: 30px 0;'>");
        
        // --- JUGADOR 1 ---
        toClient.println("  <div class='info-box' style='text-align:center;'>");
        toClient.println("    <h3>Jugador 1</h3>");
        toClient.println("    <h1 id='puntos-j1' style='font-size: 48px; color: #2e8b57; margin: 10px 0;'>0</h1>");
        toClient.println("    <button type='button' class='btn' onclick='sumarPunto(1)' style='margin-right: 5px;'>+1</button>");
        toClient.println("    <button type='button' class='btn' onclick='restarPunto(1)' style='background-color: #f0ad4e;'>-1</button>");
        toClient.println("  </div>");

        // --- JUGADOR 2 ---
        toClient.println("  <div class='info-box' style='text-align:center;'>");
        toClient.println("    <h3>Jugador 2</h3>");
        toClient.println("    <h1 id='puntos-j2' style='font-size: 48px; color: #2e8b57; margin: 10px 0;'>0</h1>");
        toClient.println("    <button type='button' class='btn' onclick='sumarPunto(2)' style='margin-right: 5px;'>+1</button>");
        toClient.println("    <button type='button' class='btn' onclick='restarPunto(2)' style='background-color: #f0ad4e;'>-1</button>");
        toClient.println("  </div>");
        
        toClient.println("</div>");

        toClient.println("<div class='text-center'>");
        toClient.println("  <button class='btn' style='background-color: #d9534f;' onclick='finalizarPartido()'>Finalizar y Guardar Resultado</button>");
        toClient.println("</div>");

        toClient.println("<form id='form-partido' action='BMatchScoreCreate' method='POST' style='display: none;'>");
        toClient.println("  <input type='hidden' name='id_tournament' value='" + idTournament + "'>");
        toClient.println("  <input type='hidden' name='id_player1' value='" + idPlayer1 + "'>");
        toClient.println("  <input type='hidden' name='id_player2' value='" + idPlayer2 + "'>");
        toClient.println("  <input type='hidden' id='input-puntos-j1' name='points_p1' value='0'>");
        toClient.println("  <input type='hidden' id='input-puntos-j2' name='points_p2' value='0'>");
        toClient.println("</form>");

        toClient.println("<script>");
        toClient.println("  let marcadorJ1 = 0;");
        toClient.println("  let marcadorJ2 = 0;");
        
        toClient.println("  function sumarPunto(jugador) {");
        toClient.println("      if (jugador === 1) {");
        toClient.println("          marcadorJ1++;");
        toClient.println("          document.getElementById('puntos-j1').innerText = marcadorJ1;");
        toClient.println("      } else {");
        toClient.println("          marcadorJ2++;");
        toClient.println("          document.getElementById('puntos-j2').innerText = marcadorJ2;");
        toClient.println("      }");
        toClient.println("  }");

        // Restar puntos (sin bajar de 0)
        toClient.println("  function restarPunto(jugador) {");
        toClient.println("      if (jugador === 1 && marcadorJ1 > 0) {");
        toClient.println("          marcadorJ1--;");
        toClient.println("          document.getElementById('puntos-j1').innerText = marcadorJ1;");
        toClient.println("      } else if (jugador === 2 && marcadorJ2 > 0) {");
        toClient.println("          marcadorJ2--;");
        toClient.println("          document.getElementById('puntos-j2').innerText = marcadorJ2;");
        toClient.println("      }");
        toClient.println("  }");

        toClient.println("  function finalizarPartido() {");
        toClient.println("      if(confirm('\\u00BFEstas seguro de finalizar el partido y guardar los resultados?')) {");
        toClient.println("          document.getElementById('input-puntos-j1').value = marcadorJ1;");
        toClient.println("          document.getElementById('input-puntos-j2').value = marcadorJ2;");
        toClient.println("          document.getElementById('form-partido').submit();");
        toClient.println("      }");
        toClient.println("  }");
        toClient.println("</script>");

        toClient.println("</div>"); 
        toClient.println(Utils.footer());
        toClient.close();
    }
}