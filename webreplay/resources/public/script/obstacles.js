function Obstacle(data) {
    this.path = data.path;
};

Obstacle.prototype.draw = function(ctx) {
    ctx.strokeStyle = "black";
    ctx.lineWidth = 35;

    var path = new Path2D();
    path.moveTo(this.path[0], this.path[1]);
    for (var i = 2; i < this.path.length; i+=2) {
        path.lineTo(this.path[i], this.path[i+1]);
    };
    ctx.stroke(path);
};

function Wall(data) {
    Obstacle.call(this, data);
};

Wall.prototype = Object.create(Obstacle.prototype);

Wall.prototype.constructor = Wall;
