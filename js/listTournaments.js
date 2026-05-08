// PAUL AYALA
// AJAX para listar torneos del organizador (gestion_organizador.html -> PListOrganizerTournamentsServlet)

function cargarMisTorneos() {
    var seccion = document.getElementById('seccionTorneos');
    var cuerpo  = document.getElementById('tablaCuerpo');
    var btn     = document.getElementById('btnMisTorneos');

    // Mostrar sección y estado de carga
    seccion.style.display = 'block';
    cuerpo.innerHTML = '<tr><td colspan="5" style="text-align:center; padding:20px; color:#888;">Cargando...</td></tr>';
    btn.textContent = 'Actualizar mis torneos';

    fetch('PListOrganizerTournamentsServlet?format=json', {
        method: 'GET'
    })
    .then(function(r) { return r.json(); })
    .then(function(data) {
        cuerpo.innerHTML = '';

        if (!data.success) {
            cuerpo.innerHTML = '<tr><td colspan="5" style="text-align:center; color:#c62828; padding:16px;">\u2717 ' + data.message + '</td></tr>';
            return;
        }

        if (data.torneos.length === 0) {
            cuerpo.innerHTML = '<tr><td colspan="5" style="text-align:center; color:#888; padding:16px;">No tienes torneos creados todavía. <a href="insertTournament.html">¡Crea el primero!</a></td></tr>';
            return;
        }

        data.torneos.forEach(function(t) {
            var fila = document.createElement('tr');
            fila.innerHTML =
                '<td style="padding:10px;">' + t.tournament + '</td>' +
                '<td style="padding:10px;">' + t.modality + '</td>' +
                '<td style="padding:10px;">' + t.tournament_date + '</td>' +
                '<td style="padding:10px;">' + t.location + '</td>' +
                '<td style="padding:10px; text-align:center;">' +
                    '<a href="PEditTournamentServlet?id=' + t.id + '" style="color:#0078ff; font-weight:bold; margin-right:14px;">Editar</a>' +
                    '<a href="PCreateTournamentPosterServlet?id=' + t.id + '" style="color:#28a745; font-weight:bold;">Crear Cartel</a>' +
                '</td>';
            cuerpo.appendChild(fila);
        });

        // Scroll suave hasta la tabla
        seccion.scrollIntoView({ behavior: 'smooth', block: 'start' });
    })
    .catch(function() {
        cuerpo.innerHTML = '<tr><td colspan="5" style="text-align:center; color:#c62828; padding:16px;">\u2717 Error de conexión con el servidor.</td></tr>';
    });
}
