function Territory(data) {
    this.path = data.path;
};

Territory.prototype.draw = function(ctx) {
    ctx.fillStyle = "rgba(104, 58, 174, 0.25)";

    var path = new Path2D();
    path.moveTo(this.path[0], this.path[1]);
    for (var i = 2; i < this.path.length; i+=2) {
        path.lineTo(this.path[i], this.path[i+1]);
    };
    ctx.fill(path);
};

function Deposit(data) {
    Territory.call(this, data);
};

Deposit.prototype = Object.create(Territory.prototype);

Deposit.prototype.constructor = Deposit;
