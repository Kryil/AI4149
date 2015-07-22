$(document).ready(function() {

  var socket = new WebSocket("ws://localhost:8080/ws", "AI4149.1");
  var gameIdentifier = location.search.split("id=")[1];
  var gameCanvas = new GameCanvas(document.getElementById("gamefield"));

  gameCanvas.initialize("images/metal-tileable.png");

  socket.onopen = function() {
    socket.send(JSON.stringify({gameId: gameIdentifier}));
  };

  socket.onmessage = function(evt) {
    var gameData = new GameData(JSON.parse(evt.data));
    gameCanvas.draw(gameData);
  };
});
