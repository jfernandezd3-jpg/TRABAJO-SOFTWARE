
// PAUL AYALA

// AJAX para la creación de torneos (insertTournament.html -> PCreateTournamentServlet)

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('createForm').addEventListener('submit', function(e) {
        e.preventDefault();
        crearTorneo(e.target);
    });
});

function crearTorneo(form) {
    var btn = document.getElementById('btnSubmit');
    var msg = document.getElementById('mensaje');

    var params = new URLSearchParams({
        tournamentName:  form.tournamentName.value,
        modality:        form.modality.value,
        dateTime:        form.dateTime.value,
        address:         form.address.value,
        rules:           form.rules.value,
        maxParticipants: form.maxParticipants.value,
        entryPrice:      form.entryPrice.value,
        prizes:          form.prizes.value,
        latitude:        form.latitude.value,
        longitude:       form.longitude.value,
        format:          'json'
    });

    btn.disabled = true;
    btn.textContent = 'Creando...';
    msg.style.display = 'none';

    fetch('PCreateTournamentServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
    })
    .then(function(r) { return r.json(); })
    .then(function(data) {
        msg.style.display = 'block';
        if (data.success) {
            msg.className = 'ok';
            msg.textContent = '\u2713 ' + data.message;
            form.reset();
        } else {
            msg.className = 'err';
            msg.textContent = '\u2717 ' + data.message;
        }
    })
    .catch(function() {
        msg.style.display = 'block';
        msg.className = 'err';
        msg.textContent = '\u2717 Error de conexión con el servidor.';
    })
    .finally(function() {
        btn.disabled = false;
        btn.textContent = 'Crear Torneo';
    });
}
