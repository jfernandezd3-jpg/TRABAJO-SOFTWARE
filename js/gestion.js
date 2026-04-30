function gestionarInscripcion(uId, tId, accion) {
    // 1. Preparamos los datos para enviar al Servlet
    const params = new URLSearchParams();
    params.append('uId', uId);
    params.append('tId', tId);
    params.append('accion', accion);

    // 2. Llamada AJAX (Fetch)
    fetch('ManageParticipantsServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params
    })
    .then(response => response.text())
    .then(resultado => {
        if (resultado === "OK") {
            // 3. Si el servidor dice OK, borramos la fila de la tabla sin recargar
            const fila = document.getElementById("fila_" + uId + "_" + tId);
            if (fila) {
                fila.style.transition = "all 0.5s";
                fila.style.opacity = "0";
                setTimeout(() => {
                    fila.remove();
                    // Si ya no quedan filas (solo queda la cabecera), podriamos mostrar el mensaje de "No hay datos"
                    verificarTablaVacia();
                }, 500);
            }
        } else {
            alert("Hubo un error al procesar: " + resultado);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert("Fallo en la conexion con el servidor.");
    });
}

function verificarTablaVacia() {
    const tabla = document.getElementById("tablaInscripciones");
    // Si solo queda la fila de cabecera (index 0)
    if (tabla.rows.length === 1) {
        const nuevaFila = tabla.insertRow();
        nuevaFila.innerHTML = "<td colspan='3' style='text-align:center;'>No hay mas inscripciones pendientes.</td>";
    }
}