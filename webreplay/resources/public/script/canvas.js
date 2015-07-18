$(document).ready(function() {
    var canvas = document.getElementById("gamefield");
    var ctx = canvas.getContext("2d");

    ctx.fillStyle = "#95a5a6",
    ctx.fillRect(0, 0, 500, 500);

    var background = new Image();
    background.onload = function() {
        ctx.drawImage(background, 0, 0);
    }
    background.src = "images/metal-tileable.png";
});
