$(document).ready(function() {
    var canvas = document.getElementById("gamefield");

    gameField.draw.initial(canvas, "images/metal-tileable.png");

    $.getJSON("script/dummydata.json", function(data) {
        gameField.draw.fromJSON(data);
    });
});

var gameField = {};

gameField.draw = (function() {
    var background = new Image();
    var canvas;
    var ctx;

    function initialize(newCanvas, imageUrl) {
        canvas = newCanvas;
        ctx = canvas.getContext("2d");
        background.src = imageUrl;
        ctx.drawImage(background, 0, 0);
    };

    function drawFromJSON(data) {
        canvas.width = data.gamefield.size[0]*100;
        canvas.height = data.gamefield.size[1]*100;
        ctx.drawImage(background, 0, 0);

        data.gamefield.walls.forEach(function(el, i, array) {
            ctx.fillRect(el[1]*100, el[0]*100, 1*100, 1*100);
        });
    };

    return {
        initial: initialize,
        fromJSON: drawFromJSON
    };
})();
