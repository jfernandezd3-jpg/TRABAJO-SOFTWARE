import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Utils {
    
    public static String header(String title, HttpServletRequest request) {
        StringBuilder str = new StringBuilder();
        
        str.append("<!DOCTYPE html>\n");
        str.append("<html lang=\"es\">\n<head>\n");
        str.append("    <meta charset=\"UTF-8\">\n");
        str.append("    <title>").append(title).append(" - TournamentHub</title>\n");
        str.append("    <link rel=\"stylesheet\" href=\"style.css\">\n");
        str.append("</head>\n<body>\n");
        
        str.append("    <div class=\"topbar\">\n");
        str.append("        <span class=\"brand\">&#127942; TournamentHub</span>\n");
        str.append("        <div>\n");
        
        // Comprobamos si hay una sesion activa
        HttpSession session = request.getSession(false); 
        if (session != null && session.getAttribute("userEmail") != null) {
            // USUARIO LOGUEADO
            String email = (String) session.getAttribute("userEmail");
            str.append("            <a href=\"home.html\">Mi Panel</a>\n");
            str.append("            <a href=\"TournamentList\">Ver Torneos</a>\n");
            str.append("            <a href=\"BTournamentMap\">Mapa</a>\n");
            str.append("            <span style=\"margin-left: 18px; color: #f39c12; font-weight: bold;\">").append(email).append("</span>\n");
            str.append("            <a href=\"BLogoutServlet\" class=\"logout\">Cerrar Sesi&oacute;n</a>\n");
        } else {
            // USUARIO NO LOGUEADO
            str.append("            <a href=\"index.html\">Inicio</a>\n");
            str.append("            <a href=\"TournamentList\">Ver Torneos</a>\n");
            str.append("            <a href=\"BTournamentMap\">Mapa</a>\n");
            str.append("            <a href=\"login.html\">Login</a>\n");
            str.append("            <a href=\"register.html\">Registro</a>\n");
        }
        
        str.append("        </div>\n");
        str.append("    </div>\n");
        
        str.append("    <div style=\"width: 100%; max-width: 1000px; margin: 0 auto;\">\n");
        str.append("        <h2 style=\"text-align: center; color: #1a4f2c;\">").append(title).append("</h2>\n");
        
        return str.toString();
    }

    public static String footer() {
        return "    </div>\n</body>\n</html>\n";
    }
}