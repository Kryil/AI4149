function Obstacle(data) {
    this.path = data.path;
};

Obstacle.prototype = Object.create(Shape.prototype);

Obstacle.prototype.draw = function(ctx) {
    ctx.strokeStyle = "black";
    ctx.lineWidth = 35;
    ctx.stroke(this.drawPathFrom(this.path));
};

function Wall(data) {
    Obstacle.call(this, data);
};

Wall.prototype = Object.create(Obstacle.prototype);

Wall.prototype.constructor = Wall;
