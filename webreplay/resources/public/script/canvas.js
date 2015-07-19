$(document).ready(function() {
    var canvas = document.getElementById("gamefield");

    gameField.draw.initial(canvas, "images/metal-tileable.png");

    $.getJSON("script/dummydata.json", function(data) {
        gameField.draw.fromJSON(data);
    });
});

var gameField = {};

gamefield.drawList = (function() {
    var items = [];

    function pushItem(item) {
        items.push(item);
    };

    function drawItems(ctx) {
        items.forEach(function(el) {
            el.draw(ctx);
        });
        items = [];
    };

    return {
        push: pushItem,
        draw: drawItems
    };
})();

gameField.draw = (function(drawList) {
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
        setCanvasOptions(data.gamefield.size);
        drawCanvasBackground();

        drawResources(data.gamefield.resources);
        drawWalls(data.gamefield.walls);
        addToDrawList(data.units, "self");
        addToDrawList(data.enemyUnits, "enemy");

        drawList.draw(ctx);
    };

    function setCanvasOptions(size) {
        canvas.width = size[0];
        canvas.height = size[1];
    };

    function drawCanvasBackground() {
        ctx.drawImage(background, 0, 0, canvas.width, canvas.height);
    };

    function drawWalls(walls) {
        ctx.strokeStyle = "black";
        ctx.lineWidth = 35;
        walls.forEach(function(el, i, array) {
            var path = new Path2D();
            path.moveTo(el[0], el[1]);
            for (var i = 2; i < el.length; i+=2) {
                path.lineTo(el[i], el[i+1]);
            };
            ctx.stroke(path);
        });
    };

    function drawResources(resources) {
        ctx.fillStyle = "rgba(104, 58, 174, 0.25)";
        resources.forEach(function(el, i, array) {
            var path = new Path2D();
            path.moveTo(el[0], el[1]);
            for (var i = 2; i < el.length; i+=2) {
                path.lineTo(el[i], el[i+1]);
            };
            ctx.fill(path);
        });
    };

    function addToDrawList(items, status) {
        items.forEach(function(el, i, array) {
            switch(el.type) {
                case "Commander":
                    drawList.push(new Commander(el, status));
                    break;
                case "Harvester":
                    drawList.push(new Harvester(el, status));
                    break;
                case "Squaddy":
                    drawList.push(new Squaddy(el, status));
                    break;
                case "Stronghold":
                    drawList.push(new Stronghold(el, status));
                    break;
            };
        });
    };

    return {
        initial: initialize,
        fromJSON: drawFromJSON
    };
})(gamefield.drawList);
