
// JAIME FERNANDEZ DE BETOÑO

let marcadorJ1 = 0;
let marcadorJ2 = 0;

function sumarPunto(jugador) {
    if (jugador === 1) {
        marcadorJ1++; 
        document.getElementById('puntos-j1').innerText = marcadorJ1;
    } else {
        marcadorJ2++; 
        document.getElementById('puntos-j2').innerText = marcadorJ2;
    }
}

function restarPunto(jugador) {
    if (jugador === 1 && marcadorJ1 > 0) {
        marcadorJ1--; 
        document.getElementById('puntos-j1').innerText = marcadorJ1;
    } else if (jugador === 2 && marcadorJ2 > 0) {
        marcadorJ2--; 
        document.getElementById('puntos-j2').innerText = marcadorJ2;
    }
}

function finalizarPartido(idTournament, idPlayer1, idPlayer2) {
    if(confirm('¿Estas seguro de finalizar el partido y guardar los resultados?')) {
        
        document.getElementById('btn-finalizar').disabled = true;
        document.getElementById('btn-finalizar').innerText = 'Guardando...';
        
        var params = 'id_tournament=' + idTournament + '&id_player1=' + idPlayer1 + '&id_player2=' + idPlayer2 + '&points_p1=' + marcadorJ1 + '&points_p2=' + marcadorJ2;
        
        var request = new XMLHttpRequest();
        request.open('POST', 'BMatchScoreCreate', true);
        request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        
        request.onload = function() {
            var mensajeDiv = document.getElementById('mensaje-ajax');
            mensajeDiv.style.display = 'block';
            
            if (request.status >= 200 && request.status < 400) {
                var data = JSON.parse(request.responseText);
                if(data.status === 'ok') {
                    mensajeDiv.innerHTML = '<h3 style="color:green;">¡Éxito! ' + data.message + '</h3><a href="home.html" class="btn" style="display:inline-block; width:auto; margin-top:10px;">Volver al Panel</a>';
                    document.getElementById('btn-finalizar').style.display = 'none';
                } else {
                    mensajeDiv.innerHTML = '<h3 style="color:red;">Error: ' + data.message + '</h3>';
                    document.getElementById('btn-finalizar').disabled = false;
                    document.getElementById('btn-finalizar').innerText = 'Reintentar Guardar';
                }
            } else {
                mensajeDiv.innerHTML = '<h3 style="color:red;">Error del servidor</h3>';
            }
        };
        
        request.send(params);
    }
}