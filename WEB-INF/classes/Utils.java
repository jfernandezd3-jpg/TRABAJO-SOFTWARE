import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Utils {
    
    // FIJATE AQUI: Ahora pide el request como segundo parametro
    public static String header(String title, HttpServletRequest request) {
        StringBuilder str = new StringBuilder();
        
        str.append("<!DOCTYPE html>\n");
        str.append("<html lang=\"es\">\n<head>\n");
        str.append("    <meta charset=\"UTF-8\">\n");
        str.append("    <title>").append(title).append(" - TournamentHub</title>\n");
        str.append("    <link rel='stylesheet' href='style.css'>\n");
        str.append("</head>\n<body class='centered-page'>\n");
        str.append("    <div class='card' style='max-width: 800px; width: 100%;'>\n");
        
        // --- INICIO DE LA BARRA DE NAVEGACION DINAMICA ---
        str.append("        <div style='text-align: center; margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid #ccc;'>\n");
        str.append("            <a href='index.html' style='margin-right: 15px;'>Home</a>\n");
        str.append("            <a href='TournamentList' style='margin-right: 15px;'>Ver Torneos</a>\n");
        
        // Comprobamos si hay una sesion activa
        HttpSession session = request.getSession(false); 
        if (session != null && session.getAttribute("userEmail") != null) {
            // USUARIO LOGUEADO
            String email = (String) session.getAttribute("userEmail");
            str.append("            <span style='margin-right: 15px; color: #0078ff; font-weight: bold;'>Usuario: ").append(email).append("</span>\n");
            str.append("            <a href='BLogoutServlet' style='color: red;'>Cerrar Sesion</a>\n");
        } else {
            // USUARIO NO LOGUEADO
            str.append("            <a href='login.html' style='margin-right: 15px;'>Login</a>\n");
            str.append("            <a href='register.html'>Registro</a>\n");
        }
        str.append("        </div>\n");
        // --- FIN DE LA BARRA DE NAVEGACION ---
        
        str.append("        <h2 align=\"center\">").append(title).append("</h2>\n");
        
        return str.toString();
    }

    public static String footer() {
        return "    </div>\n</body>\n</html>\n";
    }
}