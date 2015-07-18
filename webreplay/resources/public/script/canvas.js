$(document).ready(function() {
    var canvas = document.getElementById("gamefield");
    var ctx = canvas.getContext("2d");

    gameField.draw.initial(ctx, "images/metal-tileable.png");
});

var gameField = {};

gameField.draw = (function() {
    var background = new Image();

    function initialize(ctx, imageUrl) {
        background.onload = function() {
            ctx.drawImage(background, 0, 0);
        };
        background.src = imageUrl;
    };

    return {
        initial: initialize
    };
})();
