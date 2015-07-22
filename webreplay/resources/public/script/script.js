$(document).ready(function() {

    var canvas = document.getElementById("gamefield");

    gameField.draw.initial(canvas, "images/metal-tileable.png");

    $.getJSON("script/dummydata.json", function(data) {
        gameField.draw.fromJSON(data);
    });

    var socket = new WebSocket("ws://localhost:8080/ws", "AI4149.1");
    var gameIdentifier = location.search.split("id=")[1];

    socket.onmessage = function(evt) {
        console.log(evt.data);
    };

    socket.onopen = function() {
        socket.send(JSON.stringify({
            game: gameIdentifier
        }));
    }
});
