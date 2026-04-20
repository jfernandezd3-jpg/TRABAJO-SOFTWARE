window.onload = function() {
    let area = document.getElementById("matchArea");

    if (modality === "chess") {
        area.innerHTML = "<h3>Bracket (Chess)</h3>";
        matches.forEach((m, i) => {
            area.innerHTML += `<div class='match'>Partida ${i+1}: ${m}</div>`;
        });
    }

    if (modality === "mus") {
        area.innerHTML = "<h3>Mesas de 4 jugadores (Mus)</h3>";
        matches.forEach((m) => {
            area.innerHTML += `<div class='table'>${m}</div>`;
        });
    }

    if (modality === "poker") {
        area.innerHTML = "<h3>Mesas de 8 jugadores (Poker)</h3>";
        matches.forEach((m) => {
            area.innerHTML += `<div class='table'>${m}</div>`;
        });
    }
};
