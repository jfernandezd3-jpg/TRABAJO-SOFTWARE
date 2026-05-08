// PAUL AYALA
// AJAX para editar torneos (PEditTournamentServlet GET -> PEditTournamentServlet POST)

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('editForm').addEventListener('submit', function(e) {
        e.preventDefault();
        guardarCambios(e.target);
    });
});

function guardarCambios(form) {
    var btn = document.getElementById('btnGuardar');
    var msg = document.getElementById('mensaje');

    var params = new URLSearchParams({
        id:              form.querySelector('[name=id]').value,
        tournamentName:  form.querySelector('[name=tournamentName]').value,
        modality:        form.querySelector('[name=modality]').value,
        dateTime:        form.querySelector('[name=dateTime]').value,
        location:        form.querySelector('[name=location]').value,
        entryPrice:      form.querySelector('[name=entryPrice]').value,
        prizes:          form.querySelector('[name=prizes]').value,
        rules:           form.querySelector('[name=rules]').value,
        maxParticipants: form.querySelector('[name=maxParticipants]').value,
        latitude:        form.querySelector('[name=latitude]').value,
        longitude:       form.querySelector('[name=longitude]').value,
        format:          'json'
    });

    btn.disabled = true;
    btn.textContent = 'Guardando...';
    msg.style.display = 'none';

    fetch('PEditTournamentServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
    })
    .then(function(r) { return r.json(); })
    .then(function(data) {
        msg.style.display = 'block';
        if (data.success) {
            msg.style.background = '#e8f5e9';
            msg.style.color = '#2e7d32';
            msg.style.border = '1px solid #a5d6a7';
            msg.textContent = '\u2713 ' + data.message;
        } else {
            msg.style.background = '#ffebee';
            msg.style.color = '#c62828';
            msg.style.border = '1px solid #ef9a9a';
            msg.textContent = '\u2717 ' + data.message;
        }
    })
    .catch(function() {
        msg.style.display = 'block';
        msg.style.background = '#ffebee';
        msg.style.color = '#c62828';
        msg.style.border = '1px solid #ef9a9a';
        msg.textContent = '\u2717 Error de conexion con el servidor.';
    })
    .finally(function() {
        btn.disabled = false;
        btn.textContent = 'Guardar Cambios';
    });
}
