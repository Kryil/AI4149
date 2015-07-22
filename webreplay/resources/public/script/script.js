$(document).ready(function() {
    var canvas = document.getElementById("gamefield");

    gameField.draw.initial(canvas, "images/metal-tileable.png");

    $.getJSON("script/dummydata.json", function(data) {
        gameField.draw.fromJSON(data);
    });

    var ws = new WebSocket("ws://localhost:8080/ws", "AI4149.1");

    ws.onmessage = function(evt) {
      console.log(evt.data);
    };

    ws.onopen = function() {
      ws.send("hello");
    }
});
