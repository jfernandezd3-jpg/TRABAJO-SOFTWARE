function cargarPopup(marker, idTorneo, map) {
    marker.on('click', function(e) {
        var popup = L.popup().setLatLng(e.latlng).setContent('<i>Cargando info...</i>').openOn(map);
        
        var request = new XMLHttpRequest();
        request.open('GET', 'BTournamentPopupAjax?id=' + idTorneo, true);
        
        request.onload = function() {
            if (request.status >= 200 && request.status < 400) {
                var resp = request.responseText;
                var data = JSON.parse(resp);
                
                if(data.error) {
                    popup.setContent('<b style="color:red;">Error: ' + data.error + '</b>');
                } else {
                    var html = "<div style='text-align: center;'>" + 
                               "<b style='color: #1a4f2c; font-size: 15px;'>" + data.name + "</b><br>" +
                               "<span style='color: #666;'>Lugar: " + data.location + "</span><br>" +
                               "<span style='font-weight: bold; color: #f39c12;'>Precio: " + data.price + " &euro;</span><br><br>" +
                               "<a href='BTournamentInfo?id=" + data.id + "' class='btn' style='padding: 6px 12px; font-size: 13px; color: white !important; text-decoration: none;'>Ver Detalles</a>" +
                               "</div>";
                    popup.setContent(html);
                }
            } else {
                popup.setContent('<b style="color:red;">Error del servidor</b>');
            }
        };
        
        request.send();
    });
}