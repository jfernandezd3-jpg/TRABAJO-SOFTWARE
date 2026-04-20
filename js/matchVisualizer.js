window.onload = function() {
    console.log("JS cargado. Modality:", modality, "Matches:", matches, "Bracket:", bracket);

    let area = document.getElementById("matchArea");
    const mod = modality.toLowerCase();

    // ============================
    // MUS / POKER → solo mesas
    // ============================
    if (mod === "mus" || mod === "poker") {
        matches.forEach((m) => {
            area.innerHTML += `<div class='table'>${m}</div>`;
        });
        return; // no hay árbol
    }

    // ============================
    // CHESS → solo bracket (sin duplicados)
    // ============================
    if (mod === "chess" && bracket && bracket.length > 0) {

        bracket.forEach((round, i) => {
            area.innerHTML += `<h4>Ronda ${i+1}</h4>`;
            round.forEach(match => {
                area.innerHTML += `<div class='match'>${match}</div>`;
            });
        });
    }
};
