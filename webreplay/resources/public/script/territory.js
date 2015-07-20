function Territory(data) {
    this.path = data.path;
};

Territory.prototype = Object.create(Shape.prototype);

Territory.prototype.draw = function(ctx) {
    ctx.fillStyle = "rgba(104, 58, 174, 0.25)";
    ctx.fill(this.drawPathFrom(this.path));
};

function Deposit(data) {
    Territory.call(this, data);
};

Deposit.prototype = Object.create(Territory.prototype);

Deposit.prototype.constructor = Deposit;
