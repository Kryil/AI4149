$(document).ready(function() {

  var canvas = document.getElementById("gamefield");
  var socket = new WebSocket("ws://localhost:8080/ws", "AI4149.1");
  var gameIdentifier = location.search.split("id=")[1];

  gameField.draw.initial(canvas, "images/metal-tileable.png");

  socket.onopen = function() {
    socket.send(JSON.stringify({game: gameIdentifier}));
  };

  socket.onmessage = function(evt) {
    gameField.draw.fromJSON(JSON.parse(evt.data));
  };
});
