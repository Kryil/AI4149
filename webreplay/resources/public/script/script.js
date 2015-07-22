$(document).ready(function() {

  var canvas = document.getElementById("gamefield");
  var socket = new WebSocket("ws://localhost:8080/ws", "AI4149.1");
  var gameIdentifier = location.search.split("id=")[1];

  socket.onopen = function() {
    socket.send(JSON.stringify({game: gameIdentifier}));
  };

  var gameCanvas = new GameCanvas(canvas);

  gameCanvas.initialize("images/metal-tileable.png");

  socket.onmessage = function(evt) {
    var gameData = new GameData(JSON.parse(evt.data));
    gameCanvas.draw(gameData);
  };
});
