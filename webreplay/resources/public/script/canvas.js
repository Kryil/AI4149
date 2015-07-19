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
        ctx.drawImage(background, 0, 0, canvas.width, canvas.height);

        drawResources(data.gamefield.resources);
        drawWalls(data.gamefield.walls);
        drawUnits(data.units, "self");
        drawUnits(data.enemyUnits, "enemies");
    };

    function drawWalls(walls) {
        ctx.strokeStyle = "black";
        ctx.lineWidth = 50;
        walls.forEach(function(el, i, array) {
            var path = new Path2D();
            path.moveTo(el[0]*100, el[1]*100);
            for (var i = 2; i < el.length; i+=2) {
                path.lineTo(el[i]*100, el[i+1]*100);
            };
            ctx.stroke(path);
        });
    };

    function drawResources(resources) {
        ctx.fillStyle = "rgba(104, 58, 174, 0.25)";
        resources.forEach(function(el, i, array) {
            var path = new Path2D();
            path.moveTo(el[0]*100, el[1]*100);
            for (var i = 2; i < el.length; i+=2) {
                path.lineTo(el[i]*100, el[i+1]*100);
            };
            ctx.fill(path);
        });
    };

    function drawUnits(units, status) {
        ctx.fillStyle = status === "enemies" ? "#AA0000" : "#99FF99";
        units.forEach(function(el, i, array) {
            ctx.fillRect(el.location[0]*100, el.location[1]*100, 1*100, 1*100);
        });
    };

    return {
        initial: initialize,
        fromJSON: drawFromJSON
    };
})();
