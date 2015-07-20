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
        items.forEach(function(item) {
            item.draw(ctx);
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
        background.src = imageUrl;
        canvas = newCanvas;
        ctx = canvas.getContext("2d");
        ctx.drawImage(background, 0, 0);
    };

    function drawFromJSON(data) {
        setCanvasOptions(data.gamefield.size);
        drawCanvasBackground();

        addToDrawList(data.gamefield.territory);
        addToDrawList(data.gamefield.obstacles);
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

    function addToDrawList(items, status) {
        items.forEach(function(item) {
            drawList.push(objectWithType(item, status));
        });
    };

    function objectWithType(item, status) {
        switch(item.type) {
            case "Deposit": return new Deposit(item);
            case "Wall": return new Wall(item);
            case "Commander": return new Commander(item, status);
            case "Harvester": return new Harvester(item, status);
            case "Squaddy": return new Squaddy(item, status);
            case "Stronghold": return new Stronghold(item, status);
        };
    };

    return {
        initial: initialize,
        fromJSON: drawFromJSON
    };
})(gamefield.drawList);
