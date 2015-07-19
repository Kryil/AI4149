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

        drawWalls(data.gamefield.walls);
        drawResources(data.gamefield.resources);
        drawUnits(data.units, "self");
        drawUnits(data.enemyUnits, "enemies");
    };

    function drawWalls(walls) {
        ctx.fillStyle = "black";
        walls.forEach(function(el, i, array) {
            ctx.fillRect(el[1]*100, el[0]*100, 1*100, 1*100);
        });
    };

    function drawResources(resources) {
        ctx.fillStyle = "rgba(104, 58, 174, 0.25)";
        resources.forEach(function(el, i, array) {
            ctx.fillRect(el[1]*100, el[0]*100, 1*100, 1*100);
        });
    };

    function drawUnits(units, status) {
        ctx.fillStyle = status === "enemies" ? "#AA0000" : "#99FF99";
        units.forEach(function(el, i, array) {
            ctx.fillRect(el.location[1]*100, el.location[0]*100, 1*100, 1*100);
        });
    };

    return {
        initial: initialize,
        fromJSON: drawFromJSON
    };
})();
