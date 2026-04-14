public class Utils {
    
    public static String header(String title) {
        StringBuilder str = new StringBuilder();
        
        str.append("<!DOCTYPE html>\n");
        str.append("<html lang=\"en\">\n");
        str.append("<head>\n");
        str.append("    <meta charset=\"UTF-8\">\n");
        str.append("    <title>").append(title).append(" - TournamentHub</title>\n");
        
        // Enlace al archivo CSS (asegúrate de que la ruta css/style.css sea correcta)
        str.append("    <link rel='stylesheet' href='style.css'>\n");
        str.append("</head>\n");
        
        // Añadimos un contenedor genérico para que se vea bien centrado y con estilo
        str.append("<body class='centered-page'>\n");
        str.append("    <div class='card' style='max-width: 800px; width: 100%;'>\n");
        
        // Menú de navegación adaptado a TournamentHub
        str.append("        <div style='text-align: center; margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid #ccc;'>\n");
        str.append("            <a href='index.html' style='margin-right: 15px;'>Home</a>\n");
        str.append("            <a href='search.html' style='margin-right: 15px;'>Buscar Torneos</a>\n");
        str.append("            <a href='login.html' style='margin-right: 15px;'>Login</a>\n");
        str.append("            <a href='register.html'>Registro</a>\n");
        str.append("        </div>\n");
        
        // Título de la página
        str.append("        <h2 align=\"center\">").append(title).append("</h2>\n");
        
        return str.toString();
    }

    public static String footer() {
        StringBuilder str = new StringBuilder();
        // Cerramos el div 'card' y el body
        str.append("    </div>\n");
        str.append("</body>\n");
        str.append("</html>\n");
        return str.toString();
    }
}